package ru.is1.config.utils;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.is1.dal.entity.Person;
import ru.is1.dal.entity.Location;
import ru.is1.dal.entity.Coordinates;
//import ru.is1.dal.entity.User;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            // Укажите параметры подключения
            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://" + System.getenv("DB_HOST") + ":" + System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME"));
            configuration.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
            configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", System.getenv("DB_TYPE"));
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "true");

            // Явно зарегистрируйте сущности
            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(Location.class);
            configuration.addAnnotatedClass(Coordinates.class);
//            configuration.addAnnotatedClass(User.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to create SessionFactory: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
