package com.rackspace.papi.components.access.uri.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriAccessConfigHelper {

    private static final Logger LOG = LoggerFactory.getLogger(UriAccessConfigHelper.class);
    private AccessGroup defaultGroup;
    private Map<String, AccessGroup> configuredGroups;

    public UriAccessConfigHelper(UriAccessConfig uriAccessConfig) {
        this.configuredGroups = processConfiguration(uriAccessConfig);
    }

    public AccessGroup getGroup(String group) {

        AccessGroup accessGroup = configuredGroups.get(group);

        if (accessGroup != null) {
            return accessGroup;
        }

        return defaultGroup;
    }

    private Map<String, AccessGroup> processConfiguration(UriAccessConfig uriAccessConfig) {

        Map<String, AccessGroup> accessGroupMap = new HashMap<String, AccessGroup>();

        for (AccessGroup accessGroup : uriAccessConfig.getAccessGroup()) {

            List<Access> accessList = new ArrayList<Access>(accessGroup.getAccess().size());

            for (Access access : accessGroup.getAccess()) {
                accessList.add(new AccessWrapper(access));
            }

            accessGroup.getAccess().clear();
            accessGroup.getAccess().addAll(accessList);

            accessGroupMap.put(accessGroup.getGroup(), accessGroup);

            if (accessGroup.isDefault()) {
                defaultGroup = accessGroup;
            }
        }

        return accessGroupMap;
    }

}
