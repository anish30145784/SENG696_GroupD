package org.team1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.DoctorNotFoundException;
import org.team1.models.Doctor;
import org.team1.repositories.DoctorRepository;
import org.team1.services.DoctorService;

import java.util.List;

@RestController
public class DoctorController {
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorRepository clientRepository, DoctorService doctorService) {
        this.doctorRepository = clientRepository;
        this.doctorService = doctorService;
    }

    @GetMapping("/doctor/all")
    public List<Doctor> getDoctor() {
        return doctorRepository.findAll();
    }

    @GetMapping("/doctor/{amka}")
    public Doctor getDoctor(@PathVariable String amka) {
        return doctorRepository.findById(amka)
                .orElseThrow(() -> new DoctorNotFoundException(amka));
    }

    @PostMapping("/doctor")
    public Doctor newDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }


    @GetMapping("/doc/all/spec/{specName}")
    public List<Doctor> getDoctorsBySpecialty(@PathVariable String specName){
        return doctorService.getDoctorsWithSpecialty(specName);
    }

    @PutMapping("/doctor/{amka}")
    public Doctor updateDoctor(@PathVariable String amka, @RequestBody Doctor updateDoctor) {
        return doctorRepository.findById(amka)
                .map(doctor -> {
                    doctor.setUsername(updateDoctor.getUsername());
                    return doctorRepository.save(doctor);
                })
                .orElseThrow(() -> new DoctorNotFoundException(amka));
    }

    @DeleteMapping("doctor/{amka}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable String amka) {
        getDoctor(amka);
        doctorRepository.deleteById(amka);
    }
}

