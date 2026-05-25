package packagee.core.hospital;

import packagee.Appointment;
import java.util.ArrayList;
import java.util.Optional;

public interface AppointmentRepository {
    void addAppointment(Appointment appointment);
    Optional<Appointment> findIDapp(String id);
    ArrayList<Appointment> getAppointments();
}

class InMemoryAppointmentRepository implements AppointmentRepository {

    private final ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public Optional<Appointment> findIDapp(String id) {
        return appointments.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    @Override
    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }
}
