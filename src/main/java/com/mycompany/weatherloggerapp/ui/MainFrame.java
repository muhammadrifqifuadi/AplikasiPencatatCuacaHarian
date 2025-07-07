/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.weatherloggerapp.ui;

import com.mycompany.weatherloggerapp.AuditLogger;
import com.mycompany.weatherloggerapp.CryptoUtils;
import com.mycompany.weatherloggerapp.DatabaseManager;
import com.mycompany.weatherloggerapp.WeatherData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author MSI GF63
 */
public class MainFrame extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private String loggedInUsername;
    private ResourceBundle bundle;

    /**
     * Creates new form MainFrame
     *
     * @param username
     */
    public MainFrame(String username, ResourceBundle bundle) {
        // 1. Inisialisasi variabel-variabel penting
        this.loggedInUsername = username; // Gunakan parameter yang diterima
        this.dbManager = new DatabaseManager();
        this.bundle = bundle;

        initComponents();

        setupTable();
        loadDataWithMultiThreading();

    }


    // Metode setupTable, loadData, dll tetap sama...
    private void setupTable() {
        
        welcomeLabel.setText("Selamat Datang, " + loggedInUsername + "!");
        
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Kota", "Suhu", "Kelembapan", "Kondisi", "Tanggal"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        weatherTable.setModel(tableModel);

        weatherTable.setAutoCreateRowSorter(true);

        weatherTable.getColumnModel().getColumn(0).setMinWidth(0);
        weatherTable.getColumnModel().getColumn(0).setMaxWidth(0);
        weatherTable.getColumnModel().getColumn(0).setWidth(0);
    }

    private void loadDataWithMultiThreading() {
        SwingWorker<List<WeatherData>, Void> worker = new SwingWorker<List<WeatherData>, Void>() {
            @Override
            protected List<WeatherData> doInBackground() throws Exception {
                return dbManager.getAllWeatherData();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<WeatherData> results = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    for (WeatherData data : results) {
                        tableModel.addRow(new Object[]{
                            data.getId(),
                            data.getCity(),
                            data.getTemperature(),
                            data.getHumidity(),
                            data.getKondisi(),
                            sdf.format(data.getRecordDate())
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Gagal memuat data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void tableRowClicked() {
        int selectedRow = weatherTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            cityField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            tempField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            humidityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            kondisiComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));

            try {
                List<WeatherData> allData = dbManager.getAllWeatherData();
                for (WeatherData data : allData) {
                    if (data.getId() == id) {
                        notesField.setText(CryptoUtils.decrypt(data.getNotesEncrypted()));
                        break;
                    }
                }
            } catch (Exception e) {
                notesField.setText("");
                System.err.println("Gagal dekripsi: " + e.getMessage());
            }
        }
    }

    private void addData() {
        // 1. Validasi input agar tidak kosong atau salah format
        if (cityField.getText().isEmpty() || tempField.getText().isEmpty() || humidityField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kota, Suhu, dan Kelembapan harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 2. Ambil data dari form
            String city = cityField.getText();
            double temp = Double.parseDouble(tempField.getText());
            int humidity = Integer.parseInt(humidityField.getText());
            String kondisi = (String) kondisiComboBox.getSelectedItem();
            String notes = notesField.getText();

            // 3. Enkripsi catatan rahasia
            String encryptedNotes = CryptoUtils.encrypt(notes);

            // 4. Buat objek data baru
            WeatherData newData = new WeatherData(city, temp, humidity, kondisi, encryptedNotes);

            // 5. Tambahkan ke database
            if (dbManager.addWeatherData(newData)) {
                // -- JIKA SUKSES --
                AuditLogger.logAction(loggedInUsername, "CREATE_DATA", "Added weather data for " + city);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");

                // 6. Muat ulang data di tabel dan kosongkan form
                loadDataWithMultiThreading();
                clearFields();
            } else {
                // -- JIKA GAGAL --
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input Suhu atau Kelembapan tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Tangkap error enkripsi jika masih terjadi
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Cetak error lengkap di console untuk debug
        }
    }

    private void updateData() {
        int selectedRow = weatherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String city = cityField.getText();
            double temp = Double.parseDouble(tempField.getText());
            int humidity = Integer.parseInt(humidityField.getText());
            String kondisi = (String) kondisiComboBox.getSelectedItem();
            String notes = notesField.getText();

            String encryptedNotes = CryptoUtils.encrypt(notes);
            WeatherData updatedData = new WeatherData(id, city, temp, humidity, kondisi, encryptedNotes, null);

            // Panggil metode update ke database
            if (dbManager.updateWeatherData(updatedData)) {
                // -- JIKA UPDATE DI DATABASE SUKSES --
                AuditLogger.logAction(loggedInUsername, "UPDATE_DATA", "Updated weather data for ID " + id);
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");

                // PERBARUI TABEL SECARA LANGSUNG (LEBIH EFISIEN)
                tableModel.setValueAt(city, selectedRow, 1); // Update kolom Kota (indeks 1)
                tableModel.setValueAt(temp, selectedRow, 2); // Update kolom Suhu (indeks 2)
                tableModel.setValueAt(humidity, selectedRow, 3);// Update kolom Kelembapan (indeks 3)
                tableModel.setValueAt(kondisi, selectedRow, 4);// Update kolom kondisi (indeks 4)

                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data di database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteData() {
        int selectedRow = weatherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteWeatherData(id)) {
                // Logika jika BERHASIL
                AuditLogger.logAction(loggedInUsername, "DELETE_DATA", "Deleted weather data for ID " + id);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");

                // TAMBAHKAN BARIS INI UNTUK FEEDBACK INSTAN
                tableModel.removeRow(selectedRow);

                // clearFields() bisa dipanggil di sini agar form input langsung kosong
                clearFields();

                // Memuat ulang data di background tetap baik untuk sinkronisasi,
                // tapi sekarang tidak wajib untuk feedback visual.
                // loadDataWithMultiThreading(); 
            } else {
                // Logika jika GAGAL (tetap sama)
                JOptionPane.showMessageDialog(this, "Gagal menghapus data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        cityField.setText("");
        tempField.setText("");
        humidityField.setText("");
        kondisiComboBox.setSelectedIndex(0);
        notesField.setText("");
        weatherTable.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        welcomeLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cityLabel = new javax.swing.JLabel();
        tempLabel = new javax.swing.JLabel();
        humidityLabel = new javax.swing.JLabel();
        notesLabel = new javax.swing.JLabel();
        cityField = new javax.swing.JTextField();
        tempField = new javax.swing.JTextField();
        humidityField = new javax.swing.JTextField();
        notesField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        kondisiComboBox = new javax.swing.JComboBox<>();
        JScrollPane = new javax.swing.JScrollPane();
        weatherTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        logoutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Aplikasi Pencatat Cuaca"); // NOI18N

        welcomeLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        welcomeLabel.setText("Welcome, [username]!");

        cityLabel.setText("City");

        tempLabel.setText("Temperature (°C)");

        humidityLabel.setText("Humidity (%)");

        notesLabel.setText("Secret Note");

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(updateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(deleteButton)
                    .addComponent(addButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Condition");

        kondisiComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sunny", "Cloudy", "Rainy", "Storm" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(humidityLabel)
                            .addComponent(tempLabel)
                            .addComponent(cityLabel)
                            .addComponent(jLabel1)
                            .addComponent(notesLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(notesField)
                            .addComponent(cityField)
                            .addComponent(tempField)
                            .addComponent(humidityField)
                            .addComponent(kondisiComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cityLabel)
                    .addComponent(cityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tempLabel)
                    .addComponent(tempField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(humidityLabel)
                    .addComponent(humidityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kondisiComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(notesLabel)
                    .addComponent(notesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        weatherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kota", "Suhu", "Kelembapan", "Kondisi", "Tanggal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        weatherTable.setToolTipText("");
        weatherTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                weatherTableMouseClicked(evt);
            }
        });
        JScrollPane.setViewportView(weatherTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jMenuBar1.setToolTipText("");
        jMenuBar1.setAutoscrolls(true);
        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenu1.setText("Logout");
        jMenu1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jMenu1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        logoutMenuItem.setText("Logout");
        logoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(logoutMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(welcomeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutMenuItemActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION
        );

        // 2. Hanya lanjutkan jika pengguna menekan tombol "Yes"
        if (confirm == JOptionPane.YES_OPTION) {
            AuditLogger.logAction(loggedInUsername, "LOGOUT", "User logged out");
            // 3. Reset ID pengguna yang login (langkah keamanan penting)
            DatabaseManager.logout();

            // 4. Buka kembali jendela LoginForm
            new LoginForm().setVisible(true);

            // 5. Tutup jendela MainFrame saat ini
            this.dispose();
        }

    }//GEN-LAST:event_logoutMenuItemActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addData();
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        updateData();
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        deleteData();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void weatherTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weatherTableMouseClicked
        tableRowClicked();
    }//GEN-LAST:event_weatherTableMouseClicked

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton addButton;
    private javax.swing.JTextField cityField;
    private javax.swing.JLabel cityLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField humidityField;
    private javax.swing.JLabel humidityLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox<String> kondisiComboBox;
    private javax.swing.JMenuItem logoutMenuItem;
    private javax.swing.JTextField notesField;
    private javax.swing.JLabel notesLabel;
    private javax.swing.JTextField tempField;
    private javax.swing.JLabel tempLabel;
    private javax.swing.JButton updateButton;
    private javax.swing.JTable weatherTable;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
