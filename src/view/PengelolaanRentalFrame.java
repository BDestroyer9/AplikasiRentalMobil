/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.KontakController;
import java.io.*;
import model.Kontak;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Bara
 */
public class PengelolaanRentalFrame extends javax.swing.JFrame {
    private DefaultTableModel model;
    private KontakController controller;
    /**
    * Creates new form ContactManagerFrame
    */    
public PengelolaanRentalFrame() {
   initComponents();
    controller = new KontakController();
    model = new DefaultTableModel(new String[]
    {"No", "Nama", "Nomor Telepon", "Kategori"}, 0);
    tblKontak.setModel(model);
    loadContacts();
}

private void loadContacts() {
try {
model.setRowCount(0);
List<Kontak> contacts = controller.getAllContacts();
int rowNumber = 1;
for (Kontak contact : contacts) {
model.addRow(new Object[]{
rowNumber++,
contact.getNama(),
contact.getNomorTelepon(),
contact.getKategori()
});
}
} catch (SQLException e) {
showError(e.getMessage());
}
}
private void showError(String message) {
JOptionPane.showMessageDialog(this, message, "Error",
JOptionPane.ERROR_MESSAGE);
}
private void addContact() {
String nama = txtNama.getText().trim();
String nomorTelepon = txtNomorTelepon.getText().trim();
String kategori = (String) cmbKategori.getSelectedItem();
if (!validatePhoneNumber(nomorTelepon)) {
return; // Validasi nomor telepon gagal
}
try {
if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
return;
}
controller.addContact(nama, nomorTelepon, kategori);
loadContacts();
JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
clearInputFields();
} catch (SQLException ex) {
showError("Gagal menambahkan kontak: " + ex.getMessage());
}
}
private boolean validatePhoneNumber(String phoneNumber) {
if (phoneNumber == null || phoneNumber.isEmpty()) {
JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.");
return false;
}
if (!phoneNumber.matches("\\d+")) { // Hanya angka
JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
return false;
}
if (phoneNumber.length() < 8 || phoneNumber.length() > 15) { // Panjang 8-15
JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter.");
return false;
}
return true;
}
private void clearInputFields() {
txtNama.setText("");
txtNomorTelepon.setText("");
cmbKategori.setSelectedIndex(0);
}
private void editContact() {
int selectedRow = tblKontak.getSelectedRow();
if (selectedRow == -1) {
JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
return;
}
int id = (int) model.getValueAt(selectedRow, 0);
String nama = txtNama.getText().trim();
String nomorTelepon = txtNomorTelepon.getText().trim();
String kategori = (String) cmbKategori.getSelectedItem();
if (!validatePhoneNumber(nomorTelepon)) {
return;
}
try {
if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
return;
}
controller.updateContact(id, nama, nomorTelepon, kategori);
loadContacts();
JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
clearInputFields();
} catch (SQLException ex) {
showError("Gagal memperbarui kontak: " + ex.getMessage());
}
}
private void populateInputFields(int selectedRow) {
// Ambil data dari JTable
String nama = model.getValueAt(selectedRow, 1).toString();
String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
String kategori = model.getValueAt(selectedRow, 3).toString();
// Set data ke komponen input
txtNama.setText(nama);
txtNomorTelepon.setText(nomorTelepon);
cmbKategori.setSelectedItem(kategori);
}

private void deleteContact() {
int selectedRow = tblKontak.getSelectedRow();
if (selectedRow != -1) {
int id = (int) model.getValueAt(selectedRow, 0);
try {
controller.deleteContact(id);
loadContacts();
JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus!");
clearInputFields();
} catch (SQLException e) {

showError(e.getMessage());
}
}
}

private void searchContact() {
String keyword = txtPencarian.getText().trim();
if (!keyword.isEmpty()) {
try {
List<Kontak> contacts = controller.searchContacts(keyword);
model.setRowCount(0); // Bersihkan tabel
for (Kontak contact : contacts) {
model.addRow(new Object[]{
contact.getId(),
contact.getNama(),
contact.getNomorTelepon(),
contact.getKategori()
});
}
if (contacts.isEmpty()) {
JOptionPane.showMessageDialog(this, "Tidak ada kontak ditemukan.");
}
} catch (SQLException ex) {
showError(ex.getMessage());
}
} else {
loadContacts();
}
}

private void exportToCSV() {
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Simpan File CSV");
int userSelection = fileChooser.showSaveDialog(this);
if (userSelection == JFileChooser.APPROVE_OPTION) {
File fileToSave = fileChooser.getSelectedFile();
// Tambahkan ekstensi .csv jika pengguna tidak menambahkannya
if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
}
try (BufferedWriter writer = new BufferedWriter(new
FileWriter(fileToSave))) { 
    writer.write("ID,Nama,Nomor Telepon,Kategori\n"); // HeaderCSV
for (int i = 0; i < model.getRowCount(); i++) {
writer.write(
model.getValueAt(i, 0) + "," +
model.getValueAt(i, 1) + "," +
model.getValueAt(i, 2) + "," +
model.getValueAt(i, 3) + "\n"
);
}
JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke " + fileToSave.getAbsolutePath());
} catch (IOException ex) {
showError("Gagal menulis file: " + ex.getMessage());
}
}
}
private void importFromCSV() {
showCSVGuide();
int confirm = JOptionPane.showConfirmDialog(
this,
"Apakah Anda yakin file CSV yang dipilih sudah sesuai dengan format?",
"Konfirmasi Impor CSV",
JOptionPane.YES_NO_OPTION
);
if (confirm == JOptionPane.YES_OPTION) {
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Pilih File CSV");
int userSelection = fileChooser.showOpenDialog(this);
if (userSelection == JFileChooser.APPROVE_OPTION) {
File fileToOpen = fileChooser.getSelectedFile();
try (BufferedReader reader = new BufferedReader(new
FileReader(fileToOpen))) {

String line = reader.readLine(); // Baca header
if (!validateCSVHeader(line)) {
JOptionPane.showMessageDialog(this, "Format header CSV tidak valid. Pastikan header adalah: ID,Nama,Nomor Telepon,Kategori",
"Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
return;
}
int rowCount = 0;
int errorCount = 0;
int duplicateCount = 0;
StringBuilder errorLog = new StringBuilder("Baris dengan kesalahan:\n");
while ((line = reader.readLine()) != null) {
rowCount++;

String[] data = line.split(",");

if (data.length != 4) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Format kolom tidak sesuai.\n");
continue;
}
String nama = data[1].trim();
String nomorTelepon = data[2].trim();
String kategori = data[3].trim();
if (nama.isEmpty() || nomorTelepon.isEmpty()) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Nama atau Nomor Telepon kosong.\n");
continue;
}
if (!validatePhoneNumber(nomorTelepon)) {
errorCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Nomor Telepon tidak valid.\n");
continue;
}
try {
if
(controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
duplicateCount++;

errorLog.append("Baris ").append(rowCount +

1).append(": Kontak sudah ada.\n"); continue;
}

} catch (SQLException ex)
{Logger.getLogger(PengelolaanRentalFrame.class.getName()).log(Level.SEVERE, null, ex);
}
try {
controller.addContact(nama, nomorTelepon,
kategori);
} catch (SQLException ex) {

errorCount++;
errorLog.append("Baris ").append(rowCount +1).append(": Gagal menyimpan ke database -").append(ex.getMessage()).append("\n");
}
}
loadContacts();
if (errorCount > 0 || duplicateCount > 0) {
errorLog.append("\nTotal baris dengan kesalahan:").append(errorCount).append("\n");
errorLog.append("Total baris duplikat:").append(duplicateCount).append("\n");
JOptionPane.showMessageDialog(this,
errorLog.toString(), "Kesalahan Impor", JOptionPane.WARNING_MESSAGE);
} else {
JOptionPane.showMessageDialog(this, "Semua data berhasil diimpor.");
}
} catch (IOException ex) {
showError("Gagal membaca file: " + ex.getMessage());
}
}
}
}
private void showCSVGuide() {
String guideMessage = "Format CSV untuk impor data:\n" +
"- Header wajib: ID, Nama, Nomor Telepon, Kategori\n" +
"- ID dapat kosong (akan diisi otomatis)\n" +
"- Nama dan Nomor Telepon wajib diisi\n" +
"- Contoh isi file CSV:\n" +
" 1, Andi, 08123456789, Teman\n" +
" 2, Budi Doremi, 08567890123, Keluarga\n\n" +
"Pastikan file CSV sesuai format sebelum melakukan impor.";
JOptionPane.showMessageDialog(this, guideMessage, "Panduan Format CSV", JOptionPane.INFORMATION_MESSAGE);
}
private boolean validateCSVHeader(String header) {
return header != null &&
header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtNomorTelepon = new javax.swing.JTextField();
        txtPencarian = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rental Mobil Univ");

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));
        jPanel1.setName(""); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("APLIKASI RENTAL MOBIL UNIV");

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama Penyewa");

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nomer Telepon");

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Jenis Mobil");

        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Pencarian");

        txtNama.setName("txtNama"); // NOI18N

        txtNomorTelepon.setName("txtNomorTelepon"); // NOI18N

        txtPencarian.setName("txtPencarian"); // NOI18N

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Avanza", "Sigra", "Alphard", "Calya", "Xenia", "Terios" }));
        cmbKategori.setName("cmbKategori"); // NOI18N
        cmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKategoriActionPerformed(evt);
            }
        });

        btnTambah.setText("Tambah");
        btnTambah.setName("btnTambah"); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.setName("btnHapus"); // NOI18N
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.setName("tblKontak"); // NOI18N
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        tblKontak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblKontakKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        btnImport.setLabel("Import");
        btnImport.setName("btnImport"); // NOI18N
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnExport.setLabel("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNama)
                            .addComponent(txtNomorTelepon)
                            .addComponent(cmbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(46, 46, 46)
                                .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(49, 49, 49)
                                .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtPencarian, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(169, 169, 169)))
                .addGap(496, 496, 496))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(499, 499, 499))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(416, 416, 416)
                .addComponent(btnExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomorTelepon)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTambah, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnHapus)
                        .addComponent(btnEdit)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExport)
                    .addComponent(btnImport))
                .addContainerGap(713, Short.MAX_VALUE))
        );

        btnImport.getAccessibleContext().setAccessibleName("");
        btnExport.getAccessibleContext().setAccessibleName("btnExport");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.getAccessibleContext().setAccessibleName("");

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKategoriActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
    addContact();        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
    editContact();        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
    int selectedRow = tblKontak.getSelectedRow();
    if (selectedRow != -1) {
    populateInputFields(selectedRow);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_tblKontakMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
    deleteContact();        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tblKontakKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblKontakKeyTyped
    searchContact();        // TODO add your handling code here:
    }//GEN-LAST:event_tblKontakKeyTyped

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
    exportToCSV();        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
    importFromCSV();        // TODO add your handling code here:
    }//GEN-LAST:event_btnImportActionPerformed

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
            java.util.logging.Logger.getLogger(PengelolaanRentalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanRentalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanRentalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanRentalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanRentalFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNomorTelepon;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
