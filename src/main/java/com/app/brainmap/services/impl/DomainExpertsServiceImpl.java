package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListingAvailability;
import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ServiceRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }

    @Override
    public void createdemoService() {
        ServiceListing service = ServiceListing.builder()
                .title("quantum physics")
                .fee(55.00)
                .subject("physics")
                .description("all about quantum physics")
                .build();

        ServiceListingAvailability monday = ServiceListingAvailability.builder()
                .dayOfWeek(1)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(12,0))
                .build();

        ServiceListingAvailability wednesday = ServiceListingAvailability.builder()
                .dayOfWeek(3)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(12,0))
                .build();

        serviceRepository.save(service);
    }

}
