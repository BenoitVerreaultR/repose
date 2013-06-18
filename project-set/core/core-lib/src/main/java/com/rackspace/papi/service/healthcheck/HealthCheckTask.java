package com.rackspace.papi.service.healthcheck;

import com.rackspace.papi.commons.util.http.ServiceClientResponse;
import com.rackspace.papi.commons.util.proxy.RequestProxyService;
import com.rackspace.papi.commons.util.thread.RecurringTask;
import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.Node;
import com.rackspace.papi.model.SystemModel;
import com.rackspace.papi.service.event.common.EventService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HealthCheckTask implements RecurringTask {

    private static final Logger LOG = Logger.getLogger(HealthCheckTask.class);
    private final List<NodeStatusWrapper> nodes;
    private final RequestProxyService requestProxyService;
    private final EventService eventService;

    public HealthCheckTask(EventService eventService, RequestProxyService requestProxyService) {
        this.requestProxyService = requestProxyService;
        this.eventService = eventService;
        this.nodes = new ArrayList<NodeStatusWrapper>();
    }

    public void setSystemModel(SystemModel systemModel) {

        List<NodeStatusWrapper> tmpNodes = new ArrayList<NodeStatusWrapper>();

        // get the list of nodes with health check enabled
        for (Cluster cluster : systemModel.getServiceCluster()) {
            for (Node node : cluster.getNodes().getNode()) {
                if (node.getHealthcheck() != null) {
                    tmpNodes.add(new NodeStatusWrapper(node));
                }
            }
        }

        // transfer information if found.
        for (NodeStatusWrapper newNode : tmpNodes) {
            for (NodeStatusWrapper oldNode : nodes) {
                if (newNode.getNode().getId().equals(oldNode.getNode().getId())) {
                    newNode.setActive(oldNode.isActive());
                    newNode.setSuccessCount(oldNode.getSuccessCount());
                    break;
                }
            }
        }

        // swap nodes
        synchronized (nodes) {
            nodes.clear();
            nodes.addAll(tmpNodes);
        }

    }

    @Override
    public void run() {

        synchronized (nodes) {
            for (NodeStatusWrapper node : nodes) {
                try {
                    ServiceClientResponse response = requestProxyService.get(node.getHealthCheckUri(), Collections.<String, String>emptyMap());
                    boolean success = response.getStatusCode() == 200;
                    if (node.isActive() && success) {
                        node.setSuccessCount(node.getSuccessCount() + 1);
                    } else if (node.isActive() && !success) {
                        node.setSuccessCount(0);
                        node.setActive(false);
                        eventService.newEvent(HealthCheckEvent.UNHEALTHY, node);
                    } else if (!node.isActive() && success) {
                        node.setSuccessCount(1);
                        node.setActive(true);
                        eventService.newEvent(HealthCheckEvent.HEALTHY, node);
                    }
                } catch (Exception e) {
                    LOG.error("HealthCheckTask : Failed to request health check for node " + node.getNode().getId());
                    node.setSuccessCount(0);
                    node.setActive(false);
                    eventService.newEvent(HealthCheckEvent.UNHEALTHY, node);
                }
            }
        }

    }

}
