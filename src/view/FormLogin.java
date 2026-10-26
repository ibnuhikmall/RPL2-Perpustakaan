package view;

import controller.UserController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FormLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnKeluar;

    public FormLogin() {
        initUI();
        setLocationRelativeTo(null); // tampil di tengah layar
    }

    // ===== Membuat tampilan form =====
    private void initUI() {
        setTitle("Login - Sistem Manajemen Perpustakaan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul
        JLabel lblJudul = new JLabel("LOGIN PERPUSTAKAAN", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 20, 5);
        panel.add(lblJudul, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username"), gbc);

        txtUsername = new JTextField(18);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password"), gbc);

        txtPassword = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Tombol
        JPanel panelTombol = new JPanel();
        panelTombol.setBackground(Color.WHITE);

        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(90, 30));
        btnKeluar = new JButton("Keluar");
        btnKeluar.setPreferredSize(new Dimension(90, 30));
        panelTombol.add(btnLogin);
        panelTombol.add(btnKeluar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 5, 0, 5);
        panel.add(panelTombol, gbc);

        // Aksi tombol
        btnLogin.addActionListener(e -> prosesLogin());
        btnKeluar.addActionListener(e -> System.exit(0));

        // Tombol Enter otomatis menjalankan Login
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(panel);
        pack();
    }

    // ===== Proses login =====
    private void prosesLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.");
            return;
        }

        try {
            UserController uc = new UserController();
            if (uc.login(user, pass)) {
                new MenuUtama().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah.");
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan database: " + e.getMessage());
        }
    }

    // ===== Titik awal program =====
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FormLogin().setVisible(true));
    }
}