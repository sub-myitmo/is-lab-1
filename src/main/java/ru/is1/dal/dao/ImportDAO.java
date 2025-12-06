package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.UserImport;

import java.util.List;

@ApplicationScoped
@MonitorPerformance
public class ImportDAO extends AbstractDAO<UserImport> {

    public ImportDAO() {
        super(UserImport.class);
    }

    public List<UserImport> findAll() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(UserImport.class);
        var root = query.from(UserImport.class);

        query.select(root);
        return session.createQuery(query).list();
    }
}
