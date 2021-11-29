package org.team1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team1.models.Appointment;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Appointment save(Appointment appointment);

    List<Appointment> findAppointmentsByClientUsernameEquals(String username);

    List<Appointment> findAppointmentsByDoctorUsernameEquals(String username);

    List<Appointment> findAppointmentsByDateTimeBetweenAndDoctor_Specialty_Name(Date startDate, Date endDate, String specName);

    List<Appointment> findAppointmentsByDateTimeBetweenAndDescriptionContaining(Date startDate, Date endDate, String description);


    //@Query(value = "select a from Appointment a where a.client.username = :username and a.isDeleted = 0 order by a.datetime desc")
    List<Appointment> findAppointmentsByClientUsernameEqualsOrderByIdDesc(String username);

    // @Query(value = "select a from Appointment a where a.doctor.username = :username and a.isDeleted = 0 order by a.datetime desc")
    List<Appointment> findAppointmentsByDoctorUsernameEqualsOrderByIdDesc(String username);

    List<Appointment> findAppointmentsByDoctorEmailEqualsOrderByIdDesc(String username);
}
