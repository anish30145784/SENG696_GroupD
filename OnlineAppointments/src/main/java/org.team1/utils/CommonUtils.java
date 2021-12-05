package org.team1.utils;

import org.springframework.stereotype.Component;
import org.team1.models.Appointment;
import org.team1.models.Client;
import org.team1.models.Criticality;
import org.team1.models.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class CommonUtils {

    public static String replaceText(String text, Map<String, String> textToAndWithReplace) throws NullPointerException {
        text = (text != null ? text : "");
        Iterator<String> itr = textToAndWithReplace.keySet().iterator();
        while (itr.hasNext()) {
            String regex = itr.next();
            if ((textToAndWithReplace.get(regex) != null) && (textToAndWithReplace.get(regex).contains("$"))) {
                String changeValue = textToAndWithReplace.get(regex);
                regex = regex.replaceAll("\\\\", "");
                text = text.replace(regex, changeValue);
            } else {
                text = text.replaceAll(regex, textToAndWithReplace.get(regex));
            }
        }
        return text;
    }


    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static List<Appointment> getAppointments(ResultSet resultSet, Connection connection) throws SQLException {
        List<Appointment> appointments = new ArrayList<Appointment>();
        while (resultSet.next()) {
            Appointment appointment = new Appointment();
            appointment.setId(Long.valueOf(resultSet.getString("id")));
            appointment.setCriticality(Criticality.parse(resultSet.getInt("criticality")));
            appointment.setStatus(resultSet.getString("status"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setNotes(resultSet.getString("notes"));
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

            PreparedStatement stat1 = connection.prepareStatement("SELECT * from doctor where  id = ?");
            stat1.setLong(1, Long.parseLong(resultSet.getString("doctor_id")));
            ResultSet rs1 = stat1.executeQuery();
            while (rs1.next()) {
                Doctor doctor = new Doctor();
                doctor.setEmail(rs1.getString("email"));
                doctor.setUsername(rs1.getString("username"));
                doctor.setFirstName(rs1.getString("first_name"));
                doctor.setId(rs1.getString("id"));
                doctor.setPhone(Long.valueOf(rs1.getString("phone")));
                appointment.setDoctor(doctor);
            }
            appointments.add(appointment);

            System.out.println(appointment);
        }
        return appointments;
    }
}
