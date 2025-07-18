package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjectPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "domain_experts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DomainExperts {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name= "status", nullable = false, updatable = true)
    private Integer status;

    @Column(name = "domain", nullable = false, updatable = true)
    private String domain;

    @Column(name = "location", nullable = false, updatable = true)
    private String location;

    @Column(name = "rating", nullable = false, updatable = true)
    private String rating;

    @Column(name = "about", nullable = true, updatable = true)
    private String about;

    @OneToOne
    @MapsId  // This tells JPA that this entity uses the same ID as the user
    @JoinColumn(name = "id") // FK and PK
    private User user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DomainExperts that = (DomainExperts) o;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status) && Objects.equals(domain, that.domain) && Objects.equals(location, that.location) && Objects.equals(rating, that.rating) && Objects.equals(about, that.about) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, domain, location, rating, about, user);
    }
}
