/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Appointment;

import packagee.core.person.User;
import packagee.core.Doctor.Doctor;
import packagee.core.Patient.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.core.hospital.DataBase;
import packagee.Prescription;
import packagee.response;
import packagee.Specialty;
import packagee.core.control.validacionesformato;

/**
 *
 * @author juand
 */

public class AppointmentControl implements AppointmentControlint {

    private final DataBase store;
    private final validacionesformato validator;
    private final AppointmentFormatter formatter;
    private final AppointmentService service;

    public AppointmentControl(DataBase store) {
        this.store = store;
        this.validator = new validacionesformato();
        this.formatter = new AppointmentFormatter();
        this.service = new AppointmentService(store);
    }

    @Override
    public response requestAppointment(long patientId, boolean byDoctor,
            String doctorOrSpecialtyStr, String dateStr, String timeStr,
            String reason, boolean isInPerson) {

        if (!validacionesformato.isValidDate(dateStr)) {
            return new response(response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        if (!validacionesformato.isValidTime(timeStr)) {
            return new response(response.BAD_REQUEST, "Hora inválida. Formato: hh:mm");
        }

        Patient patient = getPatientOrNull(patientId);
        if (patient == null) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }

        Doctor doctor;
        Specialty specialty;

        if (byDoctor) {
            doctor = findDoctorById(doctorOrSpecialtyStr, dateStr, timeStr);
            if (doctor == null) {
                return new response(response.CONFLICT, 
                    "El doctor no tiene disponibilidad en ese horario.");
            }
            specialty = doctor.getSpecialty();
        } else {
            specialty = service.parseSpecialty(doctorOrSpecialtyStr);
            if (specialty == null) {
                return new response(response.BAD_REQUEST, "Especialidad inválida.");
            }

            doctor = service.findAvailableDoctorBySpecialty(specialty, 
                    parseDateTime(dateStr, timeStr));
            if (doctor == null) {
                return new response(response.CONFLICT, 
                    "No hay doctores disponibles para esa especialidad y horario.");
            }
        }

        LocalDateTime datetime = parseDateTime(dateStr, timeStr);
        Appointment appointment = service.createAppointment(patient, doctor, 
                                                            datetime, reason, isInPerson);
        service.saveAppointment(appointment, patient, doctor);

        return new response(response.SUCCESS, "Cita solicitada exitosamente. ID: " + 
                           appointment.getId(), formatter.toMap(appointment));
    }

    @Override
    public response acceptAppointment(String appointmentId, long doctorId) {
        Appointment appointment = getAppointmentOrNull(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        Doctor doctor = getDoctorOrNull(doctorId);
        if (doctor == null) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no es el asignado a esta cita.");
        }

        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            return new response(response.BAD_REQUEST, 
                "Solo se pueden aceptar citas en estado REQUESTED.");
        }

        appointment.setStatus(AppointmentStatus.PENDING);
        return new response(response.SUCCESS, "Cita aceptada.");
    }

    @Override
    public response completeAppointment(String appointmentId, long doctorId,
            String diagnosis, String observations, String recommendedTreatment, 
            String followUp) {
        
        Appointment appointment = getAppointmentOrNull(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no es el asignado a esta cita.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new response(response.BAD_REQUEST, 
                "Solo se pueden completar citas en estado PENDING.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDiagnosis(diagnosis);
        appointment.setObservations(observations);
        appointment.setRecommendedTreatment(recommendedTreatment);
        appointment.setFollowUp(followUp);

        return new response(response.SUCCESS, "Cita completada.");
    }

    @Override
    public response cancelAppointment(String appointmentId, long patientId) {
        Appointment appointment = getAppointmentOrNull(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        if (appointment.getPatient().getId() != patientId) {
            return new response(response.UNAUTHORIZED, 
                "Esta cita no pertenece a este paciente.");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return new response(response.BAD_REQUEST, 
                "No se puede cancelar una cita ya completada.");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        return new response(response.SUCCESS, "Cita cancelada.");
    }

    @Override
    public response rescheduleAppointment(String appointmentId, long doctorId,
            String newTimeStr, String rescheduleReason) {
        
        Appointment appointment = getAppointmentOrNull(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no es el asignado a esta cita.");
        }

        if (!validacionesformato.isValidTime(newTimeStr)) {
            return new response(response.BAD_REQUEST, "Hora inválida. Formato: hh:mm");
        }

        LocalDateTime newDatetime = LocalDateTime.of(
            appointment.getDatetime().toLocalDate(),
            LocalTime.parse(newTimeStr));
        
        appointment.setDatetime(newDatetime);
        String updatedReason = appointment.getReason() + " | Reagendado: " + rescheduleReason;
        appointment.setReason(updatedReason);

        return new response(response.SUCCESS, "Cita reagendada.");
    }

    @Override
    public response prescribeMedication(String appointmentId, long doctorId,
            String medicationName, double dose, String administrationRoute,
            int treatmentDuration, String additionalInstructions, int frecuency) {

        Appointment appointment = getAppointmentOrNull(appointmentId);
        if (appointment == null) {
            return new response(response.NOT_FOUND, "Cita no encontrada.");
        }

        if (appointment.getDoctor().getId() != doctorId) {
            return new response(response.UNAUTHORIZED, 
                "Este doctor no es el asignado a esta cita.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new response(response.BAD_REQUEST, 
                "Solo se pueden prescribir medicamentos en citas PENDING.");
        }

        Prescription prescription = new Prescription(appointment, medicationName, dose,
                administrationRoute, treatmentDuration, additionalInstructions, frecuency);
        appointment.addPrescription(prescription);

        return new response(response.SUCCESS, "Medicamento prescrito exitosamente.");
    }

    @Override
    public response getPatientAppointments(long patientId) {
        Patient patient = getPatientOrNull(patientId);
        if (patient == null) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }

        ArrayList<Appointment> sorted = new ArrayList<>(patient.getAppointments());
        sorted.sort(Comparator.comparing(Appointment::getDatetime).reversed());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Appointment a : sorted) {
            list.add(formatter.toMap(a));
        }

        return new response(response.SUCCESS, "OK", list);
    }

    @Override
    public response getDoctorAppointments(long doctorId, boolean pendingOnly) {
        Doctor doctor = getDoctorOrNull(doctorId);
        if (doctor == null) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        ArrayList<Appointment> sorted = new ArrayList<>(doctor.getAppointments());
        if (pendingOnly) {
            sorted.removeIf(a -> a.getStatus() != AppointmentStatus.PENDING);
        }
        sorted.sort(Comparator.comparing(Appointment::getDatetime).reversed());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Appointment a : sorted) {
            list.add(formatter.toMap(a));
        }

        return new response(response.SUCCESS, "OK", list);
    }

    // ========== MÉTODOS HELPER ==========

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

    private Appointment getAppointmentOrNull(String appointmentId) {
        return store.findIDapp(appointmentId);
    }

    private Doctor findDoctorById(String doctorIdStr, String dateStr, String timeStr) {
        try {
            long docId = Long.parseLong(doctorIdStr);
            Doctor doctor = getDoctorOrNull(docId);
            
            if (doctor != null && store.doctorAvailable(doctor, 
                    parseDateTime(dateStr, timeStr))) {
                return doctor;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);
        return LocalDateTime.of(date, time);
    }

}