package kg.ums.web.config;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import kg.ums.core.config.cache.CacheConfig;
import kg.ums.core.config.db.DbConfig;



@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "kg.demirbank.psp.web.config",
                                "kg.demirbank.psp.web.controller", 
                                "kg.demirbank.psp.common.utils.system",
                                "kg.demirbank.psp.core.*.service"})
@Import({DbConfig.class, CacheConfig.class})
public class WebAppConfig extends WebMvcConfigurerAdapter
{
    
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    @Configuration
    @Profile("test")
    @PropertySource(value ={"classpath:config.properties","classpath:config-test.properties"}, ignoreResourceNotFound = true)
    static class Test
    {}
    
    @Configuration
    @Profile("prod")
    @PropertySource(value ={"classpath:config.properties"}, ignoreResourceNotFound = true)
    static class Prod
    {}
    
    @Value("${system.app.external.resource.location}")
    private String externalResource;
    
    
    // ---------- Web Settings --------------------------------
   
    @Bean
    public ViewResolver viewResolver()
    {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass( JstlView.class );
        viewResolver.setPrefix( "/WEB-INF/views/" );
        viewResolver.setSuffix( ".jsp" );
        return viewResolver;
    }

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry )
    {
        registry.addResourceHandler( "/assets/**" ).addResourceLocations( "/assets/" );
        registry.addResourceHandler( "/resources/**" ).addResourceLocations( "file:" + externalResource);
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
    

    /**
     * Configure TilesConfigurer.
     */
    @Bean
    public TilesConfigurer getTilesConfigurer() 
    {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setCheckRefresh(true);
        tilesConfigurer.setDefinitions(new String[] {"/WEB-INF/tiles.xml"});
        //tilesConfigurer.setDefinitionsFactoryClass(TilesDefConfig.class);
        //TilesDefConfig.addDefinitions();

        return tilesConfigurer;
    }

 
    /**
     * Configure ViewResolvers.
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) 
    {
        TilesViewResolver viewResolver = new TilesViewResolver();
        registry.viewResolver(viewResolver);
    }

    
    // ---------- i18n Settings --------------------------------
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() 
    {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        //converter.setWriteAcceptCharset(true);
        converter.setSupportedMediaTypes(Arrays.asList( new MediaType("text", "plain", UTF8),
                                                        new MediaType("text", "xml", UTF8)));
        
        return converter;
    }
    
    /*
    @Bean
    public MessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename( "classpath:i18n/messages" );
        messageSource.setDefaultEncoding( "UTF-8" );
        return messageSource;
    }*/

    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale( new Locale( "ru" ) );

        return resolver;
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry )
    {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName( "locale" );
        registry.addInterceptor( interceptor );
    }

    
    // ---------- Pageable --------------------------------
    public void addArgumentResolvers( List<HandlerMethodArgumentResolver> argumentResolvers )
    {
        PageableHandlerMethodArgumentResolver resolverWithSizeFive = new PageableHandlerMethodArgumentResolver();
        resolverWithSizeFive.setFallbackPageable( new PageRequest( 0, 5, Direction.DESC, "id" ) );
        argumentResolvers.add( resolverWithSizeFive );
    }
}