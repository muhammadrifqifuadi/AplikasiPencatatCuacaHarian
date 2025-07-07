/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.weatherloggerapp.ui;

import com.mycompany.weatherloggerapp.AuditLogger;
import com.mycompany.weatherloggerapp.DatabaseManager;
import com.mycompany.weatherloggerapp.SecurityUtils;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author MSI GF63
 */
public class LoginForm extends javax.swing.JFrame {

    private ResourceBundle bundle;

    /**
     * Creates new form LoginForm
     */
    public LoginForm() {
        initComponents();
        applyLanguage();

    }

    private void applyLanguage() {
        String language;
        String country;
        Locale locale;
        int lang = languageComboBox.getSelectedIndex();
        try {
            switch (lang) {
                case 0:
                    language = "en";
                    country = "US";
                    break;
                case 1:
                    language = "in";
                    country = "ID";
                    break;
                default:
                    language = "en";
                    country = "US";
                    break;
            }

            locale = new Locale(language, country);
            ResourceBundle rb = ResourceBundle.getBundle("com.mycompany.weatherloggerapp.localization.Bundle", locale);

            languageLabel.setText(rb.getString("Language"));
            jLabel1.setText(rb.getString("judul"));
            usernameLabel.setText(rb.getString("username"));
            passwordLabel.setText(rb.getString("password"));
            goToRegisterButton.setText(rb.getString("Regist"));
            loginButton.setText(rb.getString("login"));

            int cmbLN = languageComboBox.getItemCount();
            languageComboBox.removeAllItems();
            for (int i = 0; i < cmbLN; i++) {
                languageComboBox.addItem(rb.getString("languageComboBox." + i));
            }
            languageComboBox.setSelectedIndex(lang);
            setTitle(rb.getString("Title"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        languageLabel = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox<>();
        loginButton = new javax.swing.JButton();
        goToRegisterButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Weather Logger");

        usernameLabel.setText("Username");

        passwordLabel.setText("Password");

        languageLabel.setText("Language");

        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "English", "Indonesia" }));
        languageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageComboBoxActionPerformed(evt);
            }
        });

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        goToRegisterButton.setText("Create New Account");
        goToRegisterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToRegisterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(languageLabel)
                                .addGap(13, 13, 13)
                                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(goToRegisterButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(loginButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(passwordLabel)
                                            .addComponent(usernameLabel))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                            .addComponent(passwordField))))
                                .addGap(29, 29, 29)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(121, 121, 121))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(languageLabel)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usernameLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(goToRegisterButton))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // 1. Ambil input dari form
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validasi dasar agar tidak kosong
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pengguna dan kata sandi harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Hash password yang diinput untuk dicocokkan dengan yang ada di DB
        String hashedPassword = SecurityUtils.hashPassword(password);

        // 3. Validasi kredensial ke database
        if (DatabaseManager.validateUser(username, hashedPassword)) {
            // ---- JIKA SUKSES ----

            // Log aksi login ke MongoDB
            AuditLogger.logAction(username, "LOGIN_SUCCESS", "User logged in successfully.");

            // 4. Buka Main Frame dan kirim username
            MainFrame mainFrame = new MainFrame(username, this.bundle);
            mainFrame.setVisible(true);

            // 5. Tutup form login
            this.dispose();

        } else {
            // ---- JIKA GAGAL ----

            // Log aksi login yang gagal
            AuditLogger.logAction(username, "LOGIN_FAILED", "Invalid credentials provided.");

            // Tampilkan pesan error
            JOptionPane.showMessageDialog(this, "Nama pengguna atau kata sandi salah.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void languageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageComboBoxActionPerformed
        applyLanguage();
    }//GEN-LAST:event_languageComboBoxActionPerformed

    private void goToRegisterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToRegisterButtonActionPerformed
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setVisible(true);
    }//GEN-LAST:event_goToRegisterButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goToRegisterButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox<String> languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
