package packagee.core.hospital;

import packagee.core.person.Doctor;
import packagee.core.person.User;
import packagee.Appointment;
import packagee.Specialty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {

    private static DataBase instance;

    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final HospitalizationRepository hospitalizations;
    private final IDGenerator appIDGen;
    private final IDGenerator hospIDGen;
    private final DoctorAvailabilityService availability;

    private DataBase(UserRepository users, AppointmentRepository appointments,
            HospitalizationRepository hospitalizations,
            IDGenerator appIDGen, IDGenerator hospIDGen,
            DoctorAvailabilityService availability) {
        this.users = users;
        this.appointments = appointments;
        this.hospitalizations = hospitalizations;
        this.appIDGen = appIDGen;
        this.hospIDGen = hospIDGen;
        this.availability = availability;
    }

    public static DataBase getInstance() {
        if (instance == null) {
            AppointmentRepository apps = new InMemoryAppointmentRepository();
            HospitalizationRepository hosps = new InMemoryHospitalizationRepository();
            UserRepository users = new InMemoryUserRepository(
                    new JSONUserLoader().cargarJSON("json/users.json"));
            instance = new DataBase(
                    users, apps, hosps,
                    new AppointmentCounter(),
                    new HospitalizationCounter(),
                    new DoctorAvailabilityService(apps));
        }
        return instance;
    }

    public void addUser(User user) { users.addUser(user); }
    public User findUser(String username) { return users.findUser(username).orElse(null); }
    public User findID(long id) { return users.findID(id).orElse(null); }
    public ArrayList<User> getUsers() { return users.getUsers(); }
    public ArrayList<Doctor> getDoctors() { return users.getDoctors(); }
    public boolean idExists(long id) { return users.findID(id).isPresent(); }
    public boolean userExists(String username) { return users.findUser(username).isPresent(); }

    public void addAppointment(Appointment appointment) { appointments.addAppointment(appointment); }
    public Appointment findIDapp(String id) { return appointments.findIDapp(id).orElse(null); }
    public String generateAppID(long patientId) { return appIDGen.generate("A", patientId); }
    public boolean doctorAvailable(Doctor doctor, LocalDateTime datetime) {
        return availability.doctorAvailable(doctor, datetime);
    }
    public ArrayList<Doctor> getdoctorsSpeciality(Specialty specialty, LocalDateTime datetime) {
        return availability.getdoctorsSpeciality(specialty, datetime, users);
    }

    public void addHospitalization(Hospitalization hospitalization) {
        hospitalizations.addHospitalization(hospitalization);
    }
    public Hospitalization findHospitalizationID(String id) {
        return hospitalizations.findHospitalizationID(id).orElse(null);
    }
    public String genHospitalizationID(long patientId) { return hospIDGen.generate("H", patientId); }
    public ArrayList<Hospitalization> getHospitalizations() { return hospitalizations.getHospitalizations(); }

    private interface IDGenerator {
        String generate(String prefix, long ownerId);
    }

    private static class AppointmentCounter implements IDGenerator {
        private final HashMap<String, Integer> counters = new HashMap<>();
        @Override
        public String generate(String prefix, long ownerId) {
            String key = String.valueOf(ownerId);
            int counter = counters.getOrDefault(key, 0);
            String id = String.format("%s-%s-%04d", prefix, key, counter);
            counters.put(key, counter + 1);
            return id;
        }
    }

    private static class HospitalizationCounter implements IDGenerator {
        private final HashMap<String, Integer> counters = new HashMap<>();
        @Override
        public String generate(String prefix, long ownerId) {
            String key = String.valueOf(ownerId);
            int counter = counters.getOrDefault(key, 0);
            String id = String.format("%s-%s-%04d", prefix, key, counter);
            counters.put(key, counter + 1);
            return id;
        }
    }
}
