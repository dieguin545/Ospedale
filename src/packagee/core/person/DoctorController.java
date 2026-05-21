/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.person;

/**
 *
 * @author juand
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.DataBase;
import packagee.Response;
import packagee.Specialty;

public class DoctorController {

    private final DataBase store = DataBase.getInstance();

    public Response registrarDoctor(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        Response validation = validateDoctorFields(idStr, username, password,
                confirmPassword, licenceNumber, assignedOffice, specialtyStr);
        if (!validation.isSuccess()) return validation;

        long id = Long.parseLong(idStr);
        Specialty specialty = Specialty.valueOf(
                specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());

        Doctor doctor = new Doctor(id, username, firstname, lastname, password,
                specialty, licenceNumber, assignedOffice);
        store.addUser(doctor);

        return new Response(Response.SUCCESS, "Doctor registrado exitosamente.");
    }

    public Response updateDoctor(long doctorId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        User found = store.findUserById(doctorId);
        if (found == null || !(found instanceof Doctor)) {
            return new Response(Response.NOT_FOUND, "Doctor no encontrado.");
        }

        User byUsername = store.findUserByUsername(username);
        if (byUsername != null && byUsername.getId() != doctorId) {
            return new Response(Response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        if (!password.equals(confirmPassword)) {
            return new Response(Response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }
        if (!licenceNumber.matches("L-\\d{10} MTL")) {
            return new Response(Response.BAD_REQUEST, "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }
        if (!assignedOffice.matches("O-\\d{3}")) {
            return new Response(Response.BAD_REQUEST, "Oficina inválida. Formato: O-XXX");
        }

        Specialty specialty;
        try {
            specialty = Specialty.valueOf(
                    specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return new Response(Response.BAD_REQUEST, "Especialidad inválida.");
        }

        Doctor doctor = (Doctor) found;
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setUsername(username);
        doctor.setPassword(password);
        doctor.setSpecialty(specialty);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);

        return new Response(Response.SUCCESS, "Información del doctor actualizada.");
    }

    public Response getAllDoctors() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Doctor d : store.getDoctors()) {
            list.add(serializeDoctor(d));
        }
        return new Response(Response.SUCCESS, "OK", list);
    }

    public Response getDoctorInfo(long doctorId) {
        User found = store.findUserById(doctorId);
        if (found == null || !(found instanceof Doctor)) {
            return new Response(Response.NOT_FOUND, "Doctor no encontrado.");
        }
        return new Response(Response.SUCCESS, "OK", serializeDoctor((Doctor) found));
    }


    private Response validateDoctorFields(String idStr, String username, String password,
            String confirmPassword, String licenceNumber, String assignedOffice, String specialtyStr) {

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
        if (!licenceNumber.matches("L-\\d{10} MTL")) {
            return new Response(Response.BAD_REQUEST, "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }
        if (!assignedOffice.matches("O-\\d{3}")) {
            return new Response(Response.BAD_REQUEST, "Oficina inválida. Formato: O-XXX");
        }
        try {
            Specialty.valueOf(specialtyStr.replaceAll(" & ", "_").replaceAll(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return new Response(Response.BAD_REQUEST, "Especialidad inválida.");
        }
        return new Response(Response.SUCCESS, "OK");
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
