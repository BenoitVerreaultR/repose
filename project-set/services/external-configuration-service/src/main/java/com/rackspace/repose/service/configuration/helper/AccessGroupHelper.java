package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.components.access.uri.config.Access;
import com.rackspace.papi.components.access.uri.config.AccessGroup;
import com.rackspace.papi.components.access.uri.config.HttpMethod;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.Policy;

import java.util.Iterator;
import java.util.Map;

public class AccessGroupHelper {

    private static String URI_REGEX_FORMAT = "%s.*";

    public static AccessGroup newAccessGroup() {
        AccessGroup accessGroup = new AccessGroup();
        return accessGroup;
    }

    public static AccessGroup fromPolicy(Policy policy, Map<String, Api> apisById) {
        AccessGroup accessGroup = newAccessGroup();
        update(accessGroup, policy);
        update(accessGroup, apisById);
        return accessGroup;
    }

    public static void update(AccessGroup accessGroup, Policy policy) {
        accessGroup.setId(policy.getId());
        accessGroup.setGroup(policy.getAuthId());
        accessGroup.setDefault(false);
        accessGroup.getAccess().clear();
        for (String apiId : policy.getApiIds()) {
            Access access = new Access();
            accessGroup.getAccess().add(access);
            access.setId(apiId);
        }
    }

    public static void update(AccessGroup accessGroup, Map<String, Api> apisById) {
        Iterator<Access> iterator = accessGroup.getAccess().iterator();
        while (iterator.hasNext()) {
            Access access = iterator.next();
            Api api = apisById.get(access.getId());
            if (api == null) {
                iterator.remove();
                continue;
            }
            update(access, api);
        }
    }

    public static void update(Access access, Api api) {
        access.setUri(api.getRoutingUri());
        access.setUriRegex(String.format(URI_REGEX_FORMAT, api.getCaptureUri()));
        access.getHttpMethods().clear();
        access.getHttpMethods().add(HttpMethod.ALL);
    }

}
