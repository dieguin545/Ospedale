/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.hospital;

/**
 *
 * @author juand
 */

import java.time.LocalDate;
import packagee.RoomType;
import packagee.core.hospital.DataBase;
import packagee.core.hospital.Hospitalization;
import packagee.core.hospital.HospitalizationStatus;
import packagee.core.Doctor.Doctor;
import packagee.core.Patient.Patient;


public class HospitalizationService {
    
    private final DataBase store;
    
    public HospitalizationService(DataBase store) {
        this.store = store;
    }
    

    public Hospitalization createHospitalization(Patient patient, Doctor doctor,
                                                LocalDate date, String reason,
                                                RoomType roomType, String observations) {
        String hospId = store.genHospitalizationID(patient.getId());
        return new Hospitalization(hospId, patient, doctor, date, reason, roomType, observations);
    }
    
  
    public Hospitalization createHospitalizationFromAppointment(Patient patient, 
                                                               Doctor doctor,
                                                               LocalDate date, 
                                                               String reason,
                                                               RoomType roomType, 
                                                               String observations) {
        String hospId = store.genHospitalizationID(patient.getId());
        return new Hospitalization(hospId, patient, doctor, date, reason, roomType, 
                                  observations, HospitalizationStatus.ONGOING);
    }
    
  
    public void saveHospitalization(Hospitalization hospitalization) {
        store.addHospitalization(hospitalization);
    }
    
   
    public RoomType parseRoomType(String roomTypeStr) {
        try {
            return RoomType.valueOf(roomTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
}