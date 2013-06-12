package com.rackspace.papi.components.identity.header;

import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.components.identity.token.header.config.HeadersMapping;
import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.papi.filter.logic.FilterDirector;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author malconis
 */
public class HeaderTokenIdentityHandlerFactoryTest {

    private static String QUALITY = "0.5";
    private static String QUALITY_VALUE = ";q=0.5";
    private static String URI1 = "/some/uri";
    private static String GROUP1 = "1234";
    private static String TOKEN1 = "1234";
    private static String GROUP2 = "4321";
    private static String TOKEN2 = "4321";
    private HeaderTokenIdentityHandlerFactory factory;
    private HeaderTokenIdentityConfig config;
    private HttpServletRequest request;
    private ReadableHttpServletResponse response;
    private HeaderTokenIdentityHandler handler;

    @Before
    public void setUp() {
        
        factory = new HeaderTokenIdentityHandlerFactory();
        config = new HeaderTokenIdentityConfig();
        config.setQuality(QUALITY);

        HeadersMapping headersMapping = new HeadersMapping();

        HeaderMapping mapping = new HeaderMapping();
        mapping.setId("Mapping 1");
        mapping.setGroup(GROUP1);
        mapping.setToken(TOKEN1);
        headersMapping.getHeader().add(mapping);

        mapping = new HeaderMapping();
        mapping.setId("Mapping 2");
        mapping.setGroup(GROUP2);
        mapping.setToken(TOKEN2);
        headersMapping.getHeader().add(mapping);

        config.setHeaders(headersMapping);
        
        factory.configurationUpdated(config);

        handler = factory.buildHandler();
        request = mock(HttpServletRequest.class);
        response = mock(ReadableHttpServletResponse.class);

    }
    
    @Test
    public void shouldSetDefaultQuality(){
        
        config = new HeaderTokenIdentityConfig();
        HeadersMapping headersMapping = new HeadersMapping();

        HeaderMapping headerMapping = new HeaderMapping();
        headerMapping.setId("Mapping 1");
        headerMapping.setGroup(GROUP1);
        headerMapping.setToken(TOKEN1);
        headersMapping.getHeader().add(headerMapping);
        
        config.setHeaders(headersMapping);
        
        factory.configurationUpdated(config);

        handler = factory.buildHandler();
        
        when(request.getRequestURI()).thenReturn(URI1);

        FilterDirector result = handler.handleRequest(request, response);

        Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
        assertFalse("Should have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

        String userName = values.iterator().next();

        assertEquals("Should find user name in header", GROUP1 + QUALITY_VALUE, userName);
        
    }
    
    @Test
    public void shouldSetDefaultQualityIfConfigIsBlank(){
        
        config = new HeaderTokenIdentityConfig();
        config.setQuality("");
        HeadersMapping headersMapping = new HeadersMapping();

        HeaderMapping headerMapping = new HeaderMapping();
        headerMapping.setId("Mapping 1");
        headerMapping.setGroup(GROUP1);
        headerMapping.setToken(TOKEN1);
        headersMapping.getHeader().add(headerMapping);
        
        config.setHeaders(headersMapping);
        
        factory.configurationUpdated(config);

        handler = factory.buildHandler();
        
        when(request.getRequestURI()).thenReturn(URI1);

        FilterDirector result = handler.handleRequest(request, response);

        Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
        assertFalse("Should have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

        String userName = values.iterator().next();

        assertEquals("Should find user name in header", GROUP1 + QUALITY_VALUE, userName);
        
    }
}
