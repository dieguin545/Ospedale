/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.hospital;

/**
 *
 * @author juand
 */

import java.util.HashMap;
import java.util.Map;
import packagee.core.hospital.Hospitalization;


public class HospitalizationFormatter {
    
    public Map<String, Object> toMap(Hospitalization hospitalization) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", hospitalization.getId());
        data.put("patientName", hospitalization.getPatient().getFirstname() + " " + 
                                hospitalization.getPatient().getLastname());
        data.put("doctorName", hospitalization.getDoctor().getFirstname() + " " + 
                               hospitalization.getDoctor().getLastname());
        data.put("date", hospitalization.getDate().toString());
        data.put("reason", hospitalization.getReason());
        data.put("roomType", hospitalization.getRoomType().name());
        data.put("status", hospitalization.getStatus().name());
        data.put("observations", hospitalization.getObservations());
        return data;
    }
    
}
