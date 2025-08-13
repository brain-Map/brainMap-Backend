package com.app.brainmap.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectCompositeKey implements Serializable {
    @Column(name = "user_id")
    UUID userId;

    @Column(name = "project_id")
    UUID projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProjectCompositeKey)) return false;
        UserProjectCompositeKey that = (UserProjectCompositeKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return  Objects.hash(userId, projectId);
    }

}
