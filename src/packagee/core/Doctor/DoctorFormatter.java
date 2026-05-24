package packagee.core.Doctor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author juand
 */

import java.util.HashMap;
import java.util.Map;
import packagee.core.Doctor.Doctor;

public class DoctorFormatter {
    
    public Map<String, Object> toMap(Doctor doctor) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", doctor.getId());
        data.put("username", doctor.getUsername());
        data.put("firstname", doctor.getFirstname());
        data.put("lastname", doctor.getLastname());
        data.put("specialty", doctor.getSpecialty().name());
        data.put("licenceNumber", doctor.getLicenceNumber());
        data.put("assignedOffice", doctor.getAssignedOffice());
        return data;
    }
    
}
