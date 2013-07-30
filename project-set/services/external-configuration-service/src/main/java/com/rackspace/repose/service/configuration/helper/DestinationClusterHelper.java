package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.DestinationCluster;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;

public class DestinationClusterHelper {

    public static DestinationCluster newDestination() {
        DestinationCluster destination = new DestinationCluster();
        return destination;
    }

    public static DestinationCluster fromServiceCluster(ServiceCluster serviceCluster, Cluster cluster) {
        DestinationCluster destination = newDestination();
        update(destination, serviceCluster);
        destination.setCluster(cluster);
        return destination;
    }

    public static void update(DestinationCluster destination, ServiceCluster serviceCluster) {
        destination.setId(serviceCluster.getId());
        destination.setProtocol(serviceCluster.getProtocol().name());
        destination.setDefault(false);
    }

}
