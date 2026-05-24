/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.hospital;

import packagee.core.person.Administrator;
import packagee.core.person.User;
import packagee.core.Doctor.Doctor;
import packagee.core.Patient.Patient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.Specialty;

/**
 *
 * @author juand
 */
public class DataBase {

    private static DataBase instance;

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentIDGenerator appointmentIDGenerator;
    private final HospitalizationIDGenerator hospitalizationIDGenerator;
    private final DoctorAvailabilityService doctorAvailabilityService;

    private DataBase() {
        UserLoader loader = new JSONUserLoader();
        this.userRepository = new InMemoryUserRepository(loader, "json/users.json");
        this.appointmentRepository = new InMemoryAppointmentRepository();
        this.hospitalizationRepository = new InMemoryHospitalizationRepository();
        this.appointmentIDGenerator = new AppointmentCounterGenerator();
        this.hospitalizationIDGenerator = new HospitalizationCounterGenerator();
        this.doctorAvailabilityService = new DoctorAvailabilityService(appointmentRepository);
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUser(username).orElse(null);
    }

    public User findUserById(long id) {
        return userRepository.findID(id).orElse(null);
    }

    public User findUser(String username) {
        return userRepository.findUser(username).orElse(null);
    }

    public User findID(long id) {
        return userRepository.findID(id).orElse(null);
    }

    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public ArrayList<Doctor> getDoctors() {
        return userRepository.getDoctors();
    }

    public boolean idExists(long id) {
        return userRepository.findID(id).isPresent();
    }

    public boolean usernameExists(String username) {
        return userRepository.findUser(username).isPresent();
    }

    public boolean userExists(String username) {
        return userRepository.findUser(username).isPresent();
    }

    public void addAppointment(Appointment appointment) {
        appointmentRepository.addAppointment(appointment);
    }

    public Appointment findAppointmentById(String id) {
        return appointmentRepository.findIDapp(id).orElse(null);
    }

    public Appointment findIDapp(String id) {
        return appointmentRepository.findIDapp(id).orElse(null);
    }

    public String generateAppointmentId(long patientId) {
        return appointmentIDGenerator.generateAppID(patientId);
    }

    public String generateAppID(long patientId) {
        return appointmentIDGenerator.generateAppID(patientId);
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime datetime) {
        return doctorAvailabilityService.doctorAvailable(doctor, datetime);
    }

    public boolean doctorAvailable(Doctor doctor, LocalDateTime datetime) {
        return doctorAvailabilityService.doctorAvailable(doctor, datetime);
    }

    public ArrayList<Doctor> getAvailableDoctorsBySpecialty(Specialty specialty, LocalDateTime datetime) {
        return doctorAvailabilityService.getdoctorsSpeciality(specialty, datetime, userRepository);
    }

    public ArrayList<Doctor> getdoctorsSpeciality(Specialty specialty, LocalDateTime datetime) {
        return doctorAvailabilityService.getdoctorsSpeciality(specialty, datetime, userRepository);
    }

    public void addHospitalization(Hospitalization hospitalization) {
        hospitalizationRepository.addHospitalization(hospitalization);
    }

    public Hospitalization findHospitalizationById(String id) {
        return hospitalizationRepository.findHospitalizationID(id).orElse(null);
    }

    public Hospitalization findHospitalizationID(String id) {
        return hospitalizationRepository.findHospitalizationID(id).orElse(null);
    }

    public String generateHospitalizationId(long patientId) {
        return hospitalizationIDGenerator.genHospitalizationID(patientId);
    }

    public String genHospitalizationID(long patientId) {
        return hospitalizationIDGenerator.genHospitalizationID(patientId);
    }

    public ArrayList<Hospitalization> getHospitalizations() {
        return hospitalizationRepository.getHospitalizations();
    }

    private interface UserRepository {
        void addUser(User user);
        Optional<User> findUser(String username);
        Optional<User> findID(long id);
        ArrayList<User> getUsers();
        ArrayList<Doctor> getDoctors();
    }

    private interface AppointmentRepository {
        void addAppointment(Appointment appointment);
        Optional<Appointment> findIDapp(String id);
        ArrayList<Appointment> getAppointments();
    }

    private interface HospitalizationRepository {
        void addHospitalization(Hospitalization hospitalization);
        Optional<Hospitalization> findHospitalizationID(String id);
        ArrayList<Hospitalization> getHospitalizations();
    }

    private interface AppointmentIDGenerator {
        String generateAppID(long patientId);
    }

    private interface HospitalizationIDGenerator {
        String genHospitalizationID(long patientId);
    }

    private interface UserLoader {
        ArrayList<User> cargarJSON(String path);
    }

    private static class InMemoryUserRepository implements UserRepository {

        private final ArrayList<User> users;

        public InMemoryUserRepository(UserLoader loader, String path) {
            this.users = loader.cargarJSON(path);
        }

        @Override
        public void addUser(User user) {
            users.add(user);
        }

        @Override
        public Optional<User> findUser(String username) {
            return users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();
        }

        @Override
        public Optional<User> findID(long id) {
            return users.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst();
        }

        @Override
        public ArrayList<User> getUsers() {
            return users;
        }

        @Override
        public ArrayList<Doctor> getDoctors() {
            ArrayList<Doctor> doctors = new ArrayList<>();
            for (User u : users) {
                if (u instanceof Doctor) {
                    doctors.add((Doctor) u);
                }
            }
            return doctors;
        }
    }

    private static class InMemoryAppointmentRepository implements AppointmentRepository {

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

    private static class InMemoryHospitalizationRepository implements HospitalizationRepository {

        private final ArrayList<Hospitalization> hospitalizations = new ArrayList<>();

        @Override
        public void addHospitalization(Hospitalization hospitalization) {
            hospitalizations.add(hospitalization);
        }

        @Override
        public Optional<Hospitalization> findHospitalizationID(String id) {
            return hospitalizations.stream()
                    .filter(h -> h.getId().equals(id))
                    .findFirst();
        }

        @Override
        public ArrayList<Hospitalization> getHospitalizations() {
            return hospitalizations;
        }
    }

    private static class AppointmentCounterGenerator implements AppointmentIDGenerator {

        private final HashMap<String, Integer> appointmentCounters = new HashMap<>();

        @Override
        public String generateAppID(long patientId) {
            String key = String.valueOf(patientId);
            int counter = appointmentCounters.getOrDefault(key, 0);
            String id = String.format("A-%s-%04d", key, counter);
            appointmentCounters.put(key, counter + 1);
            return id;
        }
    }

    private static class HospitalizationCounterGenerator implements HospitalizationIDGenerator {

        private final HashMap<String, Integer> hospitalizationCounters = new HashMap<>();

        @Override
        public String genHospitalizationID(long patientId) {
            String key = String.valueOf(patientId);
            int counter = hospitalizationCounters.getOrDefault(key, 0);
            String id = String.format("H-%s-%04d", key, counter);
            hospitalizationCounters.put(key, counter + 1);
            return id;
        }
    }

    private static class JSONUserLoader implements UserLoader {

        @Override
        public ArrayList<User> cargarJSON(String path) {
            ArrayList<User> users = new ArrayList<>();
            try {
                String content = new String(Files.readAllBytes(Paths.get(path)));
                JSONObject root = new JSONObject(content);
                JSONArray usersArray = root.getJSONArray("users");
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject obj = usersArray.getJSONObject(i);
                    String type = obj.getString("type");
                    long id = obj.getLong("id");
                    String username = obj.getString("username");
                    String firstname = obj.getString("firstname");
                    String lastname = obj.getString("lastname");
                    String password = obj.getString("password");
                    switch (type) {
                        case "admin":
                            users.add(new Administrator(id, username, firstname, lastname, password));
                            break;
                        case "patient":
                            String email = obj.getString("email");
                            LocalDate birthdate = LocalDate.parse(obj.getString("birthdate"));
                            boolean gender = obj.getBoolean("gender");
                            long phone = obj.getLong("phone");
                            String address = obj.getString("address");
                            users.add(new Patient(id, username, firstname, lastname, password,
                                    email, birthdate, gender, phone, address));
                            break;
                        case "doctor":
                            String specialtyStr = obj.getString("specialty");
                            Specialty specialty = parseSpecialty(specialtyStr);
                            String licenceNumber = obj.getString("licenceNumber");
                            String assignedOffice = obj.getString("assignedOffice");
                            users.add(new Doctor(id, username, firstname, lastname, password,
                                    specialty, licenceNumber, assignedOffice));
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("No se pudo cargar users.json", e);
            }
            return users;
        }

        private Specialty parseSpecialty(String specialtyStr) {
            switch (specialtyStr.toUpperCase()) {
                case "ORTHOPEDICS":
                case "TRAUMATOLOGY_ORTHOPEDICS":
                    return Specialty.TRAUMATOLOGY_ORTHOPEDICS;
                case "GYNECOLOGY":
                case "GYNECOLOGY_OBSTETRICS":
                    return Specialty.GYNECOLOGY_OBSTETRICS;
                default:
                    return Specialty.valueOf(specialtyStr.toUpperCase());
            }
        }
    }

    private static class DoctorAvailabilityService {

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
}
