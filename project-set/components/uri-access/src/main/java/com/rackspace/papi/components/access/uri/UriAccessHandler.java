package com.rackspace.papi.components.access.uri;

import com.rackspace.papi.commons.util.http.HttpStatusCode;
import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.components.access.uri.config.*;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.common.AbstractFilterLogicHandler;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriAccessHandler extends AbstractFilterLogicHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UriAccessHandler.class);
    private UriAccessConfigHelper helper;

    public UriAccessHandler(UriAccessConfigHelper helper) {
        this.helper = helper;
    }

    @Override
    public FilterDirector handleRequest(HttpServletRequest request, ReadableHttpServletResponse response) {

        final FilterDirector filterDirector = new FilterDirectorImpl();

        String group = request.getHeader(PowerApiHeader.GROUPS.toString());

        AccessGroup accessGroup = helper.getGroup(group);

        if (accessGroup == null) {
            filterDirector.setResponseStatus(HttpStatusCode.UNAUTHORIZED);
            filterDirector.setFilterAction(FilterAction.RETURN);
            return filterDirector;
        }

        for (Access access : accessGroup.getAccess()) {
            Matcher uriMatcher;
            if (access instanceof AccessWrapper) {
                uriMatcher = ((AccessWrapper) access).getRegexPattern().matcher(request.getRequestURI());
            } else {
                LOG.error("Unable to locate pre-built regex pattern in uri access group. " +
                        "In order to continue operation, uri access will compile patterns dynamically.");
                uriMatcher = Pattern.compile(access.getUriRegex()).matcher(request.getRequestURI());
            }

            if (uriMatcher.matches() && httpMethodMatches(access.getHttpMethods(), request.getMethod())) {
                filterDirector.setFilterAction(FilterAction.PASS);
                return filterDirector;
            }
        }

        filterDirector.setResponseStatus(HttpStatusCode.UNAUTHORIZED);
        filterDirector.setFilterAction(FilterAction.RETURN);
        return filterDirector;
    }

    private boolean httpMethodMatches(List<HttpMethod> httpMethods, String requestMethod) {
        return (httpMethods.contains(HttpMethod.ALL) || httpMethods.contains(HttpMethod.valueOf(requestMethod.toUpperCase())));
    }
}
