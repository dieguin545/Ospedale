/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */
import packagee.core.person.User;
import packagee.core.person.Doctor;
import packagee.core.person.Patient;
import java.util.HashMap;
import java.util.Map;
import packagee.core.person.Administrator;
import packagee.core.hospital.DataBase;
import packagee.response;

public class Authcontrol implements AuthControlint {

    private final DataBase store = DataBase.getInstance();

    @Override
    public response login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new response(response.BAD_REQUEST, "Usuario y contraseña son obligatorios.");
        }

        User user = store.findUser(username);

        if (user == null || !user.getPassword().equals(password)) {
            return new response(response.UNAUTHORIZED, "Usuario o contraseña incorrectos.");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("firstname", user.getFirstname());
        data.put("lastname", user.getLastname());

        if (user instanceof Administrator) {
            data.put("userType", "ADMIN");
        } else if (user instanceof Doctor) {
            data.put("userType", "DOCTOR");
        } else if (user instanceof Patient) {
            data.put("userType", "PATIENT");
        }

        return new response(response.SUCCESS, "Login exitoso.", data);
    }
}
