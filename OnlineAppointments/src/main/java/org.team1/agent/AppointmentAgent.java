package org.team1.agent;


import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.Criticality;
import org.team1.models.Doctor;

import java.sql.*;
import java.util.Date;
import java.util.*;

@Service
public class AppointmentAgent extends EnhancedAgent {

    public Set<AID> emailA = new HashSet<>();
    public Set<AID> smsA = new HashSet<>();
    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "test1234";
    Connection connection = null;
    @Autowired
    private JdbcTemplate appointmentRepository;

    @Override
    public void setup() {

        System.out.println("Connecting database inside Appointment Agent...");
        register("appointment");

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database connected!");


        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                try {
                    ACLMessage msg = blockingReceive();
                    if (msg != null) {
                        System.out.println("Appointment agent=============started");
                        PreparedStatement statement = connection.prepareStatement("select * from appointment where status = 'NOT_SCHEDULED'");
                        statement.execute();
                        ResultSet resultSet = statement.getResultSet();
                        List<Appointment> appointments = new ArrayList<Appointment>();
                        while (resultSet.next()) {
                            Appointment appointment = new Appointment();
                            appointment.setId(Long.valueOf(resultSet.getString("id")));
                            appointment.setBreed(resultSet.getString("breed"));
                            appointment.setAge(resultSet.getString("age"));
                            appointment.setCriticality(Criticality.parse(resultSet.getInt("criticality")));
                            appointment.setStatus(resultSet.getString("status"));
                            appointment.setDescription(resultSet.getString("description"));
                            appointment.setNotes(resultSet.getString("notes"));
                            appointment.setDateTime(resultSet.getTimestamp("datetime"));
                            System.out.println("Date Time : " + resultSet.getTimestamp("datetime"));
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

                            PreparedStatement stat1 = connection.prepareStatement("SELECT * from doctor where  id = ?");
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
                        System.out.println("Not Scheduled Appointment Size : " + appointments.size());

                        PreparedStatement statement1 = connection.prepareStatement("select datetime from appointment where status = 'Scheduled'");
                        statement1.execute();
                        ResultSet resultSet1 = statement1.getResultSet();
                        List<Date> schAppTime = new ArrayList<Date>();
                        while (resultSet1.next()) {
                            System.out.println("Scheduled DateTime : " + resultSet1.getTimestamp("datetime"));
                            schAppTime.add(resultSet1.getTimestamp("datetime"));
                        }

                        int i, j;
                        for (i = 0; i < appointments.size(); i++) {
                            System.out.println("i loop dateTime" + appointments.get(i).getDateTime());
                            for (j = 0; j < schAppTime.size(); j++) {
                                System.out.println("j loop dateTime" + schAppTime.get(i));
                                if (appointments.get(i).getDateTime().toString().equals(schAppTime.get(j).toString())) {
                                    System.out.println("Match Found !");
                                    Date dt = provideNextAvTime(appointments.get(i).getCriticality(), schAppTime, appointments.get(i).getDateTime());
                                    System.out.println("New Date Received for appointment : " + dt);
                                    appointments.get(i).setDateTime(dt);
                                    break;
                                } else {
                                    System.out.println("No match !");
                                    continue;
                                }

                            }
                        }


                        for (Appointment appointment : appointments) {
                            System.out.println("Inside Appointment Re-scheduling loop for Appointment Agent!");

                            PreparedStatement stat2 = connection.prepareStatement("Update appointment set datetime = ? where id = ?");
                            stat2.setTimestamp(1, new Timestamp(appointment.getDateTime().getTime()));
                            stat2.setLong(2, appointment.getId());
                            stat2.executeUpdate();

                            if (appointment != null) {
                                PreparedStatement stat3 = connection.prepareStatement("Update appointment set status = 'Scheduled' where id = ?");
                                stat3.setLong(1, appointment.getId());
                                stat3.executeUpdate();

                            }

                            System.out.println("Calling Email Agent !");

                            emailA = searchForService("email");
                            for (AID agentEmail : emailA) {
                                ACLMessage aclUpdEmailMsg = new ACLMessage(ACLMessage.REQUEST);
                                //aclUpdEmailMsg.addReceiver(new AID("EmailAgent", AID.ISLOCALNAME));
                                aclUpdEmailMsg.setContentObject(appointment);
                                aclUpdEmailMsg.setConversationId("email");
                                aclUpdEmailMsg.addReceiver(agentEmail);
                                send(aclUpdEmailMsg);
                            }
                            //ACLMessage aclEmailMsg = new ACLMessage(ACLMessage.REQUEST);
                            //aclEmailMsg.addReceiver(new AID("EmailAgent", AID.ISLOCALNAME));
                            //aclEmailMsg.setContentObject(appointment);
                            //send(aclEmailMsg);

                            System.out.println("Calling SMS Agent !");
                            smsA = searchForService("sms");
                            for (AID agentSMS : smsA) {
                                ACLMessage aclSMSmsg = new ACLMessage(ACLMessage.REQUEST);
                                //aclSMSmsg.addReceiver(new AID("SmsAgent", AID.ISLOCALNAME));
                                aclSMSmsg.setContentObject(appointment);
                                aclSMSmsg.setConversationId("sms");
                                aclSMSmsg.addReceiver(agentSMS);
                                send(aclSMSmsg);
                            }


                        }
                        System.out.println("Appointment agent=============Ended");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
    }


    public Date provideNextAvTime(Criticality criticality, List schAppTimes, Date dateTime) {
        String cri = criticality.toString();
        System.out.println("StartDate sent to Function : " + dateTime + " | Timestamp : " + dateTime.getTime());
        System.out.println("Criticality sent to Function : " + cri);
        int hoursTillnxtDay9am = (int) (Math.floor(dateTime.getTime() / (1000 * 60 * 60)) % 24) <= 1 ? 14 + ((int) Math.floor((dateTime.getTime() / (1000 * 60 * 60)) % 24)) : 40 - ((int) Math.floor((dateTime.getTime() / (1000 * 60 * 60)) % 24));
        System.out.println("hoursTillnxtDay9am : " + hoursTillnxtDay9am);
        Date startDate = cri.equals("URGENT") ? new Date(dateTime.getTime() + (1 * 60 * 60 * 1000)) : new Date(dateTime.getTime() + (hoursTillnxtDay9am * 3600000));
        System.out.println("New Start Date based on Criticality : " + startDate);
        while (true) {
            if ((Math.floor((startDate.getTime() / (1000 * 60 * 60)) % 24)) == 2) {
                startDate = new Date(startDate.getTime() + (14 * 60 * 60 * 1000));
                System.out.println("Next day start time : " + startDate);
            }

            if (schAppTimes.contains(startDate)) {
                startDate = new Date(startDate.getTime() + (1 * 60 * 60 * 1000));
                System.out.println("New Start date : " + startDate);
            } else {
                break;
            }


        }


        return startDate;
    }


}



