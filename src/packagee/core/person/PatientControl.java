/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.person;

/**
 *
 * @author juand
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.core.hospital.DataBase;
import packagee.response;

public class PatientControl {

    private final DataBase store = DataBase.getInstance();

    public response registrarPaciente(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        response validation = PatientValidation(idStr, username, password, confirmPassword,
                email, birthdateStr, phoneStr);
        if (!validation.isSuccess()) return validation;

        long id = Long.parseLong(idStr);
        long phone = Long.parseLong(phoneStr);
        LocalDate birthdate = LocalDate.parse(birthdateStr);
        boolean gender = genderStr.equalsIgnoreCase("Male");

        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birthdate, gender, phone, address);
        store.addUser(patient);

        return new response(response.SUCCESS, "Paciente registrado exitosamente.");
    }

    
    public response PatientUpdate(long patientId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        User found = store.findID(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }

        User byUsername = store.findUser(username);
        if (byUsername != null && byUsername.getId() != patientId) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        response validation = PatientValidation(String.valueOf(patientId), username,
                password, confirmPassword, email, birthdateStr, phoneStr);
        if (validation.getStatusCode() == response.CONFLICT) {
        } else if (!validation.isSuccess()) {
            return validation;
        }

        Patient patient = (Patient) found;
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setUsername(username);
        patient.setPassword(password);
        patient.setEmail(email);
        patient.setBirthdate(LocalDate.parse(birthdateStr));
        patient.setGender(genderStr.equalsIgnoreCase("Male"));
        patient.setPhone(Long.parseLong(phoneStr));
        patient.setAddress(address);

        return new response(response.SUCCESS, "Información del paciente actualizada.");
    }

    public response getPatient(long patientId) {
        User found = store.findID(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }
        Patient p = (Patient) found;
        Map<String, Object> data = Patientserialize(p);
        return new response(response.SUCCESS, "OK", data);
    }

    
    public response getAllPatients() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (User u : store.getUsers()) {
            if (u instanceof Patient) {
                list.add(Patientserialize((Patient) u));
            }
        }
        return new response(response.SUCCESS, "OK", list);
    }


    private response PatientValidation(String idStr, String username, String password,
            String confirmPassword, String email, String birthdateStr, String phoneStr) {

        if (!idStr.matches("\\d{12}") || Long.parseLong(idStr) <= 0) {
            return new response(response.BAD_REQUEST, "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
        }
        if (store.idExists(Long.parseLong(idStr))) {
            return new response(response.CONFLICT, "El ID ya está en uso.");
        }

        if (store.userExists(username)) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        if (!password.equals(confirmPassword)) {
            return new response(response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }

        if (!phoneStr.matches("\\d{10}")) {
            return new response(response.BAD_REQUEST, "El teléfono debe tener exactamente 10 dígitos.");
        }

        if (!email.matches("^[^@]+@[^@]+\\.com$")) {
            return new response(response.BAD_REQUEST, "El email debe seguir el formato usuario@dominio.com");
        }

        try {
            LocalDate.parse(birthdateStr);
        } catch (DateTimeParseException e) {
            return new response(response.BAD_REQUEST, "La fecha de nacimiento debe seguir el formato AAAA-MM-DD.");
        }

        return new response(response.SUCCESS, "OK");
    }

        private Map<String, Object> Patientserialize(Patient p) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", p.getId());
        data.put("username", p.getUsername());
        data.put("firstname", p.getFirstname());
        data.put("lastname", p.getLastname());
        return data;
    }
}
