package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListingAvailability;
import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ServiceListingRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }

    @Override
    public void createdemoService() {

//        fetch mentor from user repository
        UUID mentorUUID = UUID.fromString("7b06b1e7-5c3f-4b14-84d7-99b70ccffaa3");
        User mentor = userRepository.findById(mentorUUID).orElseThrow(() -> new RuntimeException(("Mentor not found with ID: " + mentorUUID)));

        ServiceListing service = ServiceListing.builder()
                .title("quantum physics")
                .subject("physics")
                .description("all about quantum physics")
                .fee(55.00)
                .mentor(mentor)
                .build();

        ServiceListingAvailability monday = ServiceListingAvailability.builder()
                .dayOfWeek(1)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(12,0))
                .service(service)
                .build();

        ServiceListingAvailability wednesday = ServiceListingAvailability.builder()
                .dayOfWeek(3)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(12,0))
                .service(service)
                .build();

//        attach availability to service
        service.setAvailabilities(List.of(monday, wednesday));
//        save service listing
        serviceListingRepository.save(service);
    }

}
