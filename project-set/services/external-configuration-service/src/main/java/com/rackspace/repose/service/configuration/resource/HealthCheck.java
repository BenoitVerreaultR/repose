package com.rackspace.repose.service.configuration.resource;

import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheck {

    @JsonProperty
    private Protocol protocol;
    @JsonProperty
    private int port;
    @JsonProperty
    private String uri;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
