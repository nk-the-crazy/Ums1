package kg.ums.core.config.db;


import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(  {"kg.demirbank.fastpay.core.*.dao"})
@PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true)
public class DbConfig
{
    
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";

    private static final String PROPERTY_NAME_POOLNAME = "db.poolName";
    private static final String PROPERTY_NAME_MINPOOLSIZE = "db.minPoolSize";
    private static final String PROPERTY_NAME_MAXPOOLSIZE = "db.maxPoolSize";
    private static final String PROPERTY_NAME_AUTOCOMMITONCLOSE = "db.autoCommitOnClose";
    //private static final String PROPERTY_NAME_PREFERREDTESTQUERY = "db.preferredTestQuery";
    private static final String PROPERTY_NAME_MAXIDLETIME = "db.maxIdleTime";
   
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

    @Resource
    private Environment env;

    
    /* *************************************
     * HikariDataSource
     */
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() throws IllegalStateException, PropertyVetoException
    {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName( env.getRequiredProperty( PROPERTY_NAME_DATABASE_DRIVER ) );
        dataSource.setJdbcUrl( env.getRequiredProperty( PROPERTY_NAME_DATABASE_URL ) );
        dataSource.setUsername( env.getRequiredProperty( PROPERTY_NAME_DATABASE_USERNAME ) );
        dataSource.setPassword( env.getRequiredProperty( PROPERTY_NAME_DATABASE_PASSWORD ) );
        dataSource.setAutoCommit( Boolean.parseBoolean( env.getRequiredProperty( PROPERTY_NAME_AUTOCOMMITONCLOSE ) ) );
        dataSource.setMaximumPoolSize( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_MAXPOOLSIZE ) ) );
        dataSource.setMinimumIdle( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_MINPOOLSIZE ) ) );
        //dataSource.setConnectionTestQuery( env.getRequiredProperty( PROPERTY_NAME_PREFERREDTESTQUERY ) );
        dataSource.setIdleTimeout( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_MAXIDLETIME ) ) );
        dataSource.setPoolName( env.getRequiredProperty( PROPERTY_NAME_POOLNAME ) );
        dataSource.addDataSourceProperty( "cachePrepStmts", "true" );
        dataSource.addDataSourceProperty( "prepStmtCacheSize", "250" );
        dataSource.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );

        return dataSource;
    }

    /* *************************************
     * EntityManagerFactory
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
                    throws IllegalStateException, PropertyVetoException
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("FSTP");
        em.setDataSource( dataSource() );
        em.setJpaVendorAdapter( jpaVendorAdapter() );    
        em.setJpaProperties( hibernateProperties() );
        em.setPackagesToScan(new String[] { "kg.demirbank.fastpay.core.*.model"});
        
        return em;
    }
    
    /* *************************************
     * Provider specific adapter.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() 
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        return hibernateJpaVendorAdapter;
    }
    

    /* *************************************
     * PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager( EntityManagerFactory emf )
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( emf );

        return transactionManager;
    }
    
    /* *************************************
     * 
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation()
    {
       return new PersistenceExceptionTranslationPostProcessor();
    }
   

    public Properties hibernateProperties()
    {
        Properties properties = new Properties();
        properties.put( PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_DIALECT ) );
        properties.put( PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_SHOW_SQL ) );
        properties.put( PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                        env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_FORMAT_SQL ) );
        properties.put( PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
                        env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO ) );
        properties.put( PROPERTY_NAME_HIBERNATE_DEFAULT_SCHEMA,
                        env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_DEFAULT_SCHEMA ) );

        return properties;
    }

}
