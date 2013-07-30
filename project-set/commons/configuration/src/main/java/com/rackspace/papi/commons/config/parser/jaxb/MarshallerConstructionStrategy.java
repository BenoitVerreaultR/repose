package com.rackspace.papi.commons.config.parser.jaxb;

import com.rackspace.papi.commons.util.pooling.ConstructionStrategy;
import com.rackspace.papi.commons.util.pooling.ResourceConstructionException;
import com.rackspace.papi.commons.validate.xsd.JAXBValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.net.URL;

public class MarshallerConstructionStrategy implements ConstructionStrategy<Marshaller> {

    private static final Logger LOG = LoggerFactory.getLogger(MarshallerConstructionStrategy.class);
    private final JAXBContext context;
    private final URL xsdStreamSource;

    public MarshallerConstructionStrategy(JAXBContext context) {
        this.context = context;
        xsdStreamSource = null;
    }

    public MarshallerConstructionStrategy(JAXBContext context, URL xsdStreamSource) {
        this.context = context;
        this.xsdStreamSource = xsdStreamSource;
    }

    @Override
    public Marshaller construct() {
        try {


            Marshaller marshaller = context.createMarshaller();

            // output pretty printed
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            if (xsdStreamSource != null) {

                SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
                factory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true);

                Schema schema = factory.newSchema(xsdStreamSource);
                if (schema != null) {
                    marshaller.setSchema(schema);
                    marshaller.setEventHandler(new JAXBValidator());
                }


            }

            return marshaller;

        } catch (JAXBException jaxbe) {
            throw new ResourceConstructionException("Failed to construct JAXB unmarshaller. Reason: " + jaxbe.getMessage(), jaxbe);
        } catch (SAXException ex) {
            LOG.error("Error validating XML file", ex);
        }
        return null;


    }
}
