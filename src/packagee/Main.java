/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee;

/**
 *
 * @author juand
 */
import packagee.core.NewJFrame;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import packagee.core.hospital.DataBase;


public class Main {

    public static void main(String[] args) {
        System.setProperty("flatlaf.useNativeLibrary", "false");

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        java.awt.EventQueue.invokeLater(() -> {
            DataBase store = null;
            new NewJFrame(store).setVisible(true);
        });
    }
}
