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
public class UserTaskCompositeKey implements Serializable {
    @Column(name = "user_id")
    UUID userId;

    @Column(name = "task_id")
    UUID taskId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTaskCompositeKey)) return false;
        UserTaskCompositeKey that = (UserTaskCompositeKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return  Objects.hash(userId, taskId);
    }

}
