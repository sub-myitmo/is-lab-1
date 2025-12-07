package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.UserImport;

import java.util.List;

@ApplicationScoped
@MonitorPerformance
public class ImportDAO extends AbstractDAO<UserImport> {

    public ImportDAO() {
        super(UserImport.class);
    }

    @Override
    public void throwIfExistsAnyByEntity(UserImport entities) {
        return;
    }

    @Override
    public boolean existsAnyByEntity(List<UserImport> list) {
        return false;
    }
}
