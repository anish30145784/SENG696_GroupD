package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ClinicAgent extends Agent {

    String url = "jdbc:mysql://localhost:3306/mydatabase?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;

    @Autowired
    private JdbcTemplate appointmentRepository;

    @Override
    public void setup() {

        System.out.println("Connecting database...");


        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database connected!");


        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                try {
                    connection.prepareStatement("delete from appointment where deleted = 1").execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
