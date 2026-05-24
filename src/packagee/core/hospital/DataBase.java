/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.hospital;

import packagee.core.hospital.Hospitalization;
import packagee.core.person.Administrator;
import packagee.core.person.User;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import packagee.Appointment;
import packagee.AppointmentStatus;
import packagee.Specialty;
/**
 *
 * @author juand
 */

public interface UserRepository {

    void addUser(User user);

    Optional<User> findUser(String username);

    Optional<User> findID(long id);

    ArrayList<User> getUsers();

    ArrayList<Doctor> getDoctors();
}

public interface AppointmentRepository {

    void addAppointment(Appointment appointment);

    Optional<Appointment> findIDapp(String id);

    ArrayList<Appointment> getAppointments();
}


public interface HospitalizationRepository {

    void addHospitalization(Hospitalization hospitalization);

    Optional<Hospitalization> findHospitalizationID(String id);

    ArrayList<Hospitalization> getHospitalizations();
}

public interface AppointmentIDGenerator {
    String generateAppID(long patientId);
}

public interface HospitalizationIDGenerator {
    String genHospitalizationID(long patientId);
}

public class AppointmentCounterGenerator implements AppointmentIDGenerator {

    private final HashMap<String, Integer> appointmentCounters =
            new HashMap<>();

    @Override
    public String generateAppID(long patientId) {

        String key = String.valueOf(patientId);

        int counter = appointmentCounters.getOrDefault(key, 0);

        String id = String.format("A-%s-%04d", key, counter);

        appointmentCounters.put(key, counter + 1);

        return id;
    }
}

public class HospitalizationCounterGenerator
        implements HospitalizationIDGenerator {

    private final HashMap<String, Integer> hospitalizationCounters =
            new HashMap<>();

    @Override
    public String genHospitalizationID(long patientId) {

        String key = String.valueOf(patientId);

        int counter = hospitalizationCounters.getOrDefault(key, 0);

        String id = String.format("H-%s-%04d", key, counter);

        hospitalizationCounters.put(key, counter + 1);

        return id;
    }
}

public interface UserLoader {
    ArrayList<User> cargarJSON(String path);
}

public class JSONUserLoader implements UserLoader {

    @Override
    public ArrayList<User> cargarJSON(String path) {

        ArrayList<User> users = new ArrayList<>();

        try {

            String content =
                    new String(Files.readAllBytes(Paths.get(path)));

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

                        users.add(
                                new Administrator(
                                        id,
                                        username,
                                        firstname,
                                        lastname,
                                        password
                                )
                        );

                        break;

                    case "patient":

                        String email = obj.getString("email");

                        LocalDate birthdate =
                                LocalDate.parse(
                                        obj.getString("birthdate")
                                );

                        boolean gender =
                                obj.getBoolean("gender");

                        long phone =
                                obj.getLong("phone");

                        String address =
                                obj.getString("address");

                        users.add(
                                new Patient(
                                        id,
                                        username,
                                        firstname,
                                        lastname,
                                        password,
                                        email,
                                        birthdate,
                                        gender,
                                        phone,
                                        address
                                )
                        );

                        break;

                    case "doctor":

                        String specialtyStr =
                                obj.getString("specialty");

                        Specialty specialty =
                                parseSpecialty(specialtyStr);

                        String licenceNumber =
                                obj.getString("licenceNumber");

                        String assignedOffice =
                                obj.getString("assignedOffice");

                        users.add(
                                new Doctor(
                                        id,
                                        username,
                                        firstname,
                                        lastname,
                                        password,
                                        specialty,
                                        licenceNumber,
                                        assignedOffice
                                )
                        );

                        break;
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo cargar users.json",
                    e
            );
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

                return Specialty.valueOf(
                        specialtyStr.toUpperCase()
                );
        }
    }
}

public class InMemoryUserRepository
        implements UserRepository {

    private final ArrayList<User> users;

    public InMemoryUserRepository(
            UserLoader loader,
            String path
    ) {

        this.users = loader.cargarJSON(path);
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findUser(String username) {

        return users.stream()
                .filter(u ->
                        u.getUsername().equals(username)
                )
                .findFirst();
    }

    @Override
    public Optional<User> findID(long id) {

        return users.stream()
                .filter(u ->
                        u.getId() == id
                )
                .findFirst();
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public ArrayList<Doctor> getDoctors() {

        ArrayList<Doctor> doctors =
                new ArrayList<>();

        for (User u : users) {

            if (u instanceof Doctor) {
                doctors.add((Doctor) u);
            }
        }

        return doctors;
    }
}
public class InMemoryAppointmentRepository
        implements AppointmentRepository {

    private final ArrayList<Appointment> appointments =
            new ArrayList<>();

    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public Optional<Appointment> findIDapp(String id) {

        return appointments.stream()
                .filter(a ->
                        a.getId().equals(id)
                )
                .findFirst();
    }

    @Override
    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }
}

public class InMemoryHospitalizationRepository
        implements HospitalizationRepository {

    private final ArrayList<Hospitalization> hospitalizations =
            new ArrayList<>();

    @Override
    public void addHospitalization(
            Hospitalization hospitalization
    ) {

        hospitalizations.add(hospitalization);
    }

    @Override
    public Optional<Hospitalization> findHospitalizationID(
            String id
    ) {

        return hospitalizations.stream()
                .filter(h ->
                        h.getId().equals(id)
                )
                .findFirst();
    }

    @Override
    public ArrayList<Hospitalization> getHospitalizations() {
        return hospitalizations;
    }
}

public class DoctorAvailabilityService {

    private final AppointmentRepository appointmentRepository;

    public DoctorAvailabilityService(
            AppointmentRepository appointmentRepository
    ) {

        this.appointmentRepository =
                appointmentRepository;
    }

    public boolean doctorAvailable(
            Doctor doctor,
            LocalDateTime datetime
    ) {

        LocalDateTime end =
                datetime.plusMinutes(15);

        for (Appointment a :
                appointmentRepository.getAppointments()) {

            if (a.getDoctor().getId() == doctor.getId()
                    && a.getStatus()
                    != AppointmentStatus.CANCELED
                    && a.getStatus()
                    != AppointmentStatus.COMPLETED) {

                LocalDateTime aStart =
                        a.getDatetime();

                LocalDateTime aEnd =
                        aStart.plusMinutes(15);

                if (datetime.isBefore(aEnd)
                        && end.isAfter(aStart)) {

                    return false;
                }
            }
        }

        return true;
    }

    public ArrayList<Doctor> getdoctorsSpeciality(
            Specialty specialty,
            LocalDateTime datetime,
            UserRepository userRepository
    ) {

        ArrayList<Doctor> available =
                new ArrayList<>();

        for (Doctor d :
                userRepository.getDoctors()) {

            if (d.getSpecialty() == specialty
                    && doctorAvailable(d, datetime)) {

                available.add(d);
            }
        }

        return available;
    }
}


public class ApplicationContext {

    private final UserRepository userRepository;

    private final AppointmentRepository
            appointmentRepository;

    private final HospitalizationRepository
            hospitalizationRepository;

    private final AppointmentIDGenerator
            appointmentIDGenerator;

    private final HospitalizationIDGenerator
            hospitalizationIDGenerator;

    private final DoctorAvailabilityService
            doctorAvailabilityService;

    public ApplicationContext() {

        UserLoader loader =
                new JSONUserLoader();

        this.userRepository =
                new InMemoryUserRepository(
                        loader,
                        "C:\\Users\\juand\\OneDrive\\Desktop\\Ospedale\\json\\users.json"
                );

        this.appointmentRepository =
                new InMemoryAppointmentRepository();

        this.hospitalizationRepository =
                new InMemoryHospitalizationRepository();

        this.appointmentIDGenerator =
                new AppointmentCounterGenerator();

        this.hospitalizationIDGenerator =
                new HospitalizationCounterGenerator();

        this.doctorAvailabilityService =
                new DoctorAvailabilityService(
                        appointmentRepository
                );
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AppointmentRepository getAppointmentRepository() {
        return appointmentRepository;
    }

    public HospitalizationRepository
    getHospitalizationRepository() {

        return hospitalizationRepository;
    }

    public AppointmentIDGenerator
    getAppointmentIDGenerator() {

        return appointmentIDGenerator;
    }

    public HospitalizationIDGenerator
    getHospitalizationIDGenerator() {

        return hospitalizationIDGenerator;
    }

    public DoctorAvailabilityService
    getDoctorAvailabilityService() {

        return doctorAvailabilityService;
    }
}