package org.team1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.AppointmentNotFoundException;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.repositories.AppointmentRepository;
import org.team1.services.AppointmentService;
import org.team1.services.ClientService;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private AppointmentService appointmentService;
    private ClientService clientService;

    @Autowired
    public AppointmentController(AppointmentRepository appointmentRepository,
                                 AppointmentService appointmentService, ClientService clientService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.clientService =clientService;
    }

    @GetMapping("/appointment/all")
    public List<Appointment> getAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointment/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id); }

    @PostMapping("/appointment/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@Valid @RequestBody Appointment appointment, Principal principal) {
        Client client = clientService.findByUserName(principal.getName());
        return appointmentService.createAppointment(appointment,client);
    }

    @GetMapping("/appointment/all/client")
    public List<Appointment> getAppointmentsByClient(Principal principal) {
        return appointmentService.getAppointmentsByClientUsername(principal.getName());
    }

    @GetMapping("/appointment/all/doctor")
    public List<Appointment> getAppointmentsByDoctorUsername(Principal principal) {
        return appointmentService.getAppointmentsByDoctorUsername(principal.getName());
    }

    @GetMapping("/appointment/all/date-specialty")
    public List<Appointment> getAppointmentsBetweenDatesAndBySpecialty(
            Principal principal,
            @RequestParam(name = "startdate") String startDate,
            @RequestParam(name = "enddate") String endDate,
            @RequestParam(name = "specialty")String specName) throws ParseException {

        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        Date startingDate = format.parse(startDate);
        Date endingDate = format.parse(endDate);

        List<Appointment> listA = appointmentService.getAppointmentsByClientUsername(principal.getName());
        List<Appointment> listB = appointmentService.getAppointmentsBetweenDatesAndBySpecialty(startingDate, endingDate, specName);

        listA.retainAll(listB);


        return listA;
    }

    @GetMapping("/appointment/all/date-desc")
    public List<Appointment> getAppointmentsBetweenDatesOrBySpecialty(
            Principal principal,
            @RequestParam(name = "startdate") String startDate,
            @RequestParam(name = "enddate") String endDate,
            @RequestParam(name = "description")String desc) throws ParseException {

        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        Date startingDate = format.parse(startDate);
        Date endingDate = format.parse(endDate);

        List<Appointment> listA = appointmentService.getAppointmentsByDoctorUsername(principal.getName());
        List<Appointment> listB = appointmentService.getAppointmentsBetweenOrByDescription(startingDate, endingDate, desc);

        listA.retainAll(listB);

        return listA;
    }

    @PutMapping("/appointment/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment updateAppointment) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setDateTime(updateAppointment.getDateTime());
                    appointment.setDescription(updateAppointment.getDescription());
                    appointment.setNotes(updateAppointment.getNotes());
                    return appointmentRepository.save(appointment);
                })
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    @DeleteMapping("appointment/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        getAppointment(id);
        appointmentRepository.deleteById(id);
    }
}
