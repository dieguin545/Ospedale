/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee;

import packagee.core.person.User;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juand
 */

public class AppointmentController {

    private final DataBase store = DataBase.getInstance();


    public Response requestAppointment(long patientId, boolean byDoctor,
            String doctorOrSpecialtyStr, String dateStr, String timeStr,
            String reason, boolean isInPerson) {

        User found = store.findUserById(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new Response(Response.NOT_FOUND, "Paciente no encontrado.");
        }
        Patient patient = (Patient) found;

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return new Response(Response.BAD_REQUEST, "Fecha inválida. Formato: AAAA-MM-DD");
        }

        if (!timeStr.matches("\\d{2}:\\d{2}")) {
            return new Response(Response.BAD_REQUEST, "Hora inválida. Formato: hh:mm");
        }
        int hour = Integer.parseInt(timeStr.substring(0, 2));
        int minute = Integer.parseInt(timeStr.substring(3));
        if (hour < 0 || hour > 23 || (minute != 0 && minute != 15 && minute != 30 && minute != 45)) {
            return new Response(Response.BAD_REQUEST, "Los minutos deben ser 00, 15, 30 o 45.");
        }
        LocalDateTime datetime = LocalDateTime.of(date, LocalTime.of(hour, minute));

        Doctor doctor;
        Specialty specialty;

        if (byDoctor) {
            long docId;
            try {
                docId = Long.parseLong(doctorOrSpecialtyStr);
            } catch (NumberFormatException e) {
                return new Response(Response.BAD_REQUEST, "ID de doctor inválido.");
            }
            User docFound = store.findUserById(docId);
            if (docFound == null || !(docFound instanceof Doctor)) {
                return new Response(Response.NOT_FOUND, "Doctor no encontrado.");
            }
            doctor = (Doctor) docFound;
            specialty = doctor.getSpecialty();

            if (!store.isDoctorAvailable(doctor, datetime)) {
                return new Response(Response.CONFLICT, "El doctor no tiene disponibilidad en ese horario.");
            }
        } else {
            try {
                specialty = Specialty.valueOf(
                        doctorOrSpecialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                return new Response(Response.BAD_REQUEST, "Especialidad inválida.");
            }
            ArrayList<Doctor> available = store.getAvailableDoctorsBySpecialty(specialty, datetime);
            if (available.isEmpty()) {
                return new Response(Response.CONFLICT, "No hay doctores disponibles para esa especialidad y horario.");
            }
            doctor = available.get(0); // asigna el primero disponible
        }

        String appointmentId = store.generateAppointmentId(patientId);
        Appointment appointment = new Appointment(appointmentId, patient, doctor,
                specialty, datetime, reason, isInPerson);

        store.addAppointment(appointment);
        patient.addAppointment(appointment);
        doctor.addAppointment(appointment);

        return new Response(Response.SUCCESS, "Cita solicitada exitosamente. ID: " + appointmentId);
    }


    public Response acceptAppointment(String appointmentId, long doctorId) {
        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new Response(Response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getDoctor().getId() != doctorId) {
            return new Response(Response.UNAUTHORIZED, "Este doctor no es el asignado a esta cita.");
        }
        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            return new Response(Response.BAD_REQUEST, "Solo se pueden aceptar citas en estado REQUESTED.");
        }
        appointment.setStatus(AppointmentStatus.PENDING);
        return new Response(Response.SUCCESS, "Cita aceptada.");
    }


    public Response completeAppointment(String appointmentId, long doctorId,
            String diagnosis, String observations, String recommendedTreatment, String followUp) {
        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new Response(Response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getDoctor().getId() != doctorId) {
            return new Response(Response.UNAUTHORIZED, "Este doctor no es el asignado a esta cita.");
        }
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new Response(Response.BAD_REQUEST, "Solo se pueden completar citas en estado PENDING.");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDiagnosis(diagnosis);
        appointment.setObservations(observations);
        appointment.setRecommendedTreatment(recommendedTreatment);
        appointment.setFollowUp(followUp);
        return new Response(Response.SUCCESS, "Cita completada.");
    }


    public Response cancelAppointment(String appointmentId, long patientId) {
        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new Response(Response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getPatient().getId() != patientId) {
            return new Response(Response.UNAUTHORIZED, "Esta cita no pertenece a este paciente.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return new Response(Response.BAD_REQUEST, "No se puede cancelar una cita ya completada.");
        }
        appointment.setStatus(AppointmentStatus.CANCELED);
        return new Response(Response.SUCCESS, "Cita cancelada.");
    }


    public Response rescheduleAppointment(String appointmentId, long doctorId,
            String newTimeStr, String rescheduleReason) {
        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new Response(Response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getDoctor().getId() != doctorId) {
            return new Response(Response.UNAUTHORIZED, "Este doctor no es el asignado a esta cita.");
        }

        if (!newTimeStr.matches("\\d{2}:\\d{2}")) {
            return new Response(Response.BAD_REQUEST, "Hora inválida. Formato: hh:mm");
        }
        int hour = Integer.parseInt(newTimeStr.substring(0, 2));
        int minute = Integer.parseInt(newTimeStr.substring(3));
        if (hour < 0 || hour > 23 || (minute != 0 && minute != 15 && minute != 30 && minute != 45)) {
            return new Response(Response.BAD_REQUEST, "Los minutos deben ser 00, 15, 30 o 45.");
        }

        LocalDateTime newDatetime = LocalDateTime.of(appointment.getDatetime().toLocalDate(),
                LocalTime.of(hour, minute));
        appointment.setDatetime(newDatetime);

        String updatedReason = appointment.getReason() + " | Reagendado: " + rescheduleReason;
        appointment.setReason(updatedReason);

        return new Response(Response.SUCCESS, "Cita reagendada.");
    }


    public Response prescribeMedication(String appointmentId, long doctorId,
            String medicationName, double dose, String administrationRoute,
            int treatmentDuration, String additionalInstructions, int frecuency) {

        Appointment appointment = store.findAppointmentById(appointmentId);
        if (appointment == null) {
            return new Response(Response.NOT_FOUND, "Cita no encontrada.");
        }
        if (appointment.getDoctor().getId() != doctorId) {
            return new Response(Response.UNAUTHORIZED, "Este doctor no es el asignado a esta cita.");
        }
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return new Response(Response.BAD_REQUEST, "Solo se pueden prescribir medicamentos en citas PENDING.");
        }

        Prescription prescription = new Prescription(appointment, medicationName, dose,
                administrationRoute, treatmentDuration, additionalInstructions, frecuency);
        appointment.addPrescription(prescription);

        return new Response(Response.SUCCESS, "Medicamento prescrito exitosamente.");
    }

    public Response getPatientAppointments(long patientId) {
        User found = store.findUserById(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new Response(Response.NOT_FOUND, "Paciente no encontrado.");
        }
        Patient patient = (Patient) found;
        ArrayList<Appointment> sorted = new ArrayList<>(patient.getAppointments());
        sorted.sort(Comparator.comparing(Appointment::getDatetime).reversed());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Appointment a : sorted) {
            list.add(serializeAppointment(a));
        }
        return new Response(Response.SUCCESS, "OK", list);
    }


    public Response getDoctorAppointments(long doctorId, boolean pendingOnly) {
        User found = store.findUserById(doctorId);
        if (found == null || !(found instanceof Doctor)) {
            return new Response(Response.NOT_FOUND, "Doctor no encontrado.");
        }
        Doctor doctor = (Doctor) found;
        ArrayList<Appointment> sorted = new ArrayList<>(doctor.getAppointments());
        if (pendingOnly) {
            sorted.removeIf(a -> a.getStatus() != AppointmentStatus.PENDING);
        }
        sorted.sort(Comparator.comparing(Appointment::getDatetime).reversed());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Appointment a : sorted) {
            list.add(serializeAppointment(a));
        }
        return new Response(Response.SUCCESS, "OK", list);
    }

    private Map<String, Object> serializeAppointment(Appointment a) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", a.getId());
        data.put("datetime", a.getDatetime().toString());
        data.put("doctorName", a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname());
        data.put("patientName", a.getPatient().getFirstname() + " " + a.getPatient().getLastname());
        data.put("specialty", a.getSpecialty().name());
        data.put("type", a.isType() ? "In-person" : "Remote");
        data.put("status", a.getStatus().name());
        data.put("reason", a.getReason());
        return data;
    }
}
