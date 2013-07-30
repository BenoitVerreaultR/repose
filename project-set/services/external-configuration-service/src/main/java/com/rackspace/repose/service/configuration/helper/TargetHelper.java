package com.rackspace.repose.service.configuration.helper;

import com.rackspace.repose.service.configuration.resource.Api;
import org.openrepose.components.routing.servlet.config.Target;

public class TargetHelper {

    private static final String CAPTURE_URI_FORMAT = "(%s)(.*)";

    public static Target newTarget() {
        Target target = new Target();
        return target;
    }

    public static Target fromApi(Api api) {
        Target target = newTarget();
        update(target, api);
        return target;
    }

    public static void update(Target target, Api api) {
        target.setId(api.getServiceClusterId());
        target.setUri(api.getRoutingUri());
        target.setUriRegex(String.format(CAPTURE_URI_FORMAT, api.getCaptureUri()));
        target.setQuality(1.0d);
    }

}
