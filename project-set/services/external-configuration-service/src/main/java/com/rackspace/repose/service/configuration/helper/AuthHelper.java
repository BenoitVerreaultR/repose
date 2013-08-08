package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.papi.model.Filter;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.Auth;
import com.rackspace.repose.service.configuration.resource.AuthType;
import org.openrepose.components.routing.servlet.config.Target;

public class AuthHelper {

    public static Auth newAuth() {
        Auth auth = new Auth();
        return auth;
    }

    public static Auth fromHeaderMapping(HeaderMapping headerMapping) {
        Auth auth = newAuth();
        update(auth, headerMapping);
        return auth;
    }

    public static void update(Auth auth, HeaderMapping headerMapping) {
        auth.setId(headerMapping.getId());
        auth.setName(headerMapping.getGroup());
        auth.setAuthKey(headerMapping.getToken());
        auth.setAuthType(AuthType.AUTH_KEY);
    }

}
