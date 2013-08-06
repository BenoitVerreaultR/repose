package com.rackspace.repose.service.configuration.resource;

import org.codehaus.jackson.annotate.JsonProperty;

public class Api {

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String captureUri;
    @JsonProperty
    private String routingUri;
    @JsonProperty
    private String serviceClusterId;
    @JsonProperty
    private boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaptureUri() {
        return captureUri;
    }

    public void setCaptureUri(String captureUri) {
        this.captureUri = captureUri;
    }

    public String getRoutingUri() {
        return routingUri;
    }

    public void setRoutingUri(String routingUri) {
        this.routingUri = routingUri;
    }

    public String getServiceClusterId() {
        return serviceClusterId;
    }

    public void setServiceClusterId(String serviceClusterId) {
        this.serviceClusterId = serviceClusterId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
