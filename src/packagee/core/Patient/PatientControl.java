/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Patient;

/**
 *
 * @author juand
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.core.control.validacionesformato;
import packagee.core.hospital.DataBase;
import packagee.core.person.User;
import packagee.response;

public class PatientControl implements patientControlint {

    private final DataBase store;
    private final PatientFormatter formatter;
    private final PatientService service;

    public PatientControl(DataBase store) {
        this.store = store;
        this.formatter = new PatientFormatter();
        this.service = new PatientService(store);
    }

    @Override
    public response registrarPaciente(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        response validation = validatePatientFields(idStr, username, password, 
                confirmPassword, email, birthdateStr, phoneStr);
        if (!validation.isSuccess()) {
            return validation;
        }

        long id = Long.parseLong(idStr);
        long phone = Long.parseLong(phoneStr);
        LocalDate birthdate = LocalDate.parse(birthdateStr);
        boolean gender = genderStr.equalsIgnoreCase("Male");

        Patient patient = service.createPatient(id, username, firstname, lastname, 
                                               password, email, birthdate, gender, 
                                               phone, address);
        service.savePatient(patient);

        return new response(response.SUCCESS, "Paciente registrado exitosamente.");
    }

    @Override
    public response PatientUpdate(long patientId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String email, String birthdateStr, String genderStr,
            String phoneStr, String address) {

        Patient patient = getPatientOrNull(patientId);
        if (patient == null) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }

        User byUsername = store.findUser(username);
        if (byUsername != null && byUsername.getId() != patientId) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        response validation = validatePatientFields(String.valueOf(patientId), username,
                password, confirmPassword, email, birthdateStr, phoneStr);
        
        if (validation.getStatusCode() != response.SUCCESS && 
            validation.getStatusCode() != response.CONFLICT) {
            return validation;
        }

        if (!password.equals(confirmPassword)) {
            return new response(response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }

        LocalDate birthdate = LocalDate.parse(birthdateStr);
        boolean gender = genderStr.equalsIgnoreCase("Male");
        long phone = Long.parseLong(phoneStr);

        service.updatePatient(patient, firstname, lastname, username, password, 
                            email, birthdate, gender, phone, address);

        return new response(response.SUCCESS, "Información del paciente actualizada.");
    }

    @Override
    public response getPatient(long patientId) {
        Patient patient = getPatientOrNull(patientId);
        if (patient == null) {
            return new response(response.NOT_FOUND, "Paciente no encontrado.");
        }
        return new response(response.SUCCESS, "OK", formatter.toMap(patient));
    }

    @Override
    public response getAllPatients() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (User u : store.getUsers()) {
            if (u instanceof Patient) {
                list.add(formatter.toMap((Patient) u));
            }
        }
        return new response(response.SUCCESS, "OK", list);
    }


    private response validatePatientFields(String idStr, String username, 
            String password, String confirmPassword, String email, 
            String birthdateStr, String phoneStr) {

        if (!validacionesformato.isValidId(idStr)) {
            return new response(response.BAD_REQUEST, 
                "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
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

        if (!validacionesformato.isValidPhone(phoneStr)) {
            return new response(response.BAD_REQUEST, 
                "El teléfono debe tener exactamente 10 dígitos.");
        }

        if (!validacionesformato.isValidEmail(email)) {
            return new response(response.BAD_REQUEST, 
                "El email debe seguir el formato usuario@dominio.com");
        }

        if (!validacionesformato.isValidDate(birthdateStr)) {
            return new response(response.BAD_REQUEST, 
                "La fecha de nacimiento debe seguir el formato AAAA-MM-DD.");
        }

        return new response(response.SUCCESS, "OK");
    }


    private Patient getPatientOrNull(long patientId) {
        User found = store.findID(patientId);
        if (found instanceof Patient) {
            return (Patient) found;
        }
        return null;
    }

}