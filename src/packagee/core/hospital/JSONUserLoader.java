package packagee.core.hospital;

import packagee.core.person.Administrator;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;
import packagee.core.person.User;
import packagee.Specialty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONUserLoader {

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
