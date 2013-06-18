package com.rackspace.papi.service.routing.robin;

import com.rackspace.papi.model.Node;
import com.rackspace.papi.model.SystemModel;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.routing.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("routingService")
public class RoundRobinRoutingService implements RoutingService {
   private Clusters domains;
   private final EventService eventService;

   @Autowired
   public RoundRobinRoutingService(
           @Qualifier("eventManager") EventService eventService) {
       this.eventService = eventService;
   }
   
   @Override
   public void setSystemModel(SystemModel config) {
       if ( this.domains != null) {
           this.domains.destroy();
       }
       this.domains = new Clusters(eventService, config);
   }
   
   @Override
   public Node getRoutableNode(String domainId) {
      ClusterWrapper domain = domains.getDomain(domainId);
      if (domain != null) {
         return domain.getNextNode();
      }
      
      return null;
   }
}
