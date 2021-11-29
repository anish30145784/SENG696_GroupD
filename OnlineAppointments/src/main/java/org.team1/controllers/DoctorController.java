package org.team1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.DoctorNotFoundException;
import org.team1.models.*;
import org.team1.repositories.AppointmentRepository;
import org.team1.repositories.ClientRepository;
import org.team1.repositories.DoctorRepository;
import org.team1.repositories.FeedbackRepository;
import org.team1.services.DoctorService;
import org.team1.utils.CommonUtils;
import org.team1.utils.Constrants;
import org.team1.utils.PdfUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

public class DoctorController {
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    public DoctorController(DoctorRepository clientRepository, DoctorService doctorService) {
        this.doctorRepository = clientRepository;
        this.doctorService = doctorService;
    }


    @PostMapping(value = "/feedback")
    public Feedback feedback(@RequestBody FeedbackDto feedback) {
        System.out.println(feedback.toString());
        Feedback feedback1 = new Feedback();
        Client client = clientRepository.findClientByEmailEquals(feedback.getPatientEmail());
        Doctor doctor = doctorRepository.findByFirstNameAndEmail(feedback.getFirstName(), feedback.getEmail());
        feedback1.setFeedback(feedback.getFeedback());
        feedback1.setDoctor(doctor);
        feedback1.setClient(client);
        feedback1.setCreatedDate(Instant.now());

        return feedbackRepository.save(feedback1);
    }

    @GetMapping(value = "/feedback", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String feedback(@RequestParam("dName") String dName, @RequestParam("dEmail") String dEmail, @RequestParam("pName") String pName, @RequestParam("pEmail") String pEmail) {


        Map<String, String> map = new HashMap<>();
        map.put(Constrants.DOCTOR_NAME, dName);
        map.put(Constrants.DOCTOR_EMAIl, dEmail);
        map.put(Constrants.PATIENT_NAME, pName);
        map.put(Constrants.PATIENT_EMAIL, pEmail);

        String body = CommonUtils.replaceText(Constrants.EMAIl_TEMPLATE_FEEDBACK, map);
        return body;
    }
    //  , produces = MediaType.TEXT_HTML_VALUE

    @GetMapping(value = "/feedback/pdf/{email}", produces =
            MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> employeeReport(@PathVariable("email") String email)
            throws IOException {
        System.out.println("email : " + email);
        Appointment appointment = appointmentRepository.findAppointmentsByDoctorEmailEqualsOrderByIdDesc(email).stream().findFirst().get();
        List<Feedback> employees = feedbackRepository.findByDoctor_EmailOrderByIdDesc(email);

        ByteArrayInputStream bis = PdfUtil.employeePDFReport(employees);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=employees.pdf");

        return ResponseEntity.ok().headers(headers).contentType
                        (MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/doctor/all")
    public List<Doctor> getDoctor() {
        return doctorRepository.findAll();
    }

    @GetMapping("/doctor/{id}")
    public Doctor getDoctor(@PathVariable String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    @PostMapping("/doctor")
    public Doctor newDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }


    @GetMapping("/doc/all/spec/{specName}")
    public List<Doctor> getDoctorsBySpecialty(@PathVariable String specName) {
        return doctorService.getDoctorsWithSpecialty(specName);
    }

    @PutMapping("/doctor/{id}")
    public Doctor updateDoctor(@PathVariable String id, @RequestBody Doctor updateDoctor) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setUsername(updateDoctor.getUsername());
                    return doctorRepository.save(doctor);
                })
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    @DeleteMapping("doctor/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable String id) {
        getDoctor(id);
        doctorRepository.deleteById(id);
    }

    @GetMapping("/doctor/user/{userName}") //done
    public Doctor getDoctorByUsername(@PathVariable String userName) {
        return doctorRepository.findByUsername(userName);
    }
}

