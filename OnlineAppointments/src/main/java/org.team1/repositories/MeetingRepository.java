package org.team1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team1.models.MeetingData;

public interface MeetingRepository extends JpaRepository<MeetingData, Long> {
}
