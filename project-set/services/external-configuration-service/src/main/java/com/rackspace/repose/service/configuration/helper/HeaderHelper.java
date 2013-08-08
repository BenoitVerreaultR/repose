package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.repose.service.configuration.resource.Auth;

public class HeaderHelper {

    public static HeaderMapping newHeaderMapping() {
        HeaderMapping headerMapping = new HeaderMapping();
        return headerMapping;
    }

    public static HeaderMapping fromAuth(Auth auth) {
        HeaderMapping headerMapping = newHeaderMapping();
        update(headerMapping, auth);
        return headerMapping;
    }

    public static void update(HeaderMapping headerMapping, Auth auth) {
        headerMapping.setId(auth.getId());
        headerMapping.setGroup(auth.getName());
        headerMapping.setToken(auth.getAuthKey());
    }

}
