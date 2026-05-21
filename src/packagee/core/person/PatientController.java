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
import packagee.DataBase;
import packagee.Response;

public class PatientController {

    private final DataBase store = DataBase.getInstance();

    public Response registrarPaciente(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        Response validation = validatePatientFields(idStr, username, password, confirmPassword,
                email, birthdateStr, phoneStr);
        if (!validation.isSuccess()) return validation;

        long id = Long.parseLong(idStr);
        long phone = Long.parseLong(phoneStr);
        LocalDate birthdate = LocalDate.parse(birthdateStr);
        boolean gender = genderStr.equalsIgnoreCase("Male");

        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birthdate, gender, phone, address);
        store.addUser(patient);

        return new Response(Response.SUCCESS, "Paciente registrado exitosamente.");
    }

    
    public Response updatePatient(long patientId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        User found = store.findUserById(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new Response(Response.NOT_FOUND, "Paciente no encontrado.");
        }

        User byUsername = store.findUserByUsername(username);
        if (byUsername != null && byUsername.getId() != patientId) {
            return new Response(Response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        Response validation = validatePatientFields(String.valueOf(patientId), username,
                password, confirmPassword, email, birthdateStr, phoneStr);
        if (validation.getStatusCode() == Response.CONFLICT) {
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

        return new Response(Response.SUCCESS, "Información del paciente actualizada.");
    }

    public Response getPatientInfo(long patientId) {
        User found = store.findUserById(patientId);
        if (found == null || !(found instanceof Patient)) {
            return new Response(Response.NOT_FOUND, "Paciente no encontrado.");
        }
        Patient p = (Patient) found;
        Map<String, Object> data = serializePatient(p);
        return new Response(Response.SUCCESS, "OK", data);
    }

    
    public Response getAllPatients() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (User u : store.getUsers()) {
            if (u instanceof Patient) {
                list.add(serializePatient((Patient) u));
            }
        }
        return new Response(Response.SUCCESS, "OK", list);
    }


    private Response validatePatientFields(String idStr, String username, String password,
            String confirmPassword, String email, String birthdateStr, String phoneStr) {

        if (!idStr.matches("\\d{12}") || Long.parseLong(idStr) <= 0) {
            return new Response(Response.BAD_REQUEST, "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
        }
        if (store.idExists(Long.parseLong(idStr))) {
            return new Response(Response.CONFLICT, "El ID ya está en uso.");
        }

        if (store.usernameExists(username)) {
            return new Response(Response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        if (!password.equals(confirmPassword)) {
            return new Response(Response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }

        if (!phoneStr.matches("\\d{10}")) {
            return new Response(Response.BAD_REQUEST, "El teléfono debe tener exactamente 10 dígitos.");
        }

        if (!email.matches("^[^@]+@[^@]+\\.com$")) {
            return new Response(Response.BAD_REQUEST, "El email debe seguir el formato usuario@dominio.com");
        }

        try {
            LocalDate.parse(birthdateStr);
        } catch (DateTimeParseException e) {
            return new Response(Response.BAD_REQUEST, "La fecha de nacimiento debe seguir el formato AAAA-MM-DD.");
        }

        return new Response(Response.SUCCESS, "OK");
    }

    private Map<String, Object> serializePatient(Patient p) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", p.getId());
        data.put("username", p.getUsername());
        data.put("firstname", p.getFirstname());
        data.put("lastname", p.getLastname());
        return data;
    }
}
