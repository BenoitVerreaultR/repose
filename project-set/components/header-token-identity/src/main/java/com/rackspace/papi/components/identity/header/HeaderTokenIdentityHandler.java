package com.rackspace.papi.components.identity.header;

import com.rackspace.papi.commons.util.http.HttpStatusCode;
import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.HeaderManager;
import com.rackspace.papi.filter.logic.common.AbstractFilterLogicHandler;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class HeaderTokenIdentityHandler extends AbstractFilterLogicHandler {

    private final String tokenHeader;
    private final Map<String, String> tokenToGroup;

    public HeaderTokenIdentityHandler(Map<String, String> tokenToGroup, String tokenHeader) {
        this.tokenHeader = tokenHeader;
        this.tokenToGroup = tokenToGroup;
    }

    @Override
    public FilterDirector handleRequest(HttpServletRequest request, ReadableHttpServletResponse response) {

        final FilterDirector filterDirector = new FilterDirectorImpl();
        final HeaderManager headerManager = filterDirector.requestHeaderManager();

        // get the token, if null return 401
        String token = request.getHeader(tokenHeader);
        if (token == null) {
            filterDirector.setFilterAction(FilterAction.RETURN);
            filterDirector.setResponseStatus(HttpStatusCode.UNAUTHORIZED);
            return filterDirector;
        }

        // get the group, if null return 401
        String group = tokenToGroup.get(token);
        if (group == null) {
            filterDirector.setFilterAction(FilterAction.RETURN);
            filterDirector.setResponseStatus(HttpStatusCode.UNAUTHORIZED);
            return filterDirector;
        }

        // group found, add rate limiting headers
        filterDirector.setFilterAction(FilterAction.PASS);
        headerManager.appendHeader(PowerApiHeader.USER.toString(), group);
        headerManager.appendHeader(PowerApiHeader.GROUPS.toString(), group);

        return filterDirector;
    }
}
