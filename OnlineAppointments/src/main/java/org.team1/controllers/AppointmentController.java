package org.team1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.AppointmentNotFoundException;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.MeetingData;
import org.team1.repositories.AppointmentRepository;
import org.team1.repositories.MeetingRepository;
import org.team1.services.AppointmentService;
import org.team1.services.ClientService;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
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
    private MeetingRepository meetingRepository;

    @Autowired
    public AppointmentController(AppointmentRepository appointmentRepository,
                                 AppointmentService appointmentService, ClientService clientService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.clientService = clientService;
    }

    @GetMapping("/appointment/all")
    public List<Appointment> getAppointments() {
        System.out.println("Inside GetAppointments call of Controller!");
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointment/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @PostMapping("/appointment/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@Valid @RequestBody Appointment appointment, Principal principal) {
        Client client = clientService.findByUserName(principal.getName());
        return appointmentService.createAppointment(appointment, client);
    }


    @GetMapping("/appointment/all/client")
    public List<Appointment> getAppointmentsByClient(Principal principal) {
        return appointmentService.getAppointmentsByClientUsername(principal.getName());
    }

    @GetMapping("/appointment/all/doctor")
    public List<Appointment> getAppointmentsByDoctorUsername(Principal principal) {
        return appointmentService.getAppointmentsByDoctorUsername(principal.getName());
    }

    @GetMapping("/appointment/complete/{id}")
    public Appointment setAppointmentCompletion(Principal principal, @PathVariable("id") Long id) {
        return appointmentService.completeAppointment(principal.getName(), id);
    }

    @GetMapping("/appointment/all/date-specialty")
    public List<Appointment> getAppointmentsBetweenDatesAndBySpecialty(
            Principal principal,
            @RequestParam(name = "startdate") String startDate,
            @RequestParam(name = "enddate") String endDate,
            @RequestParam(name = "specialty") String specName) throws ParseException {

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
            @RequestParam(name = "description") String desc) throws ParseException {

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
        System.out.println("Inside Update Mapping of Appointment Controller ");
        System.out.println("ID of updateAppointment :" + id);
        System.out.println("Time of updateAppointment :" + updateAppointment.getDateTime());
        System.out.println("Timestamp of updateAppointment :" + new Timestamp(updateAppointment.getDateTime().getTime()));
        List<Appointment> a = getAppointments();
        System.out.println("Size of all appointment array : " + a.size());
        a.removeIf(Appointment -> Appointment.getId() == id);
        System.out.println("Size of updated appointment array : " + a.size());
        Date newDt = updateAppointment.getDateTime();
        System.out.println("Update Appointment Time : " + updateAppointment.getDateTime().getTime());
        boolean state = false;
        for (int i = 0; i < a.size() && state == false; i++) {
            System.out.println("DateTime of Other Appointment : #" + (i + 1) + " | ID : " + a.get(i).getId() + " | Date Time : " + a.get(i).getDateTime().toString());
            if (new Timestamp(a.get(i).getDateTime().getTime()).toString().equals(new Timestamp(updateAppointment.getDateTime().getTime()).toString())) {
                System.out.println("Match Found !");
                newDt = generateAvailableDate(a, updateAppointment, id);
                state = true;
            } else
                continue;
        }
        System.out.println("New Suggested Date : " + newDt);
        Date finalNewDt = newDt;
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setDateTime(finalNewDt);
                    appointment.setDescription(updateAppointment.getDescription());
                    appointment.setNotes(updateAppointment.getNotes());
                    appointment.setUpdated(true);
                    appointment.setUpdatedMail(false);
                    MeetingData meetingData = meetingRepository.findAll().get(0);
                    System.out.println("Inside Appointment Controller : updateAppointment ! ");
                    // EmailUtils.main(appointment.getDoctor().getEmail(), "Appointment Updated", "Dear User,"
                    //   + "<br> your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() +
                    //    "<br> At " + appointment.getDateTime() + " <br> url -" + meetingData.getUrl() +
                    //     "<br> meeting id - " + meetingData.getMeetingId() +
                    //   "<br> password - " + meetingData.getPassword());
                    return appointmentRepository.save(appointment);
                })
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    @DeleteMapping("appointment/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        Appointment appointment = getAppointment(id);
        appointment.setDeleted(true);
        appointmentRepository.save(appointment);
    }


    public Date generateAvailableDate(List<Appointment> a, Appointment updateApp, long id) {
        System.out.println("Inside generateAvailableDate function !");
        String cri = getAppointment(id).getCriticality().toString();
        System.out.println("Update App Criticality : " + cri.toString());
        //int hoursTillnxtDay9am = (int) (Math.floor(updateApp.getDateTime().getTime() / (1000 * 60 * 60)) % 24) <= 0 ? 14 + ((int) Math.floor((updateApp.getDateTime().getTime() / (1000 * 60 * 60)) % 24)) : 33 - ((int) Math.floor((updateApp.getDateTime().getTime() / (1000 * 60 * 60)) % 24));
        int hoursTillnxtDay9am = 33 - ((int) Math.floor((updateApp.getDateTime().getTime() / (1000 * 60 * 60)) % 24));
        System.out.println("hoursTillnxtDay9am : " + hoursTillnxtDay9am);
        Date startDate = cri.equals("URGENT") ? new Date(updateApp.getDateTime().getTime() + (1 * 60 * 60 * 1000)) : new Date(updateApp.getDateTime().getTime() + (hoursTillnxtDay9am * 3600000));
        System.out.println("New Start Date based on Criticality : " + startDate);
        while (true) {
            System.out.println("Start Date timestamp : " + startDate.getTime());
            if ((Math.floor((startDate.getTime() / (1000 * 60 * 60)) % 24)) == 19) {
                startDate = new Date(startDate.getTime() + (14 * 60 * 60 * 1000));
                System.out.println("Next day start time : " + startDate);
            }

            if (a.contains(startDate)) {
                startDate = new Date(startDate.getTime() + (1 * 60 * 60 * 1000));
                System.out.println("New Start date : " + startDate);
            } else {
                break;
            }


        }


        return startDate;
    }
}
