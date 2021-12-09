package org.team1.agent;

import jade.core.behaviours.TickerBehaviour;
import org.team1.models.Client;
import org.team1.models.Doctor;
import org.team1.models.Feedback;
import org.team1.utils.EmailUtils;

import java.sql.*;

public class PdfAgent extends EnhancedAgent {

    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;

    @Override
    public void setup() {
        System.out.println("PDF agent=============started");
        System.out.println("Connecting database inside PDF Agent...");
        register("pdf");
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
                    PreparedStatement statement = connection.prepareStatement("select * from feedback where mail_send is null");
                    statement.execute();
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        System.out.println("Inside while loop of Feedback Agent");
                        Feedback feedback = new Feedback();
                        feedback.setId(resultSet.getLong("id"));
                        feedback.setFeedback(resultSet.getString("feedback"));
                        PreparedStatement stat = connection.prepareStatement("SELECT * from client WHERE  id = ?");
                        stat.setLong(1, Long.parseLong(resultSet.getString("client_id")));
                        ResultSet rs = stat.executeQuery();
                        while (rs.next()) {
                            Client client = new Client();
                            client.setEmail(rs.getString("email"));
                            client.setUsername(rs.getString("username"));
                            client.setFirstName(rs.getString("first_name"));
                            client.setId(rs.getString("id"));
                            client.setPhone(Long.valueOf(rs.getString("phone")));
                            feedback.setClient(client);
                        }

                        PreparedStatement stat1 = connection.prepareStatement("SELECT * from doctor WHERE  id = ?");
                        stat1.setLong(1, Long.parseLong(resultSet.getString("doctor_id")));
                        ResultSet rs1 = stat1.executeQuery();
                        while (rs1.next()) {
                            Doctor doctor = new Doctor();
                            doctor.setEmail(rs1.getString("email"));
                            doctor.setUsername(rs1.getString("username"));
                            doctor.setFirstName(rs1.getString("first_name"));
                            doctor.setId(rs1.getString("id"));
                            doctor.setPhone(Long.valueOf(rs1.getString("phone")));
                            feedback.setDoctor(doctor);
                        }
                        String subject = "Feedback: Pdf has been generated!! click below link to generate PDF";
                        String body = "http://localhost:8080/feedback/pdf/" + feedback.getDoctor().getEmail();
                        EmailUtils.main(feedback.getClient().getEmail(), subject, body);
                        PreparedStatement stat2 = connection.prepareStatement("update feedback set mail_send = 'yes' WHERE  id = ?");
                        stat2.setLong(1, feedback.getId());
                        stat2.executeUpdate();
                    }


                    System.out.println("PDF agent=============Ended");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}
