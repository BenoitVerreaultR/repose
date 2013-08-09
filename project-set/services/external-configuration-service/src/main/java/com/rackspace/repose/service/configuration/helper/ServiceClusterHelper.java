package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.DestinationCluster;
import com.rackspace.repose.service.configuration.resource.HealthCheck;
import com.rackspace.repose.service.configuration.resource.Node;
import com.rackspace.repose.service.configuration.resource.Protocol;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;

import java.util.ArrayList;

public class ServiceClusterHelper {

    public static ServiceCluster newServiceCluster() {
        ServiceCluster serviceCluster = new ServiceCluster();
        return serviceCluster;
    }

    public static ServiceCluster fromDestination(DestinationCluster destination) {
        ServiceCluster serviceCluster = newServiceCluster();
        update(serviceCluster, destination);
        return serviceCluster;
    }

    public static void update(ServiceCluster serviceCluster, DestinationCluster destination) {
        serviceCluster.setId(destination.getId());
        serviceCluster.setProtocol(Protocol.valueOf(destination.getProtocol()));
    }

    public static void update(ServiceCluster serviceCluster, Cluster cluster) {
        serviceCluster.setNodes(new ArrayList<Node>(cluster.getNodes().getNode().size()));
        for (com.rackspace.papi.model.Node clusterNode : cluster.getNodes().getNode()) {
            Node node = new Node();
            serviceCluster.getNodes().add(node);
            node.setId(clusterNode.getId());
            node.setHostname(clusterNode.getHostname());
            node.setHttpPort(clusterNode.getHttpPort());
            node.setHttpsPort(clusterNode.getHttpsPort());

            // if no healthcheck, dont
            if (clusterNode.getHealthcheck() == null) {
                continue;
            }

            node.setHealthCheck(new HealthCheck());
            node.getHealthCheck().setProtocol(Protocol.valueOf(clusterNode.getHealthcheck().getProtocol()));
            node.getHealthCheck().setPort(clusterNode.getHealthcheck().getPort());
            node.getHealthCheck().setUri(clusterNode.getHealthcheck().getUri());
        }
    }

}
