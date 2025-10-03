package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotesRepository extends JpaRepository <Notes, UUID>{
    // Additional query methods can be defined here if needed
}
