package packagee.core.hospital;

import packagee.core.person.Doctor;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.Specialty;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DoctorAvailabilityService {

    private final AppointmentRepository appointmentRepository;

    public DoctorAvailabilityService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean doctorAvailable(Doctor doctor, LocalDateTime datetime) {
        LocalDateTime end = datetime.plusMinutes(15);
        for (Appointment a : appointmentRepository.getAppointments()) {
            if (a.getDoctor().getId() == doctor.getId()
                    && a.getStatus() != AppointmentStatus.CANCELED
                    && a.getStatus() != AppointmentStatus.COMPLETED) {
                LocalDateTime aStart = a.getDatetime();
                LocalDateTime aEnd = aStart.plusMinutes(15);
                if (datetime.isBefore(aEnd) && end.isAfter(aStart)) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Doctor> getdoctorsSpeciality(Specialty specialty,
            LocalDateTime datetime, UserRepository userRepository) {
        ArrayList<Doctor> available = new ArrayList<>();
        for (Doctor d : userRepository.getDoctors()) {
            if (d.getSpecialty() == specialty && doctorAvailable(d, datetime)) {
                available.add(d);
            }
        }
        return available;
    }
}
