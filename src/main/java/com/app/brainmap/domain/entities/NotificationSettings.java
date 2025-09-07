package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Email notifications
    private Boolean emailProjectUpdates = true;
    private Boolean emailExpertMessages = true;
    private Boolean emailDeadlineReminders = true;
    private Boolean emailCollaborationInvites = true;
    private Boolean emailSystemUpdates = false;

    // Push notifications
    private Boolean pushInstantMessages = true;
    private Boolean pushMeetingReminders = true;
    private Boolean pushTaskDeadlines = true;
    private Boolean pushExpertAvailability = false;

    // In-app notifications
    private Boolean inAppNewProjects = true;
    private Boolean inAppComments = true;
    private Boolean inAppMentions = true;
    private Boolean inAppFileSharing = true;
}