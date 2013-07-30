package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.model.Cluster;
import com.rackspace.papi.model.Destination;
import com.rackspace.repose.service.configuration.context.SystemModelConfigListener;
import com.rackspace.repose.service.configuration.resource.*;
import com.rackspace.papi.model.Filter;
import org.openrepose.components.routing.servlet.config.Target;

import java.util.ArrayList;

public class ApiHelper {

    public static Api newApi() {
        Api api = new Api();
        return api;
    }

    public static Api fromFilter(Filter filter) {
        Api api = newApi();
        update(api, filter);
        return api;
    }

    public static void update(Api api, Filter filter) {
        api.setId(filter.getId());
        api.setName(extractName(filter));
        api.setCaptureUri(extractCaptureUri(filter));
        api.setEnabled(true);
    }

    public static void update(Api api, Target target) {
        api.setCaptureUri(extractCaptureUri(target));
        api.setRoutingUri(target.getUri());
        api.setServiceClusterId(target.getId());
    }

    public static String extractCaptureUri(Filter filter) {
        return filter.getUriRegex().replace(".*", "");
    }

    public static String extractName(Filter filter) {
        return filter.getConfiguration().replace(".cfg.xml", "");
    }

    public static String extractCaptureUri(Target target) {
        return target.getUriRegex().replace(")(.*)", "").substring(1);
    }

}
