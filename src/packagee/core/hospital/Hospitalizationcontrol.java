/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.hospital;

/**
 *
 * @author juand
 */
import packagee.core.hospital.HospitalizationFormatter;
import packagee.core.hospital.HospitalizationService;
import packagee.core.person.User;
import packagee.core.Doctor.Doctor;
import packagee.core.Patient.Patient;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.core.hospital.DataBase;
import packagee.core.hospital.Hospitalization;
import packagee.core.hospital.HospitalizationStatus;
import packagee.response;
import packagee.RoomType;
import packagee.core.control.validacionesformato;

public class Hospitalizationcontrol implements hospitalizationControlint {

    private final DataBase store;
    private final HospitalizationFormatter formatter;
    private final HospitalizationService service;

    public Hospitalizationcontrol(DataBase store) {
        this.store = store;
        this.formatter = new HospitalizationFormatter();
        this.service = new HospitalizationService(store);
    }

    @Override
    public response requestHospitalization(long patientId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations) {

        if (!validacionesformato.isValidDate(dateStr)) {
            return new response(response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        Patient patient = getPatientOrNull(patientId);
        if (patient == null) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }

        Doctor doctor = getDoctorOrNull(doctorId);
        if (doctor == null) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        LocalDate date = LocalDate.parse(dateStr);
        RoomType roomType = service.parseRoomType(roomTypeStr);
        if (roomType == null) {
            return new response(response.BAD_REQUEST, "Tipo de habitación inválido.");
        }

        Hospitalization hospitalization = service.createHospitalization(patient, doctor,
                date, reason, roomType, observations);
        service.saveHospitalization(hospitalization);

        return new response(response.SUCCESS, "Hospitalización solicitada. ID: " + 
                           hospitalization.getId());
    }

    @Override
    public response hospitalizeFromAppointment(String appointmentId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations) {

        Appointment appointment = store.findIDapp(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no es el asignado a esta cita.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new response(response.BAD_REQUEST, 
                "Solo se puede hospitalizar desde citas PENDING.");
        }

        if (!validacionesformato.isValidDate(dateStr)) {
            return new response(response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        LocalDate date = LocalDate.parse(dateStr);
        RoomType roomType = service.parseRoomType(roomTypeStr);
        if (roomType == null) {
            return new response(response.BAD_REQUEST, "Tipo de habitación inválido.");
        }

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        
        Hospitalization hospitalization = service.createHospitalizationFromAppointment(
                patient, doctor, date, reason, roomType, observations);
        service.saveHospitalization(hospitalization);

        return new response(response.SUCCESS, "Paciente hospitalizado. ID: " + 
                           hospitalization.getId());
    }

    @Override
    public response approveHospitalization(String hospId, long doctorId) {
        Hospitalization hosp = getHospitalizationOrNull(hospId);
        if (hosp == null) {
            return new response(response.NOT_FOUND, "Hospitalización no encontrada.");
        }

        if (hosp.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no está asignado a esta hospitalización.");
        }

        if (hosp.getStatus() != HospitalizationStatus.REQUESTED) {
            return new response(response.BAD_REQUEST, 
                "Solo se pueden aprobar hospitalizaciones en estado REQUESTED.");
        }

        hosp.setStatus(HospitalizationStatus.ONGOING);
        return new response(response.SUCCESS, "Hospitalización aprobada.");
    }

    @Override
    public response cancelHospitalization(String hospId, long doctorId) {
        Hospitalization hosp = getHospitalizationOrNull(hospId);
        if (hosp == null) {
            return new response(response.NOT_FOUND, "Hospitalización no encontrada.");
        }

        if (hosp.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no está asignado a esta hospitalización.");
        }

        if (hosp.getStatus() != HospitalizationStatus.REQUESTED) {
            return new response(response.BAD_REQUEST, 
                "Solo se pueden cancelar hospitalizaciones en estado REQUESTED.");
        }

        hosp.setStatus(HospitalizationStatus.CANCELED);
        return new response(response.SUCCESS, "Hospitalización cancelada.");
    }

    @Override
    public response getAllHospitalizations() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Hospitalization h : store.getHospitalizations()) {
            list.add(formatter.toMap(h));
        }
        return new response(response.SUCCESS, "OK", list);
    }


    private Patient getPatientOrNull(long patientId) {
        User found = store.findID(patientId);
        if (found instanceof Patient) {
            return (Patient) found;
        }
        return null;
    }

    private Doctor getDoctorOrNull(long doctorId) {
        User found = store.findID(doctorId);
        if (found instanceof Doctor) {
            return (Doctor) found;
        }
        return null;
    }

    private Hospitalization getHospitalizationOrNull(String hospId) {
        return store.findHospitalizationID(hospId);
    }

}
