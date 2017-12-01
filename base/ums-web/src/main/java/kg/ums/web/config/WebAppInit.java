package kg.ums.web.config;


import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


@Configuration
public class WebAppInit extends AbstractAnnotationConfigDispatcherServletInitializer
{
    //---------------------------------
    private static final Logger logger = LoggerFactory.getLogger(WebAppInit.class);
    //---------------------------------
    
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class[] { WebAppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return null;
    }

    @Override
    protected String[] getServletMappings()
    {
        return new String[] { "/" };
    }

    @Override
    public void onStartup( ServletContext servletContext ) throws ServletException
    {
        super.onStartup( servletContext );
        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter( "encoding-filter",
                        new CharacterEncodingFilter() );
        encodingFilter.setInitParameter( "encoding", "UTF-8" );
        encodingFilter.setInitParameter( "forceEncoding", "true" );
        encodingFilter.addMappingForUrlPatterns( null, true, "/*" );
        
        servletContext.addListener(new RequestContextListener());
        
        logger.info( "**** Active Profile:"+ System.getProperty("spring.profiles.active") );
        
        if(System.getProperty("spring.profiles.active") == null)
        {
                logger.info( "**** Activated  prod Profile" );
                System.setProperty("spring.profiles.active", "prod");
        }
    }
}