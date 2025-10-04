//package ru.is1.dal.dao;
//
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.hibernate.query.Query;
//import ru.is1.dal.entity.User;
//import ru.is1.config.utils.HibernateUtil;
//
//import java.util.Optional;
//
//public class UserDAO {
//
//    public Optional<User> findByUsername(String username) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
//            query.setParameter("username", username);
//            return query.uniqueResultOptional();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//
//    public Optional<User> findByEmail(String email) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
//            query.setParameter("email", email);
//            return query.uniqueResultOptional();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//
//    public boolean save(User user) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            session.persist(user);
//            transaction.commit();
//            return true;
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean existsByUsername(String username) {
//        return findByUsername(username).isPresent();
//    }
//
//    public boolean existsByEmail(String email) {
//        return findByEmail(email).isPresent();
//    }
//}
