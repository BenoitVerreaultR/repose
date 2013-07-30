package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.components.access.uri.config.Access;
import com.rackspace.papi.components.access.uri.config.AccessGroup;
import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.repose.service.configuration.resource.Auth;
import com.rackspace.repose.service.configuration.resource.AuthType;
import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.configuration.resource.RateLimitType;
import com.rackspace.repose.service.ratelimit.config.ConfiguredRatelimit;
import com.rackspace.repose.service.ratelimit.config.RateLimitingConfiguration;

import java.util.ArrayList;

public class PolicyHelper {


    public static Policy newPolicy() {
        Policy policy = new Policy();
        return policy;
    }

    public static Policy fromAccessGroup(AccessGroup accessGroup) {
        Policy policy = newPolicy();
        update(policy, accessGroup);
        return policy;
    }

    public static void update(Policy policy, AccessGroup accessGroup) {
        policy.setId(accessGroup.getId());
        policy.setAuthId(accessGroup.getGroup());
        policy.setApiIds(new ArrayList<String>(accessGroup.getAccess().size()));
        for (Access access : accessGroup.getAccess()) {
            policy.getApiIds().add(access.getId());
        }
    }

    public static void update(Policy policy, ConfiguredRatelimit ratelimit) {
        policy.setRateLimitType(RateLimitType.valueOf(ratelimit.getUnit().value()));
        policy.setRateLimitValue(ratelimit.getValue());
    }



}
