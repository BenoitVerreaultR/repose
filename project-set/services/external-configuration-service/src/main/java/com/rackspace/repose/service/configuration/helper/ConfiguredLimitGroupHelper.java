package com.rackspace.repose.service.configuration.helper;

import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.limits.schema.HttpMethod;
import com.rackspace.repose.service.limits.schema.TimeUnit;
import com.rackspace.repose.service.ratelimit.config.ConfiguredLimitGroup;
import com.rackspace.repose.service.ratelimit.config.ConfiguredRatelimit;

public class ConfiguredLimitGroupHelper {

    private static String URI = "/*";
    private static String URI_REGEX = "(/).*";

    public static ConfiguredLimitGroup newConfiguredLimitGroup() {
        ConfiguredLimitGroup limitGroup = new ConfiguredLimitGroup();
        return limitGroup;
    }

    public static ConfiguredRatelimit newConfiguredRatelimit() {
        ConfiguredRatelimit ratelimit = new ConfiguredRatelimit();
        return ratelimit;
    }

    public static ConfiguredLimitGroup fromPolicy(Policy policy) {
        ConfiguredLimitGroup limitGroup = newConfiguredLimitGroup();
        update(limitGroup, policy);
        return limitGroup;
    }

    public static void update(ConfiguredLimitGroup limitGroup, Policy policy) {
        limitGroup.setId(policy.getId());
        limitGroup.setDefault(false);
        limitGroup.getGroups().clear();
        limitGroup.getGroups().add(policy.getAuthId());
        limitGroup.getLimit().clear();
        ConfiguredRatelimit ratelimit = newConfiguredRatelimit();
        limitGroup.getLimit().add(ratelimit);
        update(ratelimit, policy);

    }

    public static void update(ConfiguredRatelimit ratelimit, Policy policy) {
        ratelimit.setUri(URI);
        ratelimit.setUriRegex(URI_REGEX);
        ratelimit.getHttpMethods().clear();
        ratelimit.getHttpMethods().add(HttpMethod.ALL);
        ratelimit.setUnit(TimeUnit.fromValue(policy.getRateLimitType().name()));
        ratelimit.setValue(policy.getRateLimitValue());
    }

}
