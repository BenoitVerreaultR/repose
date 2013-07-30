package com.rackspace.papi.commons.config.parser.inputstream;

import com.rackspace.papi.commons.config.parser.common.AbstractConfigurationObjectParser;
import com.rackspace.papi.commons.config.resource.ConfigurationResource;
import com.rackspace.papi.commons.config.resource.ResourceResolutionException;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamConfigurationParser extends AbstractConfigurationObjectParser<InputStream> {

   public InputStreamConfigurationParser() {
      super(InputStream.class);
   }

   @Override
   public InputStream read(ConfigurationResource cr) {
      try {
         return cr.newInputStream();
      } catch (IOException ex) {
         throw new ResourceResolutionException("Unable to read configuration file: " + cr.name(), ex);
      }
   }

    @Override
    public void write(Object config, String URI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
