package packagee.core.Appointment;

import packagee.response;


public interface AppointmentControlint {


    response requestAppointment(long patientId, boolean byDoctor,
            String doctorOrSpecialtyStr, String dateStr, String timeStr,
            String reason, boolean isInPerson);


    response acceptAppointment(String appointmentId, long doctorId);

    response completeAppointment(String appointmentId, long doctorId,
            String diagnosis, String observations,
            String recommendedTreatment, String followUp);


    response cancelAppointment(String appointmentId, long patientId);


    response rescheduleAppointment(String appointmentId, long doctorId,
            String newTimeStr, String rescheduleReason);


    response prescribeMedication(String appointmentId, long doctorId,
            String medicationName, double dose, String administrationRoute,
            int treatmentDuration, String additionalInstructions, int frecuency);


    response getPatientAppointments(long patientId);


    response getDoctorAppointments(long doctorId, boolean pendingOnly);
}
