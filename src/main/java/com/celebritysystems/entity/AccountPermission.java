package com.celebritysystems.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Data
@Embeddable
public class AccountPermission {
    private String name;
    private boolean canEdit;
    private boolean canRead;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountPermission)) return false;
        AccountPermission that = (AccountPermission) o;
        return canEdit == that.canEdit &&
               canRead == that.canRead &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, canEdit, canRead);
    }
}
