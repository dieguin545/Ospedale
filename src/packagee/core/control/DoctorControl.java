/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.core.hospital.DataBase;
import packagee.response;
import packagee.Specialty;
import packagee.core.person.Doctor;
import packagee.core.person.User;

public class DoctorControl {

    private final DataBase store = DataBase.getInstance();

    public response registrarDoctor(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        response validation = validateDoctorFields(idStr, username, password,
                confirmPassword, licenceNumber, assignedOffice, specialtyStr);
        if (!validation.isSuccess()) return validation;

        long id = Long.parseLong(idStr);
        Specialty specialty = Specialty.valueOf(
                specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());

        Doctor doctor = new Doctor(id, username, firstname, lastname, password,
                specialty, licenceNumber, assignedOffice);
        store.addUser(doctor);

        return new response(response.SUCCESS, "Doctor registrado exitosamente.");
    }

    public response updateDoctor(long doctorId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        User found = store.findUserById(doctorId);
        if (found == null || !(found instanceof Doctor)) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        User byUsername = store.findUserByUsername(username);
        if (byUsername != null && byUsername.getId() != doctorId) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        if (!password.equals(confirmPassword)) {
            return new response(response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }
        if (!licenceNumber.matches("L-\\d{10} MTL")) {
            return new response(response.BAD_REQUEST, "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }
        if (!assignedOffice.matches("O-\\d{3}")) {
            return new response(response.BAD_REQUEST, "Oficina inválida. Formato: O-XXX");
        }

        Specialty specialty;
        try {
            specialty = Specialty.valueOf(
                    specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return new response(response.BAD_REQUEST, "Especialidad inválida.");
        }

        Doctor doctor = (Doctor) found;
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setUsername(username);
        doctor.setPassword(password);
        doctor.setSpecialty(specialty);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);

        return new response(response.SUCCESS, "Información del doctor actualizada.");
    }

    public response getAllDoctors() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Doctor d : store.getDoctors()) {
            list.add(serializeDoctor(d));
        }
        return new response(response.SUCCESS, "OK", list);
    }

    public response getDoctorInfo(long doctorId) {
        User found = store.findUserById(doctorId);
        if (found == null || !(found instanceof Doctor)) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }
        return new response(response.SUCCESS, "OK", serializeDoctor((Doctor) found));
    }


    private response validateDoctorFields(String idStr, String username, String password,
            String confirmPassword, String licenceNumber, String assignedOffice, String specialtyStr) {

        if (!idStr.matches("\\d{12}") || Long.parseLong(idStr) <= 0) {
            return new response(response.BAD_REQUEST, "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
        }
        if (store.idExists(Long.parseLong(idStr))) {
            return new response(response.CONFLICT, "El ID ya está en uso.");
        }
        if (store.usernameExists(username)) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }
        if (!password.equals(confirmPassword)) {
            return new response(response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }
        if (!licenceNumber.matches("L-\\d{10} MTL")) {
            return new response(response.BAD_REQUEST, "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }
        if (!assignedOffice.matches("O-\\d{3}")) {
            return new response(response.BAD_REQUEST, "Oficina inválida. Formato: O-XXX");
        }
        try {
            Specialty.valueOf(specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return new response(response.BAD_REQUEST, "Especialidad inválida.");
        }
        return new response(response.SUCCESS, "OK");
    }

    private Map<String, Object> serializeDoctor(Doctor d) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", d.getId());
        data.put("username", d.getUsername());
        data.put("firstname", d.getFirstname());
        data.put("lastname", d.getLastname());
        data.put("specialty", d.getSpecialty().name());
        data.put("licenceNumber", d.getLicenceNumber());
        data.put("assignedOffice", d.getAssignedOffice());
        return data;
    }
}
