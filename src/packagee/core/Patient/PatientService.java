/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Patient;

/**
 *
 * @author juand
 */

import java.time.LocalDate;
import packagee.core.hospital.DataBase;


public class PatientService {
    
    private final DataBase store;
    
    public PatientService(DataBase store) {
        this.store = store;
    }
    
    
    public Patient createPatient(long id, String username, String firstname, 
                                String lastname, String password, String email, 
                                LocalDate birthdate, boolean gender, 
                                long phone, String address) {
        return new Patient(id, username, firstname, lastname, password, 
                          email, birthdate, gender, phone, address);
    }
    
   
    public void savePatient(Patient patient) {
        store.addUser(patient);
    }
    
    public void updatePatient(Patient patient, String firstname, String lastname, 
                             String username, String password, String email, 
                             LocalDate birthdate, boolean gender, 
                             long phone, String address) {
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setUsername(username);
        patient.setPassword(password);
        patient.setEmail(email);
        patient.setBirthdate(birthdate);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setAddress(address);
    }
    
}
