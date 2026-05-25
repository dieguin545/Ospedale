/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.core.control;

/**
 *
 * @author juand
 */

import packagee.core.control.validaciones;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class validacionesformato{
    
    public static boolean isValidTime(String time) {
        if (time == null || time.isBlank()) {
            return false;
        }
        if (!time.matches(validaciones.TIME_PATTERN)) {
            return false;
        }
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3));
        return (hour >= 0 && hour <= 23) && (minute == 0 || minute == 15 || minute == 30 || minute == 45);
    }
    
    public static boolean isValidDate(String date) {
        if (date == null || date.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean isValidLicence(String licence) {
        return licence != null && licence.matches(validaciones.LICENCE_PATTERN);
    }
    
    public static boolean isValidOffice(String office) {
        return office != null && office.matches(validaciones.OFFICE_PATTERN);
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(validaciones.EMAIL_PATTERN);
    }
    
    public static boolean isValidId(String id) {
        if (id == null || !id.matches(validaciones.ID_PATTERN)) {
            return false;
        }
        return Long.parseLong(id) > 0;
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(validaciones.PHONE_PATTERN);
    }
    
}
