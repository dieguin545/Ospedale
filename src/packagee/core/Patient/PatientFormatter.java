/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Patient;

/**
 *
 * @author juand
 */

import java.util.HashMap;
import java.util.Map;


public class PatientFormatter {
    
    public Map<String, Object> toMap(Patient patient) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", patient.getId());
        data.put("username", patient.getUsername());
        data.put("firstname", patient.getFirstname());
        data.put("lastname", patient.getLastname());
        return data;
    }
    
}
