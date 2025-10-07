package ru.is1.config.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.ws.rs.Produces;
import jdk.jfr.Name;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;

@ApplicationScoped
public class HibernateSessionFactory {

    private SessionFactory sessionFactory;

    @PostConstruct
    public void init() {
        try {
            Configuration configuration = new Configuration();
            // Укажите параметры подключения
            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url",
                    "jdbc:postgresql://" + System.getenv("DB_HOST") + ":" +
                            System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME"));
            configuration.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
            configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", System.getenv("DB_TYPE"));
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "true");

            // Регистрируем кастомные типы через MetadataBuilder
            MetadataSources metadataSources = new MetadataSources(
                    new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties())
                            .build()
            );

            metadataSources.addResource("Person.hbm.xml");
            metadataSources.addResource("Location.hbm.xml");
            metadataSources.addResource("Coordinates.hbm.xml");

            MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

            // Регистрируем кастомные типы
            metadataBuilder.applyBasicType(new GenericEnumType<>(Color.class), "color_enum");
            metadataBuilder.applyBasicType(new GenericEnumType<>(Country.class), "country_enum");

            Metadata metadata = metadataBuilder.build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create SessionFactory", e);
        }
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    @PreDestroy
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}