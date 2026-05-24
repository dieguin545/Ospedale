/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import packagee.Appointment;
import packagee.Specialty;
import packagee.core.hospital.DataBase;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;

public class AppointmentService {
    
    private final DataBase store;
    
    public AppointmentService(DataBase store) {
        this.store = store;
    }
    

    public Appointment createAppointment(Patient patient, Doctor doctor, 
                                         LocalDateTime datetime, String reason, 
                                         boolean isInPerson) {
        String appointmentId = store.generateAppID(patient.getId());
        return new Appointment(appointmentId, patient, doctor, 
                              doctor.getSpecialty(), datetime, reason, isInPerson);
    }
    

    public void saveAppointment(Appointment appointment, Patient patient, Doctor doctor) {
        store.addAppointment(appointment);
        patient.addAppointment(appointment);
        doctor.addAppointment(appointment);
    }
    

    public Doctor findAvailableDoctorById(long doctorId, LocalDateTime datetime) {
        Doctor doctor = (Doctor) store.findID(doctorId);
        
        if (doctor != null && store.doctorAvailable(doctor, datetime)) {
            return doctor;
        }
        return null;
    }
    
    public Doctor findAvailableDoctorBySpecialty(Specialty specialty, LocalDateTime datetime) {
        java.util.ArrayList<Doctor> available = store.getdoctorsSpeciality(specialty, datetime);
        if (!available.isEmpty()) {
            return available.get(0);
        }
        return null;
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
