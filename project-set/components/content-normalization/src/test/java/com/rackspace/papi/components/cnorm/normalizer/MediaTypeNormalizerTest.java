package com.rackspace.papi.components.cnorm.normalizer;

import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.components.normalization.config.MediaType;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class MediaTypeNormalizerTest {

    public static class WhenNormalizingVariantExtensions {

        private List<MediaType> configuredMediaTypes;
        private MediaTypeNormalizer normalizer;
        
        @Before
        public void standUp() {
            configuredMediaTypes = new LinkedList<MediaType>();

            final MediaType configuredMediaType = new MediaType();
            configuredMediaType.setName("application/xml");
            configuredMediaType.setVariantExtension("xml");
            configuredMediaType.setPreferred(Boolean.TRUE);

            configuredMediaTypes.add(configuredMediaType);
            
            normalizer = new MediaTypeNormalizer(configuredMediaTypes);
        }

        @Test
        public void shouldCorrectlyCaptureVariantExtensions() {
            final HttpServletRequest request = mock(HttpServletRequest.class);
            final FilterDirector director = new FilterDirectorImpl();
            
            when(request.getRequestURI()).thenReturn("/a/request/uri.xml");
            when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/a/request/uri.xml"));
            
            final MediaType identifiedMediaType = normalizer.getMediaTypeForVariant(request, director);
            
            assertNotNull("Identified media type from request variant extensions should not be null", identifiedMediaType);
            assertEquals("xml", identifiedMediaType.getVariantExtension());
            assertEquals("/a/request/uri", director.getRequestUri());
            assertEquals("http://localhost/a/request/uri", director.getRequestUrl().toString());
        }

        @Test
        public void shouldCorrectlyIgnoreQueryParameters() {
            final HttpServletRequest request = mock(HttpServletRequest.class);
            final FilterDirector director = new FilterDirectorImpl();
            
            when(request.getRequestURI()).thenReturn("/a/request/uri.xml?name=name&value=1");
            when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/a/request/uri.xml?name=name&value=1"));
            
            final MediaType identifiedMediaType = normalizer.getMediaTypeForVariant(request, director);
            
            assertNotNull("Identified media type from request variant extensions should not be null", identifiedMediaType);
            assertEquals("xml", identifiedMediaType.getVariantExtension());
            assertEquals("/a/request/uri?name=name&value=1", director.getRequestUri());
            assertEquals("http://localhost/a/request/uri?name=name&value=1", director.getRequestUrl().toString());
        }

        @Test
        public void shouldCorrectlyIgnoreUriFragments() {
            final HttpServletRequest request = mock(HttpServletRequest.class);
            final FilterDirector director = new FilterDirectorImpl();
            
            when(request.getRequestURI()).thenReturn("/a/request/uri.xml#fragment");
            when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/a/request/uri.xml#fragment"));
            
            final MediaType identifiedMediaType = normalizer.getMediaTypeForVariant(request, director);
            
            assertNotNull("Identified media type from request variant extensions should not be null", identifiedMediaType);
            assertEquals("xml", identifiedMediaType.getVariantExtension());
            assertEquals("/a/request/uri#fragment", director.getRequestUri());
            assertEquals("http://localhost/a/request/uri#fragment", director.getRequestUrl().toString());
        }

        @Test
        public void shouldCorrectlyIgnoreUriFragmentsAndQueryParameters() {
            final HttpServletRequest request = mock(HttpServletRequest.class);
            final FilterDirector director = new FilterDirectorImpl();
            
            when(request.getRequestURI()).thenReturn("/a/request/uri.xml?name=name&value=1#fragment");
            when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/a/request/uri.xml?name=name&value=1#fragment"));
            
            final MediaType identifiedMediaType = normalizer.getMediaTypeForVariant(request, director);
            
            assertNotNull("Identified media type from request variant extensions should not be null", identifiedMediaType);
            assertEquals("xml", identifiedMediaType.getVariantExtension());
            assertEquals("/a/request/uri?name=name&value=1#fragment", director.getRequestUri());
            assertEquals("http://localhost/a/request/uri?name=name&value=1#fragment", director.getRequestUrl().toString());
        }

        @Test
        public void shouldCorrectlyCaptureUnusualVariantExtensions() {
            final HttpServletRequest request = mock(HttpServletRequest.class);
            final FilterDirector director = new FilterDirectorImpl();
            
            when(request.getRequestURI()).thenReturn("/a/request/uri/.xml");
            when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/a/request/uri/.xml"));
            
            final MediaType identifiedMediaType = normalizer.getMediaTypeForVariant(request, director);
            
            assertNotNull("Identified media type from request variant extensions should not be null", identifiedMediaType);
            assertEquals("xml", identifiedMediaType.getVariantExtension());
            assertEquals("/a/request/uri/", director.getRequestUri());
            assertEquals("http://localhost/a/request/uri/", director.getRequestUrl().toString());
        }
    }
}