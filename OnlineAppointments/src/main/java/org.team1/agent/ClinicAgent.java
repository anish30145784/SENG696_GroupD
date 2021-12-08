package org.team1.agent;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.Criticality;
import org.team1.models.Doctor;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClinicAgent extends EnhancedAgent {
    public Set<AID> emailA = new HashSet<>();
    public Set<AID> appA = new HashSet<>();
    String url = "jdbc:mysql://localhost:3306/mydatabase_new?useSSL=false";
    String username = "root";
    String password = "sampreet07";
    Connection connection = null;
    String task = null;


    @Autowired
    private JdbcTemplate appointmentRepository;

    @Override
    public void setup() {
        try {
            register("clinic");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connecting database inside Clinic Agent...");


        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                try {
                    task = "Deleted";
                    System.out.println("Switch-case started ");
                    switch (task) {
                        case "Deleted":
                            System.out.println("Inside Delete Case of Clinic Agent !");
                            PreparedStatement delStmt = connection.prepareStatement("select * from appointment where deleted=1 and del_mail=0");
                            delStmt.execute();
                            ResultSet delResultSet = delStmt.getResultSet();
                            System.out.println("After Delete Result Set is created ! ");
                            List<Appointment> delAppointments = new ArrayList<Appointment>();
                            while (delResultSet.next()) {
                                System.out.println("Inside While loop for Deleted Appointment !");
                                Appointment dAppointment = new Appointment();
                                dAppointment.setId(Long.valueOf(delResultSet.getString("id")));
                                dAppointment.setCriticality(Criticality.parse(delResultSet.getInt("criticality")));
                                dAppointment.setStatus(delResultSet.getString("status"));
                                dAppointment.setDescription(delResultSet.getString("description"));
                                dAppointment.setNotes(delResultSet.getString("notes"));
                                dAppointment.setEmail(delResultSet.getString("email"));
                                dAppointment.setDateTime(delResultSet.getTimestamp("datetime"));
                                //System.out.println("Appointment DateTime in Clinic Agent Delete Block : " + delResultSet.getDate("datetime"));

                                PreparedStatement delStatCl = connection.prepareStatement("SELECT * from client WHERE  id = ?");
                                delStatCl.setLong(1, Long.parseLong(delResultSet.getString("client_id")));
                                ResultSet rs = delStatCl.executeQuery();
                                while (rs.next()) {
                                    Client client = new Client();
                                    client.setEmail(rs.getString("email"));
                                    client.setUsername(rs.getString("username"));
                                    client.setFirstName(rs.getString("first_name"));
                                    client.setId(rs.getString("id"));
                                    client.setPhone(Long.valueOf(rs.getString("phone")));
                                    dAppointment.setClient(client);
                                }

                                PreparedStatement delDocstat = connection.prepareStatement("SELECT * from doctor WHERE  id = ?");
                                delDocstat.setLong(1, Long.parseLong(delResultSet.getString("doctor_id")));
                                ResultSet rs1 = delDocstat.executeQuery();
                                while (rs1.next()) {
                                    Doctor doctor = new Doctor();
                                    doctor.setEmail(rs1.getString("email"));
                                    doctor.setUsername(rs1.getString("username"));
                                    doctor.setFirstName(rs1.getString("first_name"));
                                    doctor.setLastName(rs1.getString("last_name"));
                                    doctor.setId(rs1.getString("id"));
                                    doctor.setPhone(Long.parseLong(rs1.getString("phone")));
                                    dAppointment.setDoctor(doctor);
                                }
                                delAppointments.add(dAppointment);
                                System.out.println("To be deleted appointment : " + dAppointment);
                            }

                            for (Appointment appointment : delAppointments) {
                                //System.out.println("sending Deletion mail ... ");
                                System.out.println("Calling Email Agent from Delete Block of Clinic Agent !");

                                emailA = searchForService("email");
                                for (AID agentEmail : emailA) {

                                    ACLMessage aclUpdEmailMsg = new ACLMessage(ACLMessage.REQUEST);
                                    //aclUpdEmailMsg.addReceiver(new AID("EmailAgent", AID.ISLOCALNAME));
                                    aclUpdEmailMsg.setContentObject(appointment);
                                    aclUpdEmailMsg.setConversationId("Delete");
                                    aclUpdEmailMsg.addReceiver(agentEmail);
                                    send(aclUpdEmailMsg);
                                }


                            }
                            task = "updated";
//                            for (Appointment appointment : delAppointments) {
//                                System.out.println("sending Deletion mail ... ");
//
//                                EmailUtils.main(appointment.getDoctor().getEmail(), "Appointment Deleted", "Dear " + appointment.getClient().getFirstName() +
//                                        ",<br> your appointment is scheduled with doctor : " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
//                                        "<br> At " + appointment.getDateTime() + " has been cancelled ! ");
//
//                                PreparedStatement dStat = connection.prepareStatement("update appointment set status='Deleted', del_mail = 1 WHERE  id = ?");
//                                dStat.setLong(1, appointment.getId());
//                                dStat.executeUpdate();
//                                // connection.prepareStatement("delete from appointment where deleted = 1").execute();
//
//                            }


                        case "Updated":
                            System.out.println("Inside Update Case of Clinic Agent !");
                            PreparedStatement statement = connection.prepareStatement("select * from appointment where updated =1 and deleted=0 and updated_mail = 0");
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
                                System.out.println("Calling Email Agent from Update Block of Clinic Agent !");
                                emailA = searchForService("email");
                                for (AID agentEmail : emailA) {

                                    ACLMessage aclUpdEmailMsg = new ACLMessage(ACLMessage.REQUEST);
                                    aclUpdEmailMsg.setContentObject(appointment);
                                    aclUpdEmailMsg.setConversationId("Update");
                                    aclUpdEmailMsg.addReceiver(agentEmail);
                                    send(aclUpdEmailMsg);
                                }


                            }


                            task = "Appointment";
                        case "Appointment":
                            System.out.println("Calling Appointment Case of Clinic Agent !");

                            appA = searchForService("appointment");
                            for (AID agentAppmt : appA) {
                                ACLMessage appAclMsg = new ACLMessage(ACLMessage.REQUEST);
                                appAclMsg.addReceiver(agentAppmt);
                                appAclMsg.setContentObject("Begin Processing Appointment");
                                send(appAclMsg);
                            }

                            //new AppointmentJadeAgent().setup();


                            System.out.println("Switch-case ended ");
                            break;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}