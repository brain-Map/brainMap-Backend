package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.ProfileVisibility;
import lombok.Data;

@Data
public class NotificationSettingsDTO {
    // Email notifications
    private Boolean emailProjectUpdates;
    private Boolean emailExpertMessages;
    private Boolean emailDeadlineReminders;
    private Boolean emailCollaborationInvites;
    private Boolean emailSystemUpdates;

    // Push notifications
    private Boolean pushInstantMessages;
    private Boolean pushMeetingReminders;
    private Boolean pushTaskDeadlines;
    private Boolean pushExpertAvailability;

    // In-app notifications
    private Boolean inAppNewProjects;
    private Boolean inAppComments;
    private Boolean inAppMentions;
    private Boolean inAppFileSharing;
}

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