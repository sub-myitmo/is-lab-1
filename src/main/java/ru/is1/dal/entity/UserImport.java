package ru.is1.dal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.Identifiable;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
public class UserImport implements Identifiable {
    private Long id;

    private ImportStatus status;
    private int count;
    private String errors;
    private LocalDateTime creationDate;


    public String getStatus() {
        return status != null ? status.name() : null;
    }

    public void setStatus(String status) {
        this.status = status != null ? ImportStatus.valueOf(status) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImport userImport = (UserImport) o;
        return status == userImport.status && count == userImport.count && Objects.equals(errors, userImport.errors) && creationDate == userImport.creationDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, errors, count, creationDate);
    }

    @Override
    public String toString() {
        return "UserImport{" + "status=" + status.getDisplayName() + ", count=" + count + ", errors=" + errors + ", creationDate=" + creationDate + '}';
    }
}
