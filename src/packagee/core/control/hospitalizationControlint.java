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


public interface hospitalizationControlint {


    response requestHospitalization(long patientId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations);

 
    response hospitalizeFromAppointment(String appointmentId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations);


    response approveHospitalization(String hospId, long doctorId);


    response cancelHospitalization(String hospId, long doctorId);


    response getAllHospitalizations();
}
