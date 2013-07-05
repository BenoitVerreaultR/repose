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
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author malconis
 */
public class HeaderTokenIdentityHandlerFactoryTest {

    private static String URI1 = "/some/uri";
    private static String GROUP1 = "Group1";
    private static String TOKEN1 = "1111";
    private static String GROUP2 = "Group2";
    private static String TOKEN2 = "2222";
    private HeaderTokenIdentityHandlerFactory factory;
    private HeaderTokenIdentityConfig config;
    private HttpServletRequest request;
    private ReadableHttpServletResponse response;
    private HeaderTokenIdentityHandler handler;

    @Before
    public void setUp() {
        
        factory = new HeaderTokenIdentityHandlerFactory();
        config = new HeaderTokenIdentityConfig();

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
    public void shouldHaveTokenToGroupSet() throws NoSuchFieldException, IllegalAccessException {

        Field field = HeaderTokenIdentityHandlerFactory.class.getDeclaredField("tokenToGroup");
        field.setAccessible(true);
        Map<String, String> tokenToGroup = (Map<String, String>) field.get(factory);

        assertTrue(tokenToGroup.containsKey(TOKEN1));
        assertTrue(tokenToGroup.containsKey(TOKEN2));
        assertTrue(tokenToGroup.containsValue(GROUP1));
        assertTrue(tokenToGroup.containsValue(GROUP2));

    }

}
