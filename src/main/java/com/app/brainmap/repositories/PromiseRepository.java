package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Promise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromiseRepository extends JpaRepository<Promise, UUID> {
}