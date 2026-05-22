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


public class DataBase {

    private static DataBase instance;

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    private ArrayList<User> users;
    private ArrayList<Appointment> appointments;
    private ArrayList<Hospitalization> hospitalizations;

    private java.util.HashMap<String, Integer> appointmentCounters;
    private java.util.HashMap<String, Integer> hospitalizationCounters;

    private DataBase() {
        this.users = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
        this.appointmentCounters = new java.util.HashMap<>();
        this.hospitalizationCounters = new java.util.HashMap<>();

        cargarJSON("C:\\Users\\juand\\OneDrive\\Desktop\\Ospedale\\json\\users.json");
    }

    private void cargarJSON(String path) {
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
            System.err.println("No se pudo cargar users.json: " + e.getMessage());
        }
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public User findID(long id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public boolean userExists(String username) {
        return findUser(username) != null;
    }

    public boolean idExists(long id) {
        return findID(id) != null;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public Appointment findIDapp(String id) {
        for (Appointment a : appointments) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public String generateAppID(long patientId) {
        String key = String.valueOf(patientId);
        int counter = appointmentCounters.getOrDefault(key, 0);
        String id = String.format("A-%s-%04d", key, counter);
        appointmentCounters.put(key, counter + 1);
        return id;
    }

    public ArrayList<Hospitalization> getHospitalizations() {
        return hospitalizations;
    }

    public void addHospitalization(Hospitalization hospitalization) {
        this.hospitalizations.add(hospitalization);
    }

    public Hospitalization findHospitalizationID(String id) {
        for (Hospitalization h : hospitalizations) {
            if (h.getId().equals(id)) {
                return h;
            }
        }
        return null;
    }

    
    public String genHospitalizationID(long patientId) {
        String key = String.valueOf(patientId);
        int counter = hospitalizationCounters.getOrDefault(key, 0);
        String id = String.format("H-%s-%04d", key, counter);
        hospitalizationCounters.put(key, counter + 1);
        return id;
    }

    public ArrayList<Doctor> getDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Doctor) {
                doctors.add((Doctor) u);
            }
        }
        return doctors;
    }

    public ArrayList<Doctor> getdoctorsSpeciality(Specialty specialty, java.time.LocalDateTime datetime) {
        ArrayList<Doctor> available = new ArrayList<>();
        for (Doctor d : getDoctors()) {
            if (d.getSpecialty() == specialty && doctorAvailable(d, datetime)) {
                available.add(d);
            }
        }
        return available;
    }

    public boolean doctorAvailable(Doctor doctor, java.time.LocalDateTime datetime) {
        java.time.LocalDateTime end = datetime.plusMinutes(15);
        for (Appointment a : appointments) {
            if (a.getDoctor().getId() == doctor.getId()
                    && a.getStatus() != AppointmentStatus.CANCELED
                    && a.getStatus() != AppointmentStatus.COMPLETED) {
                java.time.LocalDateTime aStart = a.getDatetime();
                java.time.LocalDateTime aEnd = aStart.plusMinutes(15);
                if (datetime.isBefore(aEnd) && end.isAfter(aStart)) {
                    return false;
                }
            }
        }
        return true;
    }
}
