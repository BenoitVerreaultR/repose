package com.rackspace.papi.service.healthcheck;

import com.rackspace.papi.model.Node;

public class NodeStatusWrapper {

    private static final String URI_FORMAT = "%s://%s:%d%s";
    private final Node node;
    private boolean active;
    private int successCount;
    private final String uri;

    public NodeStatusWrapper(Node node) {
        this.node = node;
        this.active = false;
        this.successCount = 0;

        if (node.getHealthcheck() == null) {
            uri = "";
        } else {
            uri = String.format(URI_FORMAT, node.getHealthcheck().getProtocol(), node.getHostname(), node.getHealthcheck().getPort(), node.getHealthcheck().getUri());
        }
    }

    public Node getNode() {
        return node;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public String getHealthCheckUri() {
        return uri;
    }

}
