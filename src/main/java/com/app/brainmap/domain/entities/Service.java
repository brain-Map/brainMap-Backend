package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID serviceId;

    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String serviceDescription;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServicePackage> packages;
}
