/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */

import packagee.core.hospital.DataBase;
import packagee.core.person.Doctor;
import packagee.Specialty;


public class DoctorService {
    
    private final DataBase store;
    
    public DoctorService(DataBase store) {
        this.store = store;
    }
    

    public Doctor createDoctor(long id, String username, String firstname, 
                              String lastname, String password, 
                              Specialty specialty, String licenceNumber, 
                              String assignedOffice) {
        return new Doctor(id, username, firstname, lastname, password, 
                         specialty, licenceNumber, assignedOffice);
    }
    

    public void saveDoctor(Doctor doctor) {
        store.addUser(doctor);
    }
    
    public void updateDoctor(Doctor doctor, String firstname, String lastname, 
                            String username, String password, Specialty specialty, 
                            String licenceNumber, String assignedOffice) {
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setUsername(username);
        doctor.setPassword(password);
        doctor.setSpecialty(specialty);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);
    }
    

    public Specialty parseSpecialty(String specialtyStr) {
        try {
            return Specialty.valueOf(
                specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
}
