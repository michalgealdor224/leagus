package com.ashcollege;

import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.Set;

import static com.ashcollege.utils.Constants.DB_PASSWORD;
import static com.ashcollege.utils.Constants.DB_USERNAME;


@Configuration
@Profile("production")
public class AppConfig {


    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/ash2024?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setMaxPoolSize(20);
        dataSource.setMinPoolSize(5);
        dataSource.setIdleConnectionTestPeriod(3600);
        dataSource.setTestConnectionOnCheckin(true);
        return dataSource;
    }
    @Bean
    public Properties dataSource1() throws Exception {
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        settings.put(Environment.URL, "jdbc:mysql://localhost:3306/ash2024?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        settings.put(Environment.USER, DB_USERNAME);
        settings.put(Environment.PASS, DB_PASSWORD);
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, true);
        return settings;
    }
//    @Bean
//    public LocalSessionFactoryBean sessionFactory1() throws Exception {
//        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource());
//        Properties hibernateProperties = new Properties();
//        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
//        hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
//        hibernateProperties.put("hibernate.jdbc.batch_size", 50);
//        hibernateProperties.put("hibernate.connection.characterEncoding", "utf8");
//        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
//        sessionFactoryBean.setHibernateProperties(hibernateProperties);
//        sessionFactoryBean.setMappingResources("objects.hbm.xml");
//        return sessionFactoryBean;
//    }
    @Bean
    public SessionFactory sessionFactory() throws Exception {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.setProperties(dataSource1());
        Set<Class<? extends Object>> entities = new Reflections("com.dev.objects").getSubTypesOf(Object.class);
        for (Class<? extends Object> clazz : entities) {
            configuration.addAnnotatedClass(clazz);
        }
        configuration.addAnnotatedClass(Game.class);
        configuration.addAnnotatedClass(Team.class);
        configuration.addAnnotatedClass(User.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Bean
    public HibernateTransactionManager transactionManager() throws Exception{
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        return transactionManager;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

}
