package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.springframework.util.StringUtils;
import org.team1.models.*;
import org.team1.utils.SmsUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SmsAgent extends Agent {


    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;

    @Override
    public void setup() {

        System.out.println("Connecting database inside SMS Agent...");

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database connected!");
        addBehaviour(new TickerBehaviour(this, 15000) {

            @Override
            protected void onTick() {
                try {
                    System.out.println("SMS agent=============started");
                    PreparedStatement statement = connection.prepareStatement("select * from appointment where status = 'Scheduled'");
                    statement.execute();
                    ResultSet resultSet = statement.getResultSet();
                    List<Appointment> appointments = new ArrayList<Appointment>();
                    while (resultSet.next()) {
                        Appointment appointment = new Appointment();
                        appointment.setId(Long.valueOf(resultSet.getString("id")));
                        //System.out.println("Appointment DateTime in SMS : " + resultSet.getDate("dateTime"));
                        appointment.setDateTime(resultSet.getTimestamp("dateTime"));
                        appointment.setDescription(resultSet.getString("description"));
                        appointment.setNotes(resultSet.getString("notes"));
                        appointment.setCriticality(Criticality.parse(resultSet.getInt("criticality")));
                        appointment.setStatus(resultSet.getString("status"));
                        appointment.setDescription(resultSet.getString("description"));
                        appointment.setNotes(resultSet.getString("notes"));
                        appointment.setEmail(resultSet.getString("email"));
                        appointment.setSms(resultSet.getString("sms"));
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
                            doctor.setPhone(Long.valueOf(rs1.getString("phone")));
                            appointment.setDoctor(doctor);
                        }
                        appointments.add(appointment);
                        System.out.println(appointment);
                    }
                    for (Appointment appointment : appointments) {
                        if (StringUtils.isEmpty(appointment.getSms())) {
                            System.out.println("sending sms");
                            PreparedStatement statement1 = connection.prepareStatement("select * from meeting_date");
                            ResultSet resultSet1 = statement1.executeQuery();
                            MeetingData meetingData = new MeetingData();
                            while (resultSet1.next()) {
                                meetingData.setUrl(resultSet1.getString("url"));
                                meetingData.setPassword(resultSet1.getString("password"));
                                meetingData.setMeetingId(resultSet1.getString("meeting_id"));
                            }
                            SmsUtil.main("Dear " + appointment.getClient().getFirstName()
                                    + ",\n your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                    "\n At " + appointment.getDateTime() + " \n url -" + meetingData.getUrl() +
                                    "\n meeting id - " + meetingData.getMeetingId() +
                                    "\n password - " + meetingData.getPassword(), appointment.getClient().getPhone());
                            PreparedStatement stat1 = connection.prepareStatement("update appointment set sms = 'yes' WHERE  id = ?");
                            stat1.setLong(1, appointment.getId());
                            stat1.executeUpdate();
                        }


                    }
                    System.out.println("SMS agent=============ENDED");
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

