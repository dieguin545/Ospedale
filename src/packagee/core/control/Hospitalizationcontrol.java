/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */
import packagee.core.person.User;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;
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

public class Hospitalizationcontrol {

    private final DataBase store = DataBase.getInstance();

    
    public  response requestHospitalization(long patientId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations) {

        User patientFound = store.findUserById(patientId);
        if (patientFound == null || !(patientFound instanceof Patient)) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }
        User doctorFound = store.findUserById(doctorId);
        if (doctorFound == null || !(doctorFound instanceof Doctor)) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return new response(response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        RoomType roomType;
        try {
            roomType = RoomType.valueOf(roomTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new response(response.BAD_REQUEST, "Tipo de habitación inválido.");
        }

        Patient patient = (Patient) patientFound;
        Doctor doctor = (Doctor) doctorFound;

        String hospId = store.generateHospitalizationId(patientId);
        Hospitalization hospitalization = new Hospitalization(hospId, patient, doctor,
                date, reason, roomType, observations);

        store.addHospitalization(hospitalization);
        patient.setHospitalization(hospitalization);
        doctor.addHospitalization(hospitalization);

        return new response(response.SUCCESS, "Hospitalización solicitada. ID: " + hospId);
    }

    public response hospitalizeFromAppointment(String appointmentId, long doctorId,
            String dateStr, String reason, String roomTypeStr, String observations) {

        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, "Este doctor no es el asignado a esta cita.");
        }
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new response(response.BAD_REQUEST, "Solo se puede hospitalizar desde citas PENDING.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return new response(response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        RoomType roomType;
        try {
            roomType = RoomType.valueOf(roomTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new response(response.BAD_REQUEST, "Tipo de habitación inválido.");
        }

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        appointment.setStatus(AppointmentStatus.COMPLETED);

        String hospId = store.generateHospitalizationId(patient.getId());
        Hospitalization hospitalization = new Hospitalization(hospId, patient, doctor,
                date, reason, roomType, observations, HospitalizationStatus.ONGOING);

        store.addHospitalization(hospitalization);
        patient.setHospitalization(hospitalization);
        doctor.addHospitalization(hospitalization);

        return new response(response.SUCCESS, "Paciente hospitalizado. ID: " + hospId);
    }

    public response approveHospitalization(String hospId, long doctorId) {
        Hospitalization hosp = store.findHospitalizationById(hospId);
        if (hosp == null) {
            return new response(response.NOT_FOUND, "Hospitalización no encontrada.");
        }
        if (hosp.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, "Este doctor no está asignado a esta hospitalización.");
        }
        if (hosp.getStatus() != HospitalizationStatus.REQUESTED) {
            return new response(response.BAD_REQUEST, "Solo se pueden aprobar hospitalizaciones en estado REQUESTED.");
        }
        hosp.setStatus(HospitalizationStatus.ONGOING);
        return new response(response.SUCCESS, "Hospitalización aprobada.");
    }

    public response cancelHospitalization(String hospId, long doctorId) {
        Hospitalization hosp = store.findHospitalizationById(hospId);
        if (hosp == null) {
            return new response(response.NOT_FOUND, "Hospitalización no encontrada.");
        }
        if (hosp.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, "Este doctor no está asignado a esta hospitalización.");
        }
        if (hosp.getStatus() == HospitalizationStatus.ONGOING) {
            return new response(response.BAD_REQUEST, "No se puede cancelar una hospitalización en curso.");
        }
        hosp.setStatus(HospitalizationStatus.CANCELED);
        return new response(response.SUCCESS, "Hospitalización cancelada.");
    }

    public response getAllHospitalizations() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Hospitalization h : store.getHospitalizations()) {
            list.add(serializeHospitalization(h));
        }
        return new response(response.SUCCESS, "OK", list);
    }

    private Map<String, Object> serializeHospitalization(Hospitalization h) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", h.getId());
        data.put("patientName", h.getPatient().getFirstname() + " " + h.getPatient().getLastname());
        data.put("doctorName", h.getDoctor().getFirstname() + " " + h.getDoctor().getLastname());
        data.put("date", h.getDate().toString());
        data.put("reason", h.getReason());
        data.put("roomType", h.getRoomType().name());
        data.put("status", h.getStatus().name());
        data.put("observations", h.getObservations());
        return data;
    }
}
