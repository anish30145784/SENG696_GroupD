package org.team1.agent;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.springframework.util.StringUtils;
import org.team1.models.*;
import org.team1.utils.EmailUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailAgent extends EnhancedAgent {

    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;

    @Override
    public void setup() {

        System.out.println("Connecting database inside Email Agent...");

        try {
            register("email");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database connected!");
        addBehaviour(new TickerBehaviour(this, 12000) {

            @Override
            protected void onTick() {
                try {
                    String subject = "Appointment Booked";
                    ACLMessage msg = blockingReceive();
                    if (msg != null && msg.getConversationId() == "Delete") {
                        System.out.println("Inside Delete If Block of Email Agent !");
                        Appointment appointment = (Appointment) msg.getContentObject();

                        EmailUtils.main(appointment.getClient().getEmail(), "Appointment Deleted", "Dear " + appointment.getClient().getFirstName() +
                                ",<br> your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                "<br> At " + appointment.getDateTime() + " has been cancelled ! ");

                        PreparedStatement dStat = connection.prepareStatement("update appointment set status='Deleted', del_mail = 1 WHERE  id = ?");
                        dStat.setLong(1, appointment.getId());
                        dStat.executeUpdate();

                    } else if (msg != null) {
                        Appointment appointment = (Appointment) msg.getContentObject();
                        if (appointment != null) {
                            PreparedStatement statement1 = connection.prepareStatement("select * from meeting_date");
                            ResultSet resultSet1 = statement1.executeQuery();
                            MeetingData meetingData = new MeetingData();
                            while (resultSet1.next()) {
                                meetingData.setUrl(resultSet1.getString("url"));
                                meetingData.setPassword(resultSet1.getString("password"));
                                meetingData.setMeetingId(resultSet1.getString("meeting_id"));
                            }

                            System.out.println("Doctor Last Name : " + appointment.getDoctor().getLastName());
                            EmailUtils.main(appointment.getClient().getEmail(), subject, "Dear " + appointment.getClient().getFirstName()
                                    + ",<br> your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                    "<br> At " + appointment.getDateTime() + " <br> url -" + meetingData.getUrl() +
                                    "<br> meeting id - " + meetingData.getMeetingId() +
                                    "<br> password - " + meetingData.getPassword());
                            PreparedStatement stat1 = connection.prepareStatement("update appointment set email = 'yes' WHERE  id = ?");
                            stat1.setLong(1, appointment.getId());
                            stat1.executeUpdate();


                            EmailUtils.main(appointment.getDoctor().getEmail(), subject, "Dear " + appointment.getDoctor().getFirstName()
                                    + ",<br> your appointment is scheduled with user : " + appointment.getClient().getFirstName() + " " + appointment.getClient().getLastName() +
                                    "<br> At " + appointment.getDateTime() + " <br> url -" + meetingData.getUrl() +
                                    "<br> meeting id - " + meetingData.getMeetingId() +
                                    "<br> password - " + meetingData.getPassword());
                            PreparedStatement stat2 = connection.prepareStatement("update appointment set email = 'yes' WHERE  id = ?");
                            stat2.setLong(1, appointment.getId());
                            stat2.executeUpdate();
                        }
                    } else {
                        System.out.println("EMAIL agent=============started");
                        PreparedStatement statement = connection.prepareStatement("select * from appointment where status = 'Scheduled'");
                        statement.execute();
                        ResultSet resultSet = statement.getResultSet();
                        List<Appointment> appointments = new ArrayList<Appointment>();
                        while (resultSet.next()) {
                            Appointment appointment = new Appointment();
                            appointment.setId(Long.valueOf(resultSet.getString("id")));
                            appointment.setCriticality(Criticality.parse(resultSet.getInt("criticality")));
                            appointment.setStatus(resultSet.getString("status"));
                            appointment.setDescription(resultSet.getString("description"));
                            appointment.setNotes(resultSet.getString("notes"));
                            appointment.setAge(resultSet.getString("age"));
                            appointment.setBreed(resultSet.getString("breed"));
                            appointment.setEmail(resultSet.getString("email"));
                            appointment.setDateTime(resultSet.getTimestamp("datetime"));
                            PreparedStatement stat = connection.prepareStatement("SELECT * from client WHERE  id = ?");
                            stat.setLong(1, Long.parseLong(resultSet.getString("client_id")));
                            ResultSet rs = stat.executeQuery();
                            while (rs.next()) {
                                Client client = new Client();
                                client.setEmail(rs.getString("email"));
                                client.setUsername(rs.getString("username"));
                                client.setFirstName(rs.getString("first_name"));
                                client.setLastName(rs.getString("last_name"));
                                client.setId(rs.getString("id"));
                                client.setPhone(Long.valueOf(rs.getString("phone")));
                                appointment.setClient(client);
                            }

                            PreparedStatement stat1 = connection.prepareStatement("SELECT * from doctor WHERE  id = ?");
                            stat1.setLong(1, Long.parseLong(resultSet.getString("doctor_id")));
                            ResultSet rs1 = stat1.executeQuery();
                            while (rs1.next()) {
                                Doctor doctor = new Doctor();
                                doctor.setEmail(rs1.getString("email"));
                                doctor.setUsername(rs1.getString("username"));
                                doctor.setFirstName(rs1.getString("first_name"));
                                doctor.setLastName(rs1.getString("last_name"));
                                doctor.setId(rs1.getString("id"));
                                doctor.setPhone(Long.parseLong(rs1.getString("phone")));
                                appointment.setDoctor(doctor);
                            }
                            appointments.add(appointment);
                            System.out.println(appointment);
                        }
                        for (Appointment appointment : appointments) {
                            if (StringUtils.isEmpty(appointment.getEmail())) {
                                System.out.println("sending mail");

                                PreparedStatement statement1 = connection.prepareStatement("select * from meeting_date");
                                ResultSet resultSet1 = statement1.executeQuery();
                                MeetingData meetingData = new MeetingData();
                                while (resultSet1.next()) {
                                    meetingData.setUrl(resultSet1.getString("url"));
                                    meetingData.setPassword(resultSet1.getString("password"));
                                    meetingData.setMeetingId(resultSet1.getString("meeting_id"));
                                }

                                System.out.println("Doctor Last Name : " + appointment.getDoctor().getLastName());
                                EmailUtils.main(appointment.getClient().getEmail(), subject, "Dear " + appointment.getClient().getFirstName()
                                        + ",<br> your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                        "<br> At " + appointment.getDateTime() + " <br> url -" + meetingData.getUrl() +
                                        "<br> meeting id - " + meetingData.getMeetingId() +
                                        "<br> password - " + meetingData.getPassword());
                                PreparedStatement stat1 = connection.prepareStatement("update appointment set email = 'yes' WHERE  id = ?");
                                stat1.setLong(1, appointment.getId());
                                stat1.executeUpdate();
                            }
                        }
                        System.out.println("EMAIL agent=============Ended");
                    }
                } catch (SQLException | UnreadableException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
