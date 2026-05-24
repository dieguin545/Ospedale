/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Doctor;


/**
 *
 * @author juand
 */
import packagee.response;


public interface doctorControlint {


    response registrarDoctor(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice);


    response updateDoctor(long doctorId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice);


    response getAllDoctors();


    response getDoctorInfo(long doctorId);
}
