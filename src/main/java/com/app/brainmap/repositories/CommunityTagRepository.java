package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, UUID> {
    @Query("SELECT t FROM CommunityTag t LEFT JOIN FETCH t.posts")
    List<CommunityTag> findAllWithPostCount();

    @Query("SELECT t FROM CommunityTag t WHERE SIZE(t.posts) > 0 ORDER BY SIZE(t.posts) DESC")
    List<CommunityTag> findPopularTagsOrderedByPostCount();

    List<CommunityTag> findByNameIn(Set<String> names);
}
