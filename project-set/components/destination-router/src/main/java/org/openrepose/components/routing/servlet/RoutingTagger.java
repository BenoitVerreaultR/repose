package org.openrepose.components.routing.servlet;

import com.rackspace.papi.commons.util.StringUtilities;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.common.AbstractFilterLogicHandler;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;
import org.openrepose.components.routing.servlet.config.Target;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutingTagger extends AbstractFilterLogicHandler {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTagger.class);
    private String id;
    private String uri;
    private Pattern regex;
    private double quality;

    public RoutingTagger(String id, String uri, Pattern regex, double quality) {
        this.quality = quality;
        this.id = id;
        this.uri = uri;
        this.regex = regex;
    }

    @Override
    public FilterDirector handleRequest(HttpServletRequest request, ReadableHttpServletResponse response) {
        final FilterDirector myDirector = new FilterDirectorImpl();
        myDirector.setFilterAction(FilterAction.PASS);

        if (StringUtilities.isBlank(id)) {
            LOG.warn("No Destination configured for Destination Router");
            return myDirector;
        }

        if (regex == null) {
            LOG.warn("No uri substitution defined.");
            myDirector.addDestination(id, request.getRequestURI(), quality);
            return myDirector;
        }

        Matcher match = regex.matcher(request.getRequestURI());
        if (!match.matches() || match.groupCount() != 2) {
            LOG.warn("Wrong number of match groups in uri substitution.");
            myDirector.addDestination(id, request.getRequestURI(), quality);
            return myDirector;
        }

        myDirector.addDestination(id, uri + match.group(2), quality);
        return myDirector;
    }
}
