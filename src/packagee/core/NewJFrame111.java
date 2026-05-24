/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package packagee.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import packagee.core.control.AppointmentControl;
import packagee.core.control.DoctorControl;
import packagee.core.control.Hospitalizationcontrol;
import packagee.core.control.PatientControl;
import packagee.core.hospital.DataBase;
import packagee.response;

/**
 *
 * @author jjlora
 * @author edangulo
 */
public class NewJFrame111 extends javax.swing.JFrame {

    private int x, y;
    private final long loggedUserId;
    private final long doctorId;
    private final DoctorControl doctorControl;
    private final AppointmentControl appointmentControl;
    private final Hospitalizationcontrol hospitalizationControl;
    private final PatientControl patientControl;
    private final ArrayList<Object[]> prescriptionBuffer = new ArrayList<>();
    private final DataBase store;

    public NewJFrame111(long loggedUserId, long doctorId,DataBase store) {
        initComponents();
        this.loggedUserId = loggedUserId;
        this.doctorId = doctorId;
        this.store = store;
        this.doctorControl = new DoctorControl(store);
        this.appointmentControl = new AppointmentControl(store);
        this.hospitalizationControl = new Hospitalizationcontrol(store);
        this.patientControl = new PatientControl(store);
        jButton11.setVisible(loggedUserId != doctorId);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        loadDoctorInfo();
        appointmentRequest();
        AppointemntsLoadP();
        loadPatientCombo();
        loadHospitalizationCombos();
        loadAppointmentsTable(false);
    }

    private void loadDoctorInfo() {
        response response = doctorControl.getDoctorInfo(doctorId);
        if (!response.isSuccess()) return;
        Map<String, Object> data = (Map<String, Object>) response.getData();
        jTextField1.setText(String.valueOf(data.get("firstname")));
        jTextField2.setText(String.valueOf(data.get("lastname")));
        jTextField7.setText(String.valueOf(data.get("username")));
        jTextField6.setText(String.valueOf(data.get("licenceNumber")));
        jTextField8.setText(String.valueOf(data.get("assignedOffice")));
        String specialty = String.valueOf(data.get("specialty"));
        for (int i = 0; i < jComboBox1.getItemCount(); i++) {
            if (jComboBox1.getItemAt(i).toUpperCase().replaceAll(" & ", "_").replaceAll(" ", "_").equals(specialty)) {
                jComboBox1.setSelectedIndex(i);
                break;
            }
        }
    }

    private void appointmentRequest() {
        response response = appointmentControl.getDoctorAppointments(doctorId, false);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select one");
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> appointments = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> a : appointments) {
                if ("REQUESTED".equals(a.get("status"))) {
                    model.addElement((String) a.get("id"));
                }
            }
        }
        jComboBox2.setModel(model);
    }

    private void AppointemntsLoadP() {
        response response = appointmentControl.getDoctorAppointments(doctorId, true);
        DefaultComboBoxModel<String> modelReschedule = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modelComplete = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modelPrescribe = new DefaultComboBoxModel<>();
        modelReschedule.addElement("Select one");
        modelComplete.addElement("Select one");
        modelPrescribe.addElement("Select one");
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> appointments = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> a : appointments) {
                String id = (String) a.get("id");
                modelReschedule.addElement(id);
                modelComplete.addElement(id);
                modelPrescribe.addElement(id);
            }
        }
        jComboBox3.setModel(modelReschedule);
        jComboBox4.setModel(modelComplete);
        jComboBox7.setModel(modelPrescribe);
    }

    private void loadPatientCombo() {
        response response = patientControl.getAllPatients();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select one");
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> patients = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> p : patients) {
                model.addElement(String.valueOf(p.get("id")));
            }
        }
        jComboBox5.setModel(model);
    }

    private void loadHospitalizationCombos() {
        response response = hospitalizationControl.getAllHospitalizations();
        DefaultComboBoxModel<String> modelRequested = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modelCancel = new DefaultComboBoxModel<>();
        modelRequested.addElement("Select one");
        modelCancel.addElement("Select one");
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> hosps = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> h : hosps) {
                if ("REQUESTED".equals(h.get("status"))) {
                    modelRequested.addElement((String) h.get("id"));
                }
                if (!"CANCELED".equals(h.get("status"))) {
                    modelCancel.addElement((String) h.get("id"));
                }
            }
        }
        jComboBox6.setModel(modelRequested);
        jComboBox8.setModel(modelCancel);
    }

    private void loadAppointmentsTable(boolean pendingOnly) {
        response response = appointmentControl.getDoctorAppointments(doctorId, pendingOnly);
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> appointments = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> a : appointments) {
                model.addRow(new Object[]{a.get("id"), a.get("datetime"), a.get("patientName"), a.get("specialty"), a.get("type"), a.get("status")});
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new packagee.PanelRound();
        panelRound2 = new packagee.PanelRound();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton12 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton9 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox<>();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jSeparator4 = new javax.swing.JSeparator();
        jButton13 = new javax.swing.JButton();
        jComboBox8 = new javax.swing.JComboBox<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea9 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jComboBox7 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelRound1.setRadius(50);
        panelRound2.setRadius(50);
        panelRound2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) { panelRound2MouseDragged(evt); }
        });
        panelRound2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) { panelRound2MousePressed(evt); }
        });

        jButton1.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton1.setText("X");
        jButton1.setBorderPainted(false); jButton1.setContentAreaFilled(false); jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton1ActionPerformed(evt);}});

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI",0,14)); jLabel1.setText("DOCTOR VIEW");

        jButton11.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton11.setText("Back");
        jButton11.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton11ActionPerformed(evt);}});

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap().addComponent(jLabel1).addGap(32,32,32).addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(jButton1).addGap(19,19,19)));
        panelRound2Layout.setVerticalGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(jLabel1,javax.swing.GroupLayout.PREFERRED_SIZE,32,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton11)));

        jRadioButton3.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jRadioButton3.setText("Total appointments");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jRadioButton3ActionPerformed(evt);}});
        jRadioButton4.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jRadioButton4.setText("Pending appointments");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jRadioButton4ActionPerformed(evt);}});

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null},{null,null,null,null,null,null},{null,null,null,null,null,null},{null,null,null,null,null,null}},
            new String[]{"ID","Date","Patient","Specialty","Type","Status"}));
        jScrollPane3.setViewportView(jTable2);

        jButton12.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton12.setText("Logout");
        jButton12.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton12ActionPerformed(evt);}});

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton12)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup().addGap(16,16,16).addComponent(jRadioButton3).addGap(18,18,18).addComponent(jRadioButton4))
                        .addGroup(jPanel4Layout.createSequentialGroup().addGap(108,108,108).addComponent(jScrollPane3,javax.swing.GroupLayout.PREFERRED_SIZE,1035,javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152,Short.MAX_VALUE)));
        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup().addGap(29,29,29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton3).addComponent(jRadioButton4))
                .addGap(18,18,18).addComponent(jScrollPane3,javax.swing.GroupLayout.PREFERRED_SIZE,504,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,15,Short.MAX_VALUE).addComponent(jButton12).addGap(23,23,23)));
        jTabbedPane1.addTab("Appointments visualization", jPanel4);

        jComboBox5.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jLabel38.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel38.setText("Patient");
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null},{null,null,null,null,null,null},{null,null,null,null,null,null},{null,null,null,null,null,null}},
            new String[]{"ID","Date","Doctor","Specialty","Type","Status"}) {
            boolean[] canEdit = new boolean[]{false,false,false,false,false,false};
            public boolean isCellEditable(int r,int c){return canEdit[c];}
        });
        jScrollPane4.setViewportView(jTable3);
        jButton8.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton8.setText("Search");
        jButton8.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton8ActionPerformed(evt);}});

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup().addGap(37,37,37).addComponent(jLabel38).addGap(18,18,18).addComponent(jComboBox5,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup().addGap(63,63,63).addComponent(jScrollPane4,javax.swing.GroupLayout.PREFERRED_SIZE,1133,javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99,Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,jPanel5Layout.createSequentialGroup().addGap(0,0,Short.MAX_VALUE).addComponent(jButton8).addGap(601,601,601)));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup().addGap(32,32,32)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel38).addComponent(jComboBox5,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18).addComponent(jScrollPane4,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44,44,44).addComponent(jButton8).addContainerGap(67,Short.MAX_VALUE)));
        jTabbedPane1.addTab("History of a patient", jPanel5);

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel2.setText("Firstname");
        jTextField1.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel3.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel3.setText("Lastname");
        jTextField2.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel5.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel5.setText("Specialty");
        jLabel7.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel7.setText("License Number");
        jTextField6.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel8.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel8.setText("Assigned office");
        jTextField8.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel9.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel9.setText("User");
        jTextField7.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jTextField9.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel10.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel10.setText("Password");
        jLabel11.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel11.setText("Password confirmation");
        jTextField10.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jComboBox1.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one","General Medicine","Cardiology","Pediatrics","Neurology","Traumatology & Orthopedics","Gynecology & Obstetrics","Dermatology","Psychiatry","Oncology","Ophthalmology","Internal Medicine"}));
        jButton9.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton9.setText("Save");
        jButton9.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton9ActionPerformed(evt);}});

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(211,211,211).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField1,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18,18,18).addComponent(jLabel3).addGap(18,18,18).addComponent(jTextField2,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18,18,18).addComponent(jLabel5).addGap(18,18,18).addComponent(jComboBox1,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(351,351,351).addComponent(jLabel7).addGap(18,18,18).addComponent(jTextField6,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18,18,18).addComponent(jLabel8).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField8,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(558,558,558).addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTextField9,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jTextField7,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel9,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel10,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(521,521,521).addComponent(jLabel11))
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(576,576,576).addComponent(jButton9))
                    .addGroup(jPanel3Layout.createSequentialGroup().addGap(561,561,561).addComponent(jTextField10,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(269,Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup().addGap(49,49,49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(jTextField1,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3).addComponent(jTextField2,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel5).addComponent(jComboBox1,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel7).addComponent(jTextField6,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jTextField8,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel8))
                .addGap(30,30,30).addComponent(jLabel9).addGap(18,18,18).addComponent(jTextField7,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(jLabel10).addGap(27,27,27).addComponent(jTextField9,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(jLabel11).addGap(18,18,18).addComponent(jTextField10,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32,32,32).addComponent(jButton9).addContainerGap(161,Short.MAX_VALUE)));
        jTabbedPane1.addTab("Modify info", jPanel3);

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel13.setText("Accept medical appointment");
        jLabel14.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel14.setText("Appointment ID");
        jComboBox2.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jButton3.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton3.setText("Accept");
        jButton3.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton3ActionPerformed(evt);}});
        jLabel15.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel15.setText("Reschedule appointment");
        jLabel16.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel16.setText("Appointment");
        jComboBox3.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jButton4.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton4.setText("Reschedule");
        jButton4.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton4ActionPerformed(evt);}});
        jLabel17.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel17.setText("New time");
        jTextField13.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel18.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel18.setText("Reason");
        jTextField14.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jLabel19.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel19.setText("Complete appointment");
        jLabel20.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel20.setText("Appointment");
        jComboBox4.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jLabel21.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel21.setText("Diagnosis");
        jLabel22.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel22.setText("Observations");
        jLabel23.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel23.setText("Recommended treatment");
        jLabel24.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel24.setText("Follow-up");
        jButton5.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton5.setText("Complete");
        jButton5.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton5ActionPerformed(evt);}});
        jLabel25.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel25.setText("Hospitalization");
        jLabel27.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel27.setText("Reason");
        jLabel28.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel28.setText("Date");
        jTextField21.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel29.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel29.setText("Room type");
        jTextField22.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel30.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel30.setText("Observations");
        jTextArea1.setColumns(20); jTextArea1.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
        jButton6.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton6.setText("Generate");
        jButton6.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton6ActionPerformed(evt);}});
        jComboBox6.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jRadioButton5.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jRadioButton5.setText("Approve request");
        jRadioButton6.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jRadioButton6.setText("From appointment");
        jTextArea5.setColumns(20); jTextArea5.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea5.setRows(5);
        jScrollPane6.setViewportView(jTextArea5);
        jTextArea6.setColumns(20); jTextArea6.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea6.setRows(5);
        jScrollPane7.setViewportView(jTextArea6);
        jTextArea7.setColumns(20); jTextArea7.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea7.setRows(5);
        jScrollPane8.setViewportView(jTextArea7);
        jTextArea8.setColumns(20); jTextArea8.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea8.setRows(5);
        jScrollPane9.setViewportView(jTextArea8);
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jButton13.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton13.setText("Cancel");
        jButton13.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton13ActionPerformed(evt);}});
        jComboBox8.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));
        jTextArea9.setColumns(20); jTextArea9.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jTextArea9.setRows(5);
        jScrollPane10.setViewportView(jTextArea9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addGap(26,26,26).addComponent(jLabel13).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addGap(17,17,17).addComponent(jLabel13).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)));
        jTabbedPane1.addTab("Appointments", jPanel1);

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel31.setText("Appointment ID");
        jLabel32.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel32.setText("Medication name");
        jTextField24.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel33.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel33.setText("Dose");
        jTextField25.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel34.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel34.setText("Administration route");
        jTextField26.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel35.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel35.setText("Frecuency");
        jTextField27.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel36.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel36.setText("Treatment duration");
        jTextField28.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jLabel37.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jLabel37.setText("Additional instructions");
        jTextField29.setFont(new java.awt.Font("Yu Gothic UI",0,18));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null,null},{null,null,null,null,null,null,null},{null,null,null,null,null,null,null},{null,null,null,null,null,null,null}},
            new String[]{"Appointment ID","Medication","Dose","Route","Duration","Instructions","Frecuency"}) {
            boolean[] canEdit = new boolean[]{false,false,false,false,false,false,false};
            public boolean isCellEditable(int r,int c){return canEdit[c];}
        });
        jScrollPane2.setViewportView(jTable1);
        jButton7.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton7.setText("Add");
        jButton7.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton7ActionPerformed(evt);}});
        jButton10.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jButton10.setText("Prescribe");
        jButton10.addActionListener(new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent evt){jButton10ActionPerformed(evt);}});
        jComboBox7.setFont(new java.awt.Font("Yu Gothic UI",0,18)); jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Select one"}));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup().addGap(62,62,62)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2,javax.swing.GroupLayout.PREFERRED_SIZE,1125,javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel31).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jComboBox7,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(9,9,9).addComponent(jLabel32).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField24,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18,18,18).addComponent(jLabel33).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField25,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18,18,18).addComponent(jLabel34).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField26,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE).addComponent(jButton7))
                    .addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel36).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField28,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel37).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField29,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE).addComponent(jLabel35).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jTextField27,javax.swing.GroupLayout.PREFERRED_SIZE,109,javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(108,Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup().addGap(583,583,583).addComponent(jButton10).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup().addGap(57,57,57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel31).addComponent(jLabel32).addComponent(jTextField24,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel33).addComponent(jTextField25,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel34).addComponent(jTextField26,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButton7).addComponent(jComboBox7,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel36).addComponent(jTextField28,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel37).addComponent(jTextField29,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel35).addComponent(jTextField27,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30,30,30).addComponent(jScrollPane2,javax.swing.GroupLayout.PREFERRED_SIZE,340,javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47,47,47).addComponent(jButton10).addContainerGap(64,Short.MAX_VALUE)));
        jTabbedPane1.addTab("Prescribe", jPanel2);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,false).addComponent(panelRound2,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE).addComponent(jTabbedPane1))
                .addGap(0,0,Short.MAX_VALUE)));
        panelRound1Layout.setVerticalGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup().addComponent(panelRound2,javax.swing.GroupLayout.PREFERRED_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTabbedPane1)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(panelRound1,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(panelRound1,javax.swing.GroupLayout.DEFAULT_SIZE,javax.swing.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panelRound2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX(); y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void panelRound2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_panelRound2MouseDragged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        this.setVisible(false);
        new NewJFrame(store).setVisible(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        this.setVisible(false);
        new NewJFrame11(loggedUserId,store).setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        jRadioButton4.setSelected(false);
        loadAppointmentsTable(false);
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        jRadioButton3.setSelected(false);
        loadAppointmentsTable(true);
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String selected = (String) jComboBox5.getSelectedItem();
        if (selected == null || selected.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona un paciente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long pid = Long.parseLong(selected);
        response response = appointmentControl.getPatientAppointments(pid);
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);
        if (response.isSuccess()) {
            ArrayList<Map<String, Object>> appointments = (ArrayList<Map<String, Object>>) response.getData();
            for (Map<String, Object> a : appointments) {
                model.addRow(new Object[]{a.get("id"), a.get("datetime"), a.get("doctorName"), a.get("specialty"), a.get("type"), a.get("status")});
            }
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        response response = doctorControl.updateDoctor(doctorId, jTextField1.getText(), jTextField2.getText(), jTextField7.getText(), jTextField9.getText(), jTextField10.getText(), (String) jComboBox1.getSelectedItem(), jTextField6.getText(), jTextField8.getText());
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String appointmentId = (String) jComboBox2.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        response response = appointmentControl.acceptAppointment(appointmentId, doctorId);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            appointmentRequest();
            AppointemntsLoadP();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String appointmentId = (String) jComboBox3.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        response response = appointmentControl.rescheduleAppointment(appointmentId, doctorId, jTextField13.getText(), jTextField14.getText());
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            jTextField13.setText(""); jTextField14.setText("");
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String appointmentId = (String) jComboBox4.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        response response = appointmentControl.completeAppointment(appointmentId, doctorId, jTextArea5.getText(), jTextArea6.getText(), jTextArea7.getText(), jTextArea8.getText());
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            jTextArea5.setText(""); jTextArea6.setText(""); jTextArea7.setText(""); jTextArea8.setText("");
            AppointemntsLoadP();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (jRadioButton5.isSelected()) {
            String hospId = (String) jComboBox6.getSelectedItem();
            if (hospId == null || hospId.equals("Select one")) {
                JOptionPane.showMessageDialog(this, "Selecciona una hospitalización.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            response response = hospitalizationControl.approveHospitalization(hospId, doctorId);
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                loadHospitalizationCombos();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (jRadioButton6.isSelected()) {
            String selected = (String) jComboBox4.getSelectedItem();
            if (selected == null || selected.equals("Select one")) {
                JOptionPane.showMessageDialog(this, "Selecciona una cita.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            response response = hospitalizationControl.hospitalizeFromAppointment(selected, doctorId, jTextField21.getText(), jTextArea9.getText(), jTextField22.getText(), jTextArea1.getText());
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                jTextField21.setText(""); jTextField22.setText(""); jTextArea9.setText(""); jTextArea1.setText("");
                AppointemntsLoadP();
                loadHospitalizationCombos();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una opción.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String hospId = (String) jComboBox8.getSelectedItem();
        if (hospId == null || hospId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona una hospitalización.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        response response = hospitalizationControl.cancelHospitalization(hospId, doctorId);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadHospitalizationCombos();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String appointmentId = (String) jComboBox7.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String medicationName = jTextField24.getText();
            double dose = Double.parseDouble(jTextField25.getText());
            String adminRoute = jTextField26.getText();
            int duration = Integer.parseInt(jTextField28.getText());
            String additionalInfo = jTextField29.getText();
            int frecuency = Integer.parseInt(jTextField27.getText());
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.addRow(new Object[]{appointmentId, medicationName, dose, adminRoute, duration, additionalInfo, frecuency});
            prescriptionBuffer.add(new Object[]{appointmentId, medicationName, dose, adminRoute, duration, additionalInfo, frecuency});
            jTextField24.setText(""); jTextField25.setText(""); jTextField26.setText("");
            jTextField27.setText(""); jTextField28.setText(""); jTextField29.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dosis, frecuencia y duración deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if (prescriptionBuffer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay medicamentos en la lista.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean allOk = true;
        for (Object[] row : prescriptionBuffer) {
            response response = appointmentControl.prescribeMedication((String) row[0], doctorId, (String) row[1], (double) row[2], (String) row[3], (int) row[4], (String) row[5], (int) row[6]);
            if (!response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                allOk = false;
            }
        }
        if (allOk) {
            JOptionPane.showMessageDialog(this, "Medicamentos prescritos exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            prescriptionBuffer.clear();
            ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private packagee.PanelRound panelRound1;
    private packagee.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables
}
