package com.rackspace.papi.service.routing.robin;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.Node;
import com.rackspace.papi.service.event.common.Event;
import com.rackspace.papi.service.event.common.EventListener;
import com.rackspace.papi.service.healthcheck.HealthCheckEvent;
import com.rackspace.papi.service.healthcheck.NodeStatusWrapper;

import java.util.ArrayList;
import java.util.List;

public class ClusterWrapper implements EventListener<HealthCheckEvent, NodeStatusWrapper> {
   private final List<Node> nodes;
   private final List<Node> healthyNodes;
   private int currentIndex = 0;
   
    public ClusterWrapper(Cluster domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain cannot be null");
        }

        this.nodes = domain.getNodes() != null? domain.getNodes().getNode(): new ArrayList<Node>();
        this.healthyNodes = new ArrayList<Node>(nodes);

    }

    public Node getNextNode() {
        synchronized(healthyNodes) {
            return getNode(currentIndex++);
        }
    }

    public Node getNode(int index) {
        return healthyNodes.size() > 0 ? healthyNodes.get(Math.abs(index % healthyNodes.size())): null;
    }

    @Override
    public void onEvent(Event<HealthCheckEvent, NodeStatusWrapper> e) {
        switch (e.type()) {
            case HEALTHY:

                // if the node is already healthy
                for (Node node : healthyNodes) {
                    if (node.getId().equals(e.payload().getNode().getId())) {
                        return;
                    }
                }

                // add the node to healthy nodes
                for (Node node : nodes) {
                    if (node.getId().equals(e.payload().getNode().getId())) {
                        synchronized (healthyNodes) {
                            healthyNodes.add(node);
                        }
                        return;
                    }
                }

                break;
            case UNHEALTHY:

                // remove from healthy nodes
                for (int i = 0; i < healthyNodes.size(); i++) {
                    if (healthyNodes.get(i).getId().equals(e.payload().getNode().getId())) {
                        synchronized (healthyNodes) {
                            healthyNodes.remove(i);
                        }
                        return;
                    }
                }
                break;
        }
    }
}
