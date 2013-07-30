package com.rackspace.papi.commons.config.parser.common;

import com.rackspace.papi.commons.config.resource.ConfigurationResource;

import javax.xml.bind.JAXBElement;

public interface ConfigurationParser<T> {

    T read(ConfigurationResource cr);

    void write(Object config, String URI);

    Class<T> configurationClass();
}
