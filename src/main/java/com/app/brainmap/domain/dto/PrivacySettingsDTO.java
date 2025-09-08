package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.ProfileVisibility;
import lombok.Data;

@Data
public class PrivacySettingsDTO {
    private ProfileVisibility profileVisibility;
    private Boolean showEmail;
    private Boolean showPhone;
    private Boolean showProjects;
    private Boolean showProgress;
    private Boolean allowContactFromExperts;
    private Boolean showOnlineStatus;
}
