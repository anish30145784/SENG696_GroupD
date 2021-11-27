package org.team1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.DoctorNotFoundException;
import org.team1.models.Client;
import org.team1.models.Doctor;
import org.team1.models.Feedback;
import org.team1.models.FeedbackDto;
import org.team1.repositories.ClientRepository;
import org.team1.repositories.DoctorRepository;
import org.team1.repositories.FeedbackRepository;
import org.team1.services.DoctorService;
import org.team1.utils.PdfUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController

public class DoctorController {
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    public DoctorController(DoctorRepository clientRepository, DoctorService doctorService) {
        this.doctorRepository = clientRepository;
        this.doctorService = doctorService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/feedback")
    public Feedback feedback(@RequestBody FeedbackDto feedback) {
        System.out.println(feedback.toString());
        Feedback feedback1 = new Feedback();
        Client client = clientRepository.findClientByEmailEquals(feedback.getPatientEmail());
        Doctor doctor = doctorRepository.findByFirstNameAndEmail(feedback.getFirstName(), feedback.getEmail());
        feedback1.setFeedback(feedback.getFeedback());
        feedback1.setDoctor(doctor);
        feedback1.setClient(client);

        return feedbackRepository.save(feedback1);
    }

    @GetMapping(value = "/feedback/pdf/{email}", produces =
            MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> employeeReport(@PathVariable("email") String email)
            throws IOException {
        List<Feedback> employees = feedbackRepository.findByDoctor_Email(email);

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
}

