package com.rackspace.papi.commons.config.parser.jaxb;

import com.rackspace.papi.commons.util.pooling.ResourceContextException;
import com.rackspace.papi.commons.util.pooling.SimpleResourceContext;
import com.sun.tools.internal.ws.processor.util.DirectoryUtil;
import com.sun.tools.javac.util.Paths;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import org.springframework.util.FileSystemUtils;
import sun.nio.ch.FileChannelImpl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

public class MarshallerResourceContext implements SimpleResourceContext<Marshaller> {

    private final Object config;
    private final String uri;

    public MarshallerResourceContext(Object config, String uri) {
        this.config = config;
        this.uri = uri;
    }

    //Suppressing the warning as the new exception is using the jaxbe error code and message to pass on to the ResourceContextExcepiton
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void perform(Marshaller resource) {

        try {
            File file = new File(uri);
            File tmp = new File(uri + ".tmp");
            resource.marshal(config, tmp);
            tmp.renameTo(file);
        } catch (JAXBException jaxbe) {
            throw new ResourceContextException("Failed to marshall resource " + config.getClass().getSimpleName() + " - " + jaxbe.getCause()
                    + " - Error code: "
                    + jaxbe.getErrorCode()
                    + " - Reason: "
                    + jaxbe.getMessage(), jaxbe.getLinkedException());
        } /*catch (IOException ioe) {
            throw new ResourceContextException("An I/O error has occurred while trying to write resource " + cfgResource.getClass().getSimpleName() + " - Reason: " + ioe.getMessage(), ioe);
        } */catch (Exception ex) {
            throw new ResourceContextException("Failed to marshall resource " + config.getClass().getSimpleName() + " - Reason: " + ex.getMessage(), ex);
        } finally {
            // attempt cleanup
            File tmp = new File(uri + ".tmp");
            if (tmp.exists()) {
                tmp.delete();
            }
        }
    }
}
