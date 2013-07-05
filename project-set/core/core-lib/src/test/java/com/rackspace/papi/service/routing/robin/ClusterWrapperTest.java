package com.rackspace.papi.service.routing.robin;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.Node;
import com.rackspace.papi.model.NodeList;
import com.rackspace.papi.service.event.common.Event;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.healthcheck.HealthCheckEvent;
import com.rackspace.papi.service.healthcheck.NodeStatusWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class ClusterWrapperTest {

    public static class WhenHandlingHealthCheckEvents {

        private static String NODE1 = "node1";
        private static String NODE2 = "node2";
        private static int HTTP = 8080;
        private static int HTTPS = 443;

        private ClusterWrapper clusterWrapper;
        private Cluster cluster;
        private NodeStatusWrapper wrapper1;
        private NodeStatusWrapper wrapper2;

        private Event<HealthCheckEvent, NodeStatusWrapper> event;

        @Before
        public void setUp() {

            event = mock(Event.class);

            Node node1 = new Node();
            node1.setId(NODE1);
            node1.setHostname(NODE1);
            node1.setHttpPort(HTTP);
            node1.setHttpsPort(HTTPS);

            Node node2 = new Node();
            node2.setId(NODE2);
            node2.setHostname(NODE2);
            node2.setHttpPort(HTTP);
            node2.setHttpsPort(HTTPS);

            NodeList nodeList = new NodeList();
            nodeList.getNode().add(node1);
            nodeList.getNode().add(node2);

            wrapper1 = new NodeStatusWrapper(node1);
            wrapper2 = new NodeStatusWrapper(node2);

            cluster = new Cluster();
            cluster.setNodes(nodeList);

            clusterWrapper = new ClusterWrapper(cluster);

        }

        @Test
        public void healthyNodeToUnhealthy() {

            when(event.payload()).thenReturn(wrapper1);
            when(event.type()).thenReturn(HealthCheckEvent.UNHEALTHY);

            clusterWrapper.onEvent(event);

            Node node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

            // there should only be one node, so we will get node 2 twice in a row
            node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

        }

        @Test
        public void healthyNodeToHealthy() {

            when(event.payload()).thenReturn(wrapper1);
            when(event.type()).thenReturn(HealthCheckEvent.HEALTHY);

            clusterWrapper.onEvent(event);

            Node node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE1));

            node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

        }

        @Test
        public void unhealthyNodeToHealthy() throws NoSuchFieldException, IllegalAccessException {

            removeNode1FromHealthyNodes();

            when(event.payload()).thenReturn(wrapper1);
            when(event.type()).thenReturn(HealthCheckEvent.HEALTHY);

            clusterWrapper.onEvent(event);

            Node node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

            node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE1));

        }


        @Test
        public void unhealthyNodeToUnhealthy() throws NoSuchFieldException, IllegalAccessException {

            removeNode1FromHealthyNodes();

            when(event.payload()).thenReturn(wrapper1);
            when(event.type()).thenReturn(HealthCheckEvent.UNHEALTHY);

            clusterWrapper.onEvent(event);

            Node node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

            // node1 was already unhealthy, expect node2 to be returned twice.
            node = clusterWrapper.getNextNode();
            assertNotNull(node);
            assertTrue(node.getId().equals(NODE2));

        }

        private void removeNode1FromHealthyNodes() throws NoSuchFieldException, IllegalAccessException {

            // remove node 1 from the healthy nodes
            Field field = ClusterWrapper.class.getDeclaredField("healthyNodes");
            field.setAccessible(true);
            List<Node> healthyNodes = (List<Node>) field.get(clusterWrapper);

            if (healthyNodes.get(0).getId().equals(NODE1)) {
                healthyNodes.remove(0);
            } else {
                healthyNodes.remove(1);
            }
        }

    }

}
