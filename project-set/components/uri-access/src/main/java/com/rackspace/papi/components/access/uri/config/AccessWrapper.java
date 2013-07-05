package com.rackspace.papi.components.access.uri.config;

import java.util.List;
import java.util.regex.Pattern;

public class AccessWrapper extends Access {

    private Access access;
    private Pattern regexPattern;

    public AccessWrapper(Access access) {
        this.access = access;
        this.regexPattern = Pattern.compile(access.getUriRegex());
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    @Override
    public String getUri() {
        return access.getUri();
    }

    @Override
    public void setUri(String value) {
        access.setUri(value);
    }

    @Override
    public String getUriRegex() {
        return access.getUriRegex();
    }

    @Override
    public void setUriRegex(String value) {
        access.setUriRegex(value);
    }

    @Override
    public List<HttpMethod> getHttpMethods() {
        return access.getHttpMethods();
    }

    @Override
    public String toString() {
        return access.toString();
    }

}
