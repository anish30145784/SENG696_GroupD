package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.team1.models.Appointment;
import org.team1.repositories.AppointmentRepository;

import java.sql.*;

@Service
public class JadeAgent extends Agent {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String url = "jdbc:mysql://localhost:3306/appointment?useSSL=false";
    String username = "root";
    String password = "anish@123";
    Connection connection = null;
    @Override
    public void setup() {
        System.out.println("hellooo");



        System.out.println("Connecting database...");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try  {
             connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch ( SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                try {
                   System.out.println("hello");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
