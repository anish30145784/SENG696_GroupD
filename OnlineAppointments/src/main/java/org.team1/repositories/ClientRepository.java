package org.team1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team1.models.Client;

public interface ClientRepository extends JpaRepository<Client, String> {

    Client save(Client client);

    Client findByUsername(String username);

    Client findClientByIdEquals(String id);

    Client findClientByEmailEquals(String email);

}
