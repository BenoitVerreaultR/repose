package com.rackspace.repose.service.configuration.resource;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy {

    @JsonProperty
    private String id;
    @JsonProperty
    private List<String> apiIds;
    @JsonProperty
    private String authId;
    @JsonProperty
    private RateLimitType rateLimitType;
    @JsonProperty
    private int rateLimitValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getApiIds() {
        return apiIds;
    }

    public void setApiIds(List<String> apiIds) {
        this.apiIds = apiIds;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public RateLimitType getRateLimitType() {
        return rateLimitType;
    }

    public void setRateLimitType(RateLimitType rateLimitType) {
        this.rateLimitType = rateLimitType;
    }

    public int getRateLimitValue() {
        return rateLimitValue;
    }

    public void setRateLimitValue(int rateLimitValue) {
        this.rateLimitValue = rateLimitValue;
    }
}
