package com.rackspace.repose.service.configuration.servlet;

import com.rackspace.papi.model.*;
import com.rackspace.repose.service.configuration.context.SystemModelConfigListener;
import com.rackspace.repose.service.configuration.helper.ClusterHelper;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.helper.DestinationClusterHelper;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;

import java.util.Iterator;

public class ServiceClusterServlet extends BaseServlet<ServiceCluster> {

    @Override
    protected void updateResource(ServiceCluster oldResource, ServiceCluster newResource) {
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel config = systemModelConfigListener.getConfig();
        ReposeCluster reposeCluster = config.getReposeCluster().get(0);

        // update destination node.
        Iterator<DestinationCluster> destinationIterator = reposeCluster.getDestinations().getTarget().iterator();
        while (destinationIterator.hasNext()) {
            DestinationCluster destinationCluster = destinationIterator.next();
            if (destinationCluster.getId().equals(oldResource.getId())) {
                DestinationClusterHelper.update(destinationCluster, newResource);
                break;
            }
        }

        // update service cluster node.
        Iterator<Cluster> clusterIterator = config.getServiceCluster().iterator();
        while (clusterIterator.hasNext()) {
            Cluster cluster = clusterIterator.next();
            if (cluster.getId().equals(oldResource.getId())) {
                ClusterHelper.update(cluster, newResource);
                break;
            }
        }

        systemModelConfigListener.save();
    }

    @Override
    protected void createResource(ServiceCluster resource) {
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel config = systemModelConfigListener.getConfig();
        ReposeCluster reposeCluster = config.getReposeCluster().get(0);

        Cluster cluster = ClusterHelper.fromServiceCluster(resource);
        config.getServiceCluster().add(cluster);

        reposeCluster.getDestinations().getTarget().add(DestinationClusterHelper.fromServiceCluster(resource, cluster));

        systemModelConfigListener.save();
    }

    @Override
    protected void deleteResource(String resourceId) {
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel config = systemModelConfigListener.getConfig();
        ReposeCluster reposeCluster = config.getReposeCluster().get(0);

        // remove destination node.
        Iterator<DestinationCluster> destinationIterator = reposeCluster.getDestinations().getTarget().iterator();
        while (destinationIterator.hasNext()) {
            DestinationCluster destinationCluster = destinationIterator.next();
            if (destinationCluster.getId().equals(resourceId)) {
                destinationIterator.remove();
                break;
            }
        }

        // remove service cluster node.
        Iterator<Cluster> clusterIterator = config.getServiceCluster().iterator();
        while (clusterIterator.hasNext()) {
            Cluster cluster = clusterIterator.next();
            if (cluster.getId().equals(resourceId)) {
                clusterIterator.remove();
                break;
            }
        }

        systemModelConfigListener.save();
    }

}
