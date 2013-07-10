package org.openrepose.components.routing.servlet;

import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;
import com.rackspace.papi.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.openrepose.components.routing.servlet.config.Target;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrepose.components.routing.servlet.config.DestinationRouterConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class RoutingTaggerTest {

    public static class WhenRoutingToServletContexts {

        private SystemModel powerProxy;
        private NodeList domainNodeList;
        private Node domainNode;
        private DestinationList destinationList;
        private DestinationEndpoint destinationEndpoint;
        private ReposeCluster serviceDomain;
        private Target target;
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private RoutingTagger routingTagger;
        private final String DST = "dst1";
        private final String REGEX1 = "(/some/uri)";
        private final String REGEX2 = "(/some/uri)(.*)";
        private final String REQUEST_URI = "/some/uri/with/extra";
        private final String CONFIG_URI = "/another/uri";
        private final String FINAL_URI = "/another/uri/with/extra";
        private DestinationRouterHandlerFactory factory;
        private DestinationRouterConfiguration destinationRouterConfig;

        @Before
        public void setUp() {
            
            destinationRouterConfig = new DestinationRouterConfiguration();
            factory = new DestinationRouterHandlerFactory();
            
            powerProxy = new SystemModel();
            serviceDomain = new ReposeCluster();
            target = new Target();
            domainNode = new Node();
            domainNodeList = new NodeList();
            destinationEndpoint = new DestinationEndpoint();
            destinationList = new DestinationList();

            domainNode.setId("node1");
            domainNode.setHostname("localhost");
            domainNode.setHttpPort(8888);
            domainNodeList.getNode().add(domainNode);

            destinationEndpoint.setHostname("localhost");
            destinationEndpoint.setId(DST);
            destinationList.getEndpoint().add(destinationEndpoint);

            serviceDomain.setId("repose");
            serviceDomain.setNodes(domainNodeList);
            serviceDomain.setDestinations(destinationList);
            
            destinationRouterConfig.setTarget(target);
            factory.configurationUpdated(destinationRouterConfig);


            httpServletRequest = mock(HttpServletRequest.class);
            httpServletResponse = mock(HttpServletResponse.class);

        }

        @Test
        public void shouldAddRoute() {
            target.setId(DST);
            routingTagger = factory.buildHandler();

            FilterDirector director = routingTagger.handleRequest(httpServletRequest, null);
            assertEquals("Director should have the targeted destination", director.getDestinations().get(0).getDestinationId(), DST);
        }

        @Test
        public void shouldNotAddRoute() {
            routingTagger = factory.buildHandler();

            FilterDirector filter = routingTagger.handleRequest(httpServletRequest, null);
            assertEquals("No destinations should have been added.", 0, filter.getDestinations().size());
        }

        @Test
        public void shouldRedirectToUri() {
            target.setId(DST);
            target.setUriRegex(REGEX1);
            factory.configurationUpdated(destinationRouterConfig);
            routingTagger = factory.buildHandler();

            when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

            FilterDirector filter = routingTagger.handleRequest(httpServletRequest, null);

            assertNotNull(filter.getDestinations().get(0));
            assertEquals(REQUEST_URI, filter.getDestinations().get(0).getUri());
        }

        @Test
        public void shouldReplaceUri() {

            target.setId(DST);
            target.setUriRegex(REGEX2);
            target.setUri(CONFIG_URI);
            factory.configurationUpdated(destinationRouterConfig);
            routingTagger = factory.buildHandler();

            when(httpServletRequest.getRequestURI()).thenReturn(REQUEST_URI);

            FilterDirector filterDirector = routingTagger.handleRequest(httpServletRequest, null);

            assertNotNull(filterDirector.getDestinations().get(0));
            assertEquals(FINAL_URI, filterDirector.getDestinations().get(0).getUri());
        }
    }
}
