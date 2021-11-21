package org.team1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.team1.exceptions.ClientAmkaExistsException;
import org.team1.exceptions.ClientEmailExistsException;
import org.team1.exceptions.ClientNotFoundException;
import org.team1.exceptions.ClientParamsException;
import org.team1.models.Client;

import org.team1.repositories.ClientRepository;
import org.team1.services.ClientService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ClientController {

    private final ClientRepository clientRepository;
    private ClientService clientService;

    @Autowired
    public ClientController(ClientRepository clientRepository, ClientService clientService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Client registerAccount(@Valid @RequestBody Client client) throws ClientEmailExistsException, ClientAmkaExistsException, ClientParamsException {
        return clientService.registerUserIfIsValid(client);
    }

    @GetMapping("/clients")
    public List<Client> getClients() { return clientRepository.findAll(); }

    @GetMapping("/clients/{id}") //done
    public Client getClient(@PathVariable String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @PutMapping("/clients/{id}")
    public Client updateClient(@PathVariable String id, @PathVariable Client updateClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setUsername(updateClient.getUsername());
                    return clientRepository.save(client);
                })
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @DeleteMapping("clients/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable String id) {
        getClient(id);
        clientRepository.deleteById(id);
    }
}

