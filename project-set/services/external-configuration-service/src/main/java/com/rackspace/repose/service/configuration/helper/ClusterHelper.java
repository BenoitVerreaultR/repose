package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.HealthCheck;
import com.rackspace.papi.model.Node;
import com.rackspace.papi.model.NodeList;
import com.rackspace.repose.service.configuration.resource.Protocol;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;

import java.util.ArrayList;

public class ClusterHelper {

    public static Cluster newCluster() {
        Cluster cluster = new Cluster();
        return cluster;
    }

    public static Cluster fromServiceCluster(ServiceCluster serviceCluster) {
        Cluster cluster = newCluster();
        update(cluster, serviceCluster);
        return cluster;
    }

    public static void update(Cluster cluster, ServiceCluster serviceCluster) {
        cluster.setNodes(new NodeList());
        cluster.setId(serviceCluster.getId());
        cluster.getNodes().getNode().clear();
        for (com.rackspace.repose.service.configuration.resource.Node serviceClusterNode : serviceCluster.getNodes()) {
            Node node = new Node();
            cluster.getNodes().getNode().add(node);
            node.setId(serviceClusterNode.getId());
            node.setHostname(serviceClusterNode.getHostname());
            node.setHttpPort(serviceClusterNode.getHttpPort());
            node.setHttpsPort(serviceClusterNode.getHttpsPort());
            node.setHealthcheck(new HealthCheck());
            node.getHealthcheck().setProtocol(serviceClusterNode.getHealthCheck().getProtocol().name());
            node.getHealthcheck().setUri(serviceClusterNode.getHealthCheck().getUri());
            node.getHealthcheck().setPort(serviceClusterNode.getHealthCheck().getPort());
        }
    }

}
