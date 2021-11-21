package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.Criticality;
import org.team1.models.Doctor;
import org.team1.utils.EmailUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailAgent extends Agent {

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
        addBehaviour(new TickerBehaviour(this, 12000) {

            @Override
            protected void onTick() {
                try {

                    System.out.println("EMAIL agent=============started" );
                    PreparedStatement statement = connection.prepareStatement("select * from appointment where status = 'Scheduled'");
                    statement.execute();
                    ResultSet resultSet = statement.getResultSet();
                    List<Appointment> appointments  =  new ArrayList<Appointment>();
                    while (resultSet.next()) {
                        Appointment appointment = new Appointment();
                        appointment.setId(Long.valueOf(resultSet.getString("id")));
                        appointment.setCriticality(Criticality.parse(resultSet.getInt("criticality")));
                        appointment.setStatus(resultSet.getString("status"));
                        appointment.setDescription(resultSet.getString("description"));
                        appointment.setNotes(resultSet.getString("notes"));
                        appointment.setEmail(resultSet.getString("email"));
                        appointment.setDateTime(resultSet.getDate("datetime"));
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

                        PreparedStatement stat1 = connection.prepareStatement("SELECT * from Doctor WHERE  id = ?");
                        stat1.setLong(1, Long.parseLong(resultSet.getString("doctor_id")));
                        ResultSet rs1 = stat.executeQuery();
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
                        if(StringUtils.isEmpty(appointment.getEmail())) {
                            System.out.println("sending mail");
                            String subject = "Appointment Booked";
                            EmailUtils.main(appointment.getDoctor().getEmail(), subject,"Dear User,"
                                    + "\n\n your appointment is scheduled with doctor : " +appointment.getDoctor().getFirstName()+
                                    "\n\n At " + appointment.getDateTime());
                            PreparedStatement stat1 = connection.prepareStatement("update appointment set email = 'yes' WHERE  id = ?");
                            stat1.setLong(1, appointment.getId());
                            stat1.executeUpdate();
                        }
                    }
                    System.out.println("EMAIL agent=============Ended" );
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
