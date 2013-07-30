package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.model.Filter;
import com.rackspace.repose.service.configuration.resource.Api;

public class FilterHelper {

    public static final String DESTINATION_ROUTER = "destination-router";
    private static final String CONFIG_FILE_FORMAT = "%s.cfg.xml";
    private static final String CAPTURE_URI_FORMAT = "%s.*";

    public static Filter newFilter() {
        Filter filter = new Filter();
        filter.setName(DESTINATION_ROUTER);
        return filter;
    }

    public static Filter fromApi(Api api) {
        Filter filter = newFilter();
        update(filter, api);
        return filter;
    }

    public static void update(Filter filter, Api api) {
        filter.setId(api.getId());
        filter.setUriRegex(String.format(CAPTURE_URI_FORMAT, api.getCaptureUri()));
        filter.setConfiguration(String.format(CONFIG_FILE_FORMAT, api.getName()));
    }

}
