package com.rackspace.repose.service.configuration.resource;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: benoit.verreault
 * Date: 2013/08/01
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class Ids {

    @JsonProperty
    private List<String> ids;

    public Ids(Collection<String> ids) {
        this.ids = new ArrayList<String>(ids);
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

}
