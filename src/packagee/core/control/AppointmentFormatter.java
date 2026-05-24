/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */

import java.util.HashMap;
import java.util.Map;
import packagee.Appointment;
import packagee.Appointment;


public class AppointmentFormatter {
    
    public Map<String, Object> toMap(Appointment appointment) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", appointment.getId());
        data.put("datetime", appointment.getDatetime().toString());
        data.put("doctorName", appointment.getDoctor().getFirstname() + " " + 
                               appointment.getDoctor().getLastname());
        data.put("patientName", appointment.getPatient().getFirstname() + " " + 
                                appointment.getPatient().getLastname());
        data.put("specialty", appointment.getSpecialty().name());
        data.put("type", appointment.isType() ? "In-person" : "Remote");
        data.put("status", appointment.getStatus().name());
        data.put("reason", appointment.getReason());
        return data;
    }
    
}