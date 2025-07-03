package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String serviceDescription;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServicePackage> packages;
}
