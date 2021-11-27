package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.springframework.util.StringUtils;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.Criticality;
import org.team1.models.Doctor;
import org.team1.utils.CommonUtils;
import org.team1.utils.Constrants;
import org.team1.utils.EmailUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletionAgent extends Agent {


    String url = "jdbc:mysql://localhost:3306/mydatabase?useSSL=false";
    String username = "root";
    String password = "test1234";
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
        addBehaviour(new TickerBehaviour(this, 17000) {

            @Override
            protected void onTick() {
                try {
                    System.out.println("complition agent=============started");
                    PreparedStatement statement = connection.prepareStatement("select * from appointment where status = 'Completed' and feedback IS NULL");
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
                        appointment.setSms(resultSet.getString("sms"));
                        appointment.setFeedback(resultSet.getString("feedback"));
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
                        if (StringUtils.isEmpty(appointment.getFeedback())) {
                            System.out.println("sending feedback mail");

                            String subject = "Feedback: patient name " + appointment.getClient().getLastName();
                            Map<String, String> map = new HashMap<>();
                            map.put(Constrants.DOCTOR_NAME, appointment.getDoctor().getFirstName());
                            map.put(Constrants.DOCTOR_EMAIl, appointment.getDoctor().getEmail());
                            map.put(Constrants.PATIENT_NAME, appointment.getClient().getFirstName());
                            map.put(Constrants.PATIENT_EMAIL, appointment.getClient().getEmail());

                            String body = CommonUtils.replaceText(Constrants.EMAIl_TEMPLATE_FEEDBACK, map);
                            EmailUtils.main(appointment.getDoctor().getEmail(), subject, body);

                            PreparedStatement stat1 = connection.prepareStatement("update appointment set feedback = 'yes' WHERE  id = ?");
                            stat1.setLong(1, appointment.getId());
                            stat1.executeUpdate();
                        }


                    }
                    System.out.println("complition agent=============Ended");
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

