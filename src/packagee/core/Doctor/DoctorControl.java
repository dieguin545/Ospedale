/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.Doctor;

/**
 *
 * @author juand
 */
import packagee.core.Doctor.DoctorFormatter;
import packagee.core.Doctor.DoctorService;
import packagee.core.Doctor.doctorControlint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import packagee.core.hospital.DataBase;
import packagee.response;
import packagee.Specialty;
import packagee.core.Doctor.Doctor;
import packagee.core.control.validacionesformato;
import packagee.core.person.User;

public class DoctorControl implements doctorControlint {

    private final DataBase store;
    private final DoctorFormatter formatter;
    private final DoctorService service;

    public DoctorControl(DataBase store) {
        this.store = store;
        this.formatter = new DoctorFormatter();
        this.service = new DoctorService(store);
    }

    @Override
    public response registrarDoctor(String firstname, String lastname, String idStr,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        response validation = validateDoctorFields(idStr, username, password,
                confirmPassword, licenceNumber, assignedOffice, specialtyStr);
        if (!validation.isSuccess()) {
            return validation;
        }

        long id = Long.parseLong(idStr);
        Specialty specialty = service.parseSpecialty(specialtyStr);
        
        if (specialty == null) {
            return new response(response.BAD_REQUEST, "Especialidad inválida.");
        }

        Doctor doctor = service.createDoctor(id, username, firstname, lastname, 
                                            password, specialty, licenceNumber, 
                                            assignedOffice);
        service.saveDoctor(doctor);

        return new response(response.SUCCESS, "Doctor registrado exitosamente.");
    }

    @Override
    public response updateDoctor(long doctorId, String firstname, String lastname,
            String username, String password, String confirmPassword,
            String specialtyStr, String licenceNumber, String assignedOffice) {

        Doctor doctor = getDoctorOrNull(doctorId);
        if (doctor == null) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }

        User byUsername = store.findUser(username);
        if (byUsername != null && byUsername.getId() != doctorId) {
            return new response(response.CONFLICT, "El nombre de usuario ya está en uso.");
        }

        if (!validacionesformato.isValidLicence(licenceNumber)) {
            return new response(response.BAD_REQUEST, 
                "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }

        if (!validacionesformato.isValidOffice(assignedOffice)) {
            return new response(response.BAD_REQUEST, 
                "Oficina inválida. Formato: O-XXX");
        }

        if (!password.equals(confirmPassword)) {
            return new response(response.BAD_REQUEST, "Las contraseñas no coinciden.");
        }

        Specialty specialty = service.parseSpecialty(specialtyStr);
        if (specialty == null) {
            return new response(response.BAD_REQUEST, "Especialidad inválida.");
        }

        // ========== ACTUALIZAR ==========
        service.updateDoctor(doctor, firstname, lastname, username, password, 
                           specialty, licenceNumber, assignedOffice);

        return new response(response.SUCCESS, "Información del doctor actualizada.");
    }

    @Override
    public response getAllDoctors() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (Doctor d : store.getDoctors()) {
            list.add(formatter.toMap(d));
        }
        return new response(response.SUCCESS, "OK", list);
    }

    @Override
    public response getDoctorInfo(long doctorId) {
        Doctor doctor = getDoctorOrNull(doctorId);
        if (doctor == null) {
            return new response(response.NOT_FOUND, "Doctor no encontrado.");
        }
        return new response(response.SUCCESS, "OK", formatter.toMap(doctor));
    }


    private response validateDoctorFields(String idStr, String username, String password,
            String confirmPassword, String licenceNumber, String assignedOffice, 
            String specialtyStr) {

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

        if (!validacionesformato.isValidLicence(licenceNumber)) {
            return new response(response.BAD_REQUEST, 
                "Número de licencia inválido. Formato: L-XXXXXXXXXX MTL");
        }

        if (!validacionesformato.isValidOffice(assignedOffice)) {
            return new response(response.BAD_REQUEST, 
                "Oficina inválida. Formato: O-XXX");
        }

        if (service.parseSpecialty(specialtyStr) == null) {
            return new response(response.BAD_REQUEST, "Especialidad inválida.");
        }

        return new response(response.SUCCESS, "OK");
    }

    // ========== MÉTODO HELPER ==========

    private Doctor getDoctorOrNull(long doctorId) {
        User found = store.findID(doctorId);
        if (found instanceof Doctor) {
            return (Doctor) found;
        }
        return null;
    }

}
