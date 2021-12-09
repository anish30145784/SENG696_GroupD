package org.team1.agent;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.springframework.util.StringUtils;
import org.team1.models.*;
import org.team1.utils.SmsUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmsAgent extends EnhancedAgent {


    public Set<AID> videoA = new HashSet<>();
    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;
    boolean status = false;

    @Override
    public void setup() {
        register("sms");
        System.out.println("Connecting database inside SMS Agent...");
        MeetingData meetingData = new MeetingData();

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
                    ACLMessage smsMsg = receive();
                    if (smsMsg != null) {
                        Appointment appmt = (Appointment) smsMsg.getContentObject();
                        System.out.println("Received Appointment object from Appointment agent : " + appmt);

                        try {
                            System.out.println("Calling Video Agent from Update Block of SMS Agent !");
                            videoA = searchForService("video");
                            for (AID agentVideo : videoA) {
                                ACLMessage aclVideoMsg = new ACLMessage(ACLMessage.REQUEST);
                                //aclUpdEmailMsg.addReceiver(new AID("EmailAgent", AID.ISLOCALNAME));
                                aclVideoMsg.setContentObject(appmt);
                                aclVideoMsg.setConversationId("sms");
                                aclVideoMsg.addReceiver(agentVideo);
                                send(aclVideoMsg);

                                System.out.println("Waiting For VideoLink Agent message from Update Block of SMS Agent !");
                                ACLMessage videoMsg = blockingReceive();
                                MeetingData md = (MeetingData) videoMsg.getContentObject();
                                meetingData.setUrl(md.getUrl());
                                meetingData.setPassword(md.getPassword());
                                meetingData.setMeetingId(md.getMeetingId());
                            }
                        } catch (Exception e) {
                            status = true;
                            e.printStackTrace();
                        }

                        if (status == true) {
                            PreparedStatement statement1 = connection.prepareStatement("select * from meeting_date");
                            ResultSet resultSet1 = statement1.executeQuery();
                            while (resultSet1.next()) {
                                meetingData.setUrl(resultSet1.getString("url"));
                                meetingData.setPassword(resultSet1.getString("password"));
                                meetingData.setMeetingId(resultSet1.getString("meeting_id"));
                            }
                        }

                        if (StringUtils.isEmpty(appmt.getSms())) {
                            SmsUtil.main("Dear " + appmt.getClient().getFirstName()
                                    + ",\n your appointment is scheduled with doctor : " + appmt.getDoctor().getFirstName() + " " + appmt.getDoctor().getLastName() +
                                    "\n At " + appmt.getDateTime() + " \n url -" + meetingData.getUrl() +
                                    "\n meeting id - " + meetingData.getMeetingId() +
                                    "\n password - " + meetingData.getPassword(), appmt.getClient().getPhone());
                            PreparedStatement stat1 = connection.prepareStatement("update appointment set sms = 'yes' WHERE  id = ?");
                            stat1.setLong(1, appmt.getId());
                            stat1.executeUpdate();
                        }

                    } else {
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
                                PreparedStatement stmt1 = connection.prepareStatement("select * from meeting_date");
                                ResultSet rsSet1 = stmt1.executeQuery();
                                MeetingData meetingDt = new MeetingData();
                                while (rsSet1.next()) {
                                    meetingDt.setUrl(rsSet1.getString("url"));
                                    meetingDt.setPassword(rsSet1.getString("password"));
                                    meetingDt.setMeetingId(rsSet1.getString("meeting_id"));
                                }

                                SmsUtil.main("Dear " + appointment.getClient().getFirstName()
                                        + ",\n your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                        "\n At " + appointment.getDateTime() + " \n url -" + meetingDt.getUrl() +
                                        "\n meeting id - " + meetingDt.getMeetingId() +
                                        "\n password - " + meetingDt.getPassword(), appointment.getClient().getPhone());
                                PreparedStatement stat1 = connection.prepareStatement("update appointment set sms = 'yes' WHERE  id = ?");
                                stat1.setLong(1, appointment.getId());
                                stat1.executeUpdate();
                            }


                        }
                    }
                    System.out.println("SMS agent=============ENDED");
                } catch (UnreadableException | SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

