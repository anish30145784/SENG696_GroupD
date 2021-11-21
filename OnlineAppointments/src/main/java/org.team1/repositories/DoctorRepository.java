package org.team1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team1.models.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Doctor findByUsername(String username);

    Optional<Doctor> findById(String id);

    List<Doctor> findDoctorsBySpecialtyNameEquals(String specName);


}
