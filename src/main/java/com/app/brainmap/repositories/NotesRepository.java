package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotesRepository extends JpaRepository<Notes, UUID> {

    // Find notes by the user's id (user.id)
    List<Notes> findByUser_Id(UUID userId);

}
