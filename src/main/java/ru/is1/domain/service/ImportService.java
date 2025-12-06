package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.is1.dal.dao.ImportDAO;
import ru.is1.dal.entity.*;
import ru.is1.domain.validate.PersonValidator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportDAO importDAO;

    @Inject
    private JsonService jsonService;

    @Inject
    private ImportHelpService importHelpService;

    @Transactional
    public long getTotalEntitiesCount() {
        return importDAO.getTotalCount();
    }

    @Transactional
    public List<UserImport> getEntitiesPaginated(int first, int size, String field, String direction) {
        return importDAO.findWithPagination(first, size, field, direction);
    }

    @Transactional
    public List<UserImport> getAllImports() {
        return importDAO.findAll();
    }

    public UserImport importFromJson(InputStream fileInputStream) {
        UserImport userImport = new UserImport();
        userImport.setCreationDate(LocalDateTime.now());

        try {
            Person[] personsData = jsonService.fromJson(fileInputStream, Person[].class);
            List<String> errors = PersonValidator.validatePersonsData(personsData);

            if (!errors.isEmpty()) {
                userImport.setCount(0);
                userImport.setStatus(ImportStatus.ERROR.name());
                userImport.setErrors("Валидация: " + String.join(", ", errors));
                return importHelpService.saveImport(userImport);
            }

            int importedCount = importHelpService.massSave(personsData);

            userImport.setCount(importedCount);
            userImport.setStatus(ImportStatus.SUCCESS.name());
            return importHelpService.saveImport(userImport);

        } catch (Exception e) {
            userImport.setCount(0);
            userImport.setStatus(ImportStatus.ERROR.name());
            userImport.setErrors(e.getMessage());
            return importHelpService.saveImport(userImport);
        }
    }
}