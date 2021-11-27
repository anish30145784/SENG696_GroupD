package org.team1.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team1.exceptions.AppointmentNotFoundException;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.repositories.AppointmentRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, DoctorService doctorService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
    }

    public Appointment createAppointment(Appointment appointment, Client client) {

        Appointment newAppointment = new Appointment();

        newAppointment.setClient(client);
        newAppointment.setDoctor(doctorService.findDoctorByAmka(appointment.getDoctor().getId()));
        newAppointment.setDateTime(appointment.getDateTime());
        newAppointment.setDescription(appointment.getDescription());
        newAppointment.setNotes(appointment.getNotes());
        newAppointment.setCriticality(appointment.getCriticality());
        newAppointment.setStatus("NOT_SCHEDULED");
        newAppointment.setDeleted(Boolean.FALSE);

        appointmentRepository.save(newAppointment);
        return newAppointment;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll().stream().filter(d -> d.getDeleted() == Boolean.FALSE).collect(Collectors.toList());
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).filter(d -> d.getDeleted() == Boolean.FALSE)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    public List<Appointment> getAppointmentsByClientUsername(String username) {
        return appointmentRepository.findAppointmentsByClientUsernameEquals(username).stream().filter(d -> d.getDeleted() == Boolean.FALSE).collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctorUsername(String username) {
        return appointmentRepository.findAppointmentsByDoctorUsernameEquals(username).stream().filter(d -> d.getDeleted() == Boolean.FALSE).collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsBetweenDatesAndBySpecialty(Date startDate, Date endDate, String specName) {
        return appointmentRepository.findAppointmentsByDateTimeBetweenAndDoctor_Specialty_Name(startDate, endDate, specName).stream().filter(d -> d.getDeleted() == Boolean.FALSE).collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsBetweenOrByDescription(Date startDate, Date endDate, String desc) {
        return appointmentRepository.findAppointmentsByDateTimeBetweenAndDescriptionContaining(startDate, endDate, desc);
    }


    public Appointment completeAppointment(String name, Long id) {
        Appointment appointment = appointmentRepository.findById(id).get();
        appointment.setStatus("Completed");
        return appointmentRepository.save(appointment);
    }
}
