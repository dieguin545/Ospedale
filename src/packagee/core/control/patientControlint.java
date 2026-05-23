/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;


/**
 *
 * @author juand
 */
import packagee.response;


public interface patientControlint {


    response registrarPaciente(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address);


    response PatientUpdate(long patientId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address);


    response getPatient(long patientId);


    response getPatients();
}
