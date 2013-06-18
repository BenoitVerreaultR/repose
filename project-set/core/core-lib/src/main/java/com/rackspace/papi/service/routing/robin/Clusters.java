package com.rackspace.papi.service.routing.robin;

import com.rackspace.papi.commons.util.Destroyable;
import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.SystemModel;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.healthcheck.HealthCheckEvent;

import java.util.HashMap;
import java.util.Map;

public class Clusters implements Destroyable {
    private final Map<String, ClusterWrapper> domains;
    private final EventService eventService;

    public Clusters(EventService eventService, SystemModel config) {

        this.eventService = eventService;
        this.domains = new HashMap<String, ClusterWrapper>();

        for (Cluster domain: config.getReposeCluster()) {
            domains.put(domain.getId(), new ClusterWrapper(domain));
        }
        for (Cluster domain: config.getServiceCluster()) {
            ClusterWrapper wrapper = new ClusterWrapper(domain);
            eventService.listen(wrapper, HealthCheckEvent.class);
            domains.put(domain.getId(), wrapper);
        }
    }

    public ClusterWrapper getDomain(String id) {
        return domains.get(id);
    }

    @Override
    public void destroy() {
        for (ClusterWrapper wrapper : domains.values()) {
            eventService.squelch(wrapper, HealthCheckEvent.class);
        }
    }
}
