package ru.is1.config.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@ApplicationScoped
public class HibernateSessionFactory {

    private SessionFactory sessionFactory;

    @PostConstruct
    public void init() {
        try {
            // Получаем DataSource из JNDI WildFly
            DataSource ds = (DataSource) new InitialContext().lookup("java:/PersonDS");

            // Hibernate Configuration
            var configuration = new org.hibernate.cfg.Configuration();
            configuration.setProperty("hibernate.hbm2ddl.auto", "none");
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "true");

            // Используем DataSource WildFly напрямую
            configuration.getProperties().put("hibernate.connection.datasource", ds);
            configuration.setProperty("hibernate.transaction.jta.platform",
                    JBossAppServerJtaPlatform.class.getName());
            configuration.setProperty("hibernate.current_session_context_class", "jta");
            configuration.setProperty("hibernate.transaction.coordinator_class", "jta");

            // Регистрируем mapping XML
            MetadataSources metadataSources = new MetadataSources(
                    new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties())
                            .build()
            );
            metadataSources.addResource("Person.orm.xml");
            metadataSources.addResource("Location.orm.xml");
            metadataSources.addResource("Coordinates.orm.xml");

            MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
            Metadata metadata = metadataBuilder.build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup DataSource", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SessionFactory", e);
        }
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @PreDestroy
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
