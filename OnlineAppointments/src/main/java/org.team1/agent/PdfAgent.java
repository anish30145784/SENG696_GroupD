package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.springframework.cglib.core.EmitUtils;
import org.team1.models.Feedback;
import org.team1.utils.EmailUtils;

import java.sql.*;

public class PdfAgent extends Agent {

    String url = "jdbc:mysql://localhost:3306/appointment?useSSL=false";
    String username = "root";
    String password = "anish@123";
    Connection connection = null;

    @Override
    public void setup() {

        System.out.println("Connecting database...");

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database connected!");
        addBehaviour(new TickerBehaviour(this, 19000) {

            @Override
            protected void onTick() {
                try {
                    System.out.println("PDF agent=============started");
                    PreparedStatement statement = connection.prepareStatement("select * from feedback where mail_send is null");
                    statement.execute();
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        Feedback feedback = new Feedback();
                        feedback.setId(resultSet.getLong("id"));
                        feedback.setEmail(resultSet.getString("email"));
                        feedback.setFirstName(resultSet.getString("first_name"));
                        feedback.setLastName(resultSet.getString("last_name"));
                        feedback.setFeedback(resultSet.getString("feedback"));

                        String subject = "Feedback: Pdf has been generated!! click below link to generate PDF";
                        String body = "http://localhost:8080/feedback/pdf/"+ feedback.getEmail() ;
                        EmailUtils.main(feedback.getEmail(),subject,body);
                        PreparedStatement stat1 = connection.prepareStatement("update feedback set mail_send = 'yes' WHERE  id = ?");
                        stat1.setLong(1, feedback.getId());
                        stat1.executeUpdate();
                    }
                    System.out.println("PDF agent=============Ended");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
