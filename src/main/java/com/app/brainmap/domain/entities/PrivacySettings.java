package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "privacy_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PrivacySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    private Boolean showEmail = false;
    private Boolean showPhone = false;
    private Boolean showProjects = true;
    private Boolean showProgress = true;
    private Boolean allowContactFromExperts = true;
    private Boolean showOnlineStatus = true;
}

