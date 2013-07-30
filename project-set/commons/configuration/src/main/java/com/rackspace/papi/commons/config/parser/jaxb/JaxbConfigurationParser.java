package com.rackspace.papi.commons.config.parser.jaxb;

import com.rackspace.papi.commons.config.parser.common.AbstractConfigurationObjectParser;
import com.rackspace.papi.commons.config.resource.ConfigurationResource;
import com.rackspace.papi.commons.util.pooling.GenericBlockingResourcePool;
import com.rackspace.papi.commons.util.pooling.Pool;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbConfigurationParser <T> extends AbstractConfigurationObjectParser<T> {

    private final Pool<Unmarshaller> unmarshallerPool;
    private final Pool<Marshaller> marshallerPool;

    public JaxbConfigurationParser(Class<T> configurationClass, JAXBContext jaxbContext, URL xsdStreamSource) {
        super(configurationClass);
        unmarshallerPool = new GenericBlockingResourcePool<Unmarshaller>(new UnmarshallerConstructionStrategy(jaxbContext,xsdStreamSource));
        marshallerPool = new GenericBlockingResourcePool<Marshaller>(new MarshallerConstructionStrategy(jaxbContext,xsdStreamSource));
    }
    
    @Override
    public T read(ConfigurationResource cr) {
        final Object parsedObject = unmarshallerPool.use(new UnmarshallerResourceContext(cr));
        final Object returnable = parsedObject instanceof JAXBElement
                ? ((JAXBElement)parsedObject).getValue()
                : parsedObject;
                
        if (!configurationClass().isInstance(returnable)) {
             throw new ClassCastException("Parsed object from XML does not match the expected configuration class. "
                     + "Expected: " + configurationClass().getCanonicalName() + "  -  "
                     + "Actual: " + returnable.getClass().getCanonicalName());
        }
        
        return configurationClass().cast(returnable);
    }

    public void write(Object config, String uri) {
        marshallerPool.use(new MarshallerResourceContext(config, uri));
    }

}
