import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JFrame {
    // Instance logic utama
    private static final Perpustakaan lib = new Perpustakaan();

    // --- Komponen UI Global (agar bisa di-refresh) ---
    private JTable tableBuku, tableMember, tableLog, tableGlobalPinjam;
    private DefaultTableModel modelBuku, modelMember, modelLog, modelGlobalPinjam;

    // Komponen Tab Member
    private JTextField txtMemberName;
    private JLabel lblStatusMember;
    private JTextArea txtListPinjaman;
    private JButton btnPinjam, btnKembali;

    // State Member yang sedang Login
    private Member currentMember = null;

    public Main() {
        // Setup Window
        setTitle("Sistem Manajemen Perpustakaan & Sirkulasi");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Posisi tengah layar

        // Tab Container Utama
        JTabbedPane tabbedPane = new JTabbedPane();

        // ============================================================
        // TAB 1: KATALOG BUKU (Pencarian & Daftar Buku)
        // ============================================================
        JPanel panelBuku = new JPanel(new BorderLayout());

        // Tabel Buku
        String[] colBuku = {"ID", "Judul", "Pengarang", "Status", "Kategori"};
        modelBuku = new DefaultTableModel(colBuku, 0);
        tableBuku = new JTable(modelBuku);

        // Panel Pencarian (Atas)
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(25);
        JButton btnSearch = new JButton("Cari Judul");
        JButton btnRefreshBuku = new JButton("Refresh Full");

        // Logic Search
        btnSearch.addActionListener(e -> refreshTabelBuku(txtSearch.getText()));
        btnRefreshBuku.addActionListener(e -> {
            txtSearch.setText("");
            refreshTabelBuku("");
        });

        panelSearch.add(new JLabel("Cari Buku:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);
        panelSearch.add(btnRefreshBuku);

        panelBuku.add(panelSearch, BorderLayout.NORTH);
        panelBuku.add(new JScrollPane(tableBuku), BorderLayout.CENTER);

        // ============================================================
        // TAB 2: OPERASIONAL MEMBER (Login, Pinjam, Kembali)
        // ============================================================
        JPanel panelMember = new JPanel(new BorderLayout());

        // 1. Panel Login (Atas)
        JPanel panelLogin = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLogin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtMemberName = new JTextField(15);
        JButton btnLogin = new JButton("Masuk / Daftar");
        lblStatusMember = new JLabel("Status: Belum Login (Silakan masukkan nama)");
        lblStatusMember.setForeground(Color.RED);
        lblStatusMember.setFont(new Font("SansSerif", Font.BOLD, 13));

        panelLogin.add(new JLabel("Nama Member:"));
        panelLogin.add(txtMemberName);
        panelLogin.add(btnLogin);
        panelLogin.add(Box.createHorizontalStrut(20)); // Spasi
        panelLogin.add(lblStatusMember);

        // 2. Panel Daftar Pinjaman Pribadi (Tengah)
        JPanel panelPinjamanMember = new JPanel(new BorderLayout());
        panelPinjamanMember.setBorder(BorderFactory.createTitledBorder("Buku yang Sedang Anda Pinjam"));
        txtListPinjaman = new JTextArea();
        txtListPinjaman.setEditable(false);
        txtListPinjaman.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panelPinjamanMember.add(new JScrollPane(txtListPinjaman), BorderLayout.CENTER);

        // 3. Panel Tombol Aksi (Bawah)
        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPinjam = new JButton("PINJAM BUKU");
        btnKembali = new JButton("KEMBALIKAN BUKU");
        btnPinjam.setPreferredSize(new Dimension(150, 40));
        btnKembali.setPreferredSize(new Dimension(150, 40));

        // Default mati sebelum login
        btnPinjam.setEnabled(false);
        btnKembali.setEnabled(false);

        panelAksi.add(btnPinjam);
        panelAksi.add(btnKembali);

        panelMember.add(panelLogin, BorderLayout.NORTH);
        panelMember.add(panelPinjamanMember, BorderLayout.CENTER);
        panelMember.add(panelAksi, BorderLayout.SOUTH);

        // --- LOGIC MEMBER ---

        // Logic Login
        btnLogin.addActionListener(e -> {
            String nama = txtMemberName.getText().trim();
            if (nama.isEmpty()) return;

            // Cek member di DB
            currentMember = lib.getAnggota(nama);

            // Jika tidak ada, tawarkan buat baru
            if (currentMember == null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Member '" + nama + "' tidak ditemukan.\nDaftarkan sebagai member baru?",
                        "Member Baru", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    currentMember = lib.daftarAnggota(nama, 3); // Default batas 3
                } else {
                    return;
                }
            }

            // Update UI setelah login sukses
            lblStatusMember.setText("Login: " + currentMember.getNama() + " (Kuota Pinjam: " + currentMember.getBatasPinjam() + ")");
            lblStatusMember.setForeground(new Color(0, 128, 0)); // Hijau
            btnPinjam.setEnabled(true);
            btnKembali.setEnabled(true);
            refreshListPinjamanMember(); // Load buku yang sedang dipinjam
        });

        // Logic Pinjam (DENGAN DROPDOWN PILIHAN)
        btnPinjam.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(this, "Masukkan Judul / Kata Kunci Buku:");
            if (keyword == null || keyword.trim().isEmpty()) return;

            // 1. Cari buku
            List<Buku> hasil = lib.cariBukuByJudul(keyword);

            if (hasil.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Buku tidak ditemukan!");
            } else {
                // 2. Siapkan Array String untuk Dropdown
                String[] opsiBuku = new String[hasil.size()];
                for (int i = 0; i < hasil.size(); i++) {
                    Buku b = hasil.get(i);
                    // Format: ID - Judul (Pengarang) [STATUS]
                    String status = b.isDipinjam() ? "[DIPINJAM]" : "[TERSEDIA]";
                    opsiBuku[i] = b.getId() + " - " + b.getJudul() + " (" + b.getPengarang() + ") " + status;
                }

                // 3. Tampilkan Popup Dropdown
                String pilihan = (String) JOptionPane.showInputDialog(
                        this,
                        "Ditemukan " + hasil.size() + " buku. Pilih yang ingin dipinjam:",
                        "Pilih Buku",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opsiBuku,     // List opsi
                        opsiBuku[0]); // Default select

                // 4. Proses Pilihan
                if (pilihan != null) {
                    // Cari object buku asli berdasarkan string pilihan
                    Buku bukuTerpilih = null;
                    for (int i = 0; i < opsiBuku.length; i++) {
                        if (opsiBuku[i].equals(pilihan)) {
                            bukuTerpilih = hasil.get(i);
                            break;
                        }
                    }

                    if (bukuTerpilih != null) {
                        String res = lib.pinjamBuku(currentMember.getNama(), bukuTerpilih);
                        JOptionPane.showMessageDialog(this, res);
                        refreshAllData(); // Refresh semua tabel
                    }
                }
            }
        });

        // Logic Kembali
        btnKembali.addActionListener(e -> {
            String judul = JOptionPane.showInputDialog(this, "Masukkan Judul Buku yang dikembalikan:");
            if (judul != null && !judul.isEmpty()) {
                String res = lib.kembalikanBuku(currentMember.getNama(), judul);
                JOptionPane.showMessageDialog(this, res);
                refreshAllData();
            }
        });

        // ============================================================
        // TAB 3: ADMIN AREA (Tambah Buku & Monitoring)
        // ============================================================
        JSplitPane splitAdmin = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // --- BAGIAN KIRI: Form Input Buku ---
        JPanel panelAddBuku = new JPanel(new GridLayout(6, 2, 10, 10));
        panelAddBuku.setBorder(BorderFactory.createTitledBorder("Tambah Koleksi Buku"));

        JTextField txtJudulBuku = new JTextField();
        JTextField txtPengarang = new JTextField();
        JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Fiksi", "Non-Fiksi"});
        JTextField txtInfoKhusus = new JTextField(); // Genre atau Subjek
        JButton btnSimpanBuku = new JButton("Simpan Buku");

        panelAddBuku.add(new JLabel("Judul Buku:")); panelAddBuku.add(txtJudulBuku);
        panelAddBuku.add(new JLabel("Pengarang:")); panelAddBuku.add(txtPengarang);
        panelAddBuku.add(new JLabel("Tipe Buku:")); panelAddBuku.add(cmbJenis);
        panelAddBuku.add(new JLabel("Genre / Subjek:")); panelAddBuku.add(txtInfoKhusus);
        panelAddBuku.add(new JLabel("")); panelAddBuku.add(btnSimpanBuku);

        // Wrapper agar form tidak memenuhi tinggi layar
        JPanel wrapperForm = new JPanel(new BorderLayout());
        wrapperForm.add(panelAddBuku, BorderLayout.NORTH);

        // Logic Simpan Buku
        btnSimpanBuku.addActionListener(e -> {
            String j = txtJudulBuku.getText();
            String p = txtPengarang.getText();
            String info = txtInfoKhusus.getText();

            if(j.isEmpty() || p.isEmpty() || info.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            if (cmbJenis.getSelectedItem().equals("Fiksi")) {
                lib.tambahBuku(new BukuFiksi(j, p, info));
            } else {
                lib.tambahBuku(new BukuNonFiksi(j, p, info));
            }

            JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
            // Reset Form
            txtJudulBuku.setText(""); txtPengarang.setText(""); txtInfoKhusus.setText("");
            refreshAllData();
        });

        // --- BAGIAN KANAN: Tabel Monitoring (Tabbed lagi) ---
        JTabbedPane tabAdminData = new JTabbedPane();

        // 1. Tabel Log
        modelLog = new DefaultTableModel(new String[]{"Waktu", "Aksi", "Member", "Info"}, 0);
        tableLog = new JTable(modelLog);
        tabAdminData.add("Log Aktivitas", new JScrollPane(tableLog));

        // 2. Tabel Global Pinjam
        modelGlobalPinjam = new DefaultTableModel(new String[]{"Judul Buku", "Pengarang", "Peminjam"}, 0);
        tableGlobalPinjam = new JTable(modelGlobalPinjam);
        tabAdminData.add("Sedang Dipinjam (Global)", new JScrollPane(tableGlobalPinjam));

        // 3. Tabel Daftar Member
        modelMember = new DefaultTableModel(new String[]{"ID", "Nama Member", "Batas Pinjam", "Status Pinjam"}, 0);
        tableMember = new JTable(modelMember);
        tabAdminData.add("Daftar Member", new JScrollPane(tableMember));

        // --- Tambahkan tombol Export CSV di atas tabAdminData ---
        JPanel rightWrapper = new JPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportLog = new JButton("Export Log ke CSV");
        toolbar.add(btnExportLog);
        rightWrapper.add(toolbar, BorderLayout.NORTH);
        rightWrapper.add(tabAdminData, BorderLayout.CENTER);

        // Logic Export CSV
        btnExportLog.addActionListener(e -> {
            Object[][] logs = lib.getLogData();

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("log_aktivitas.csv"));
            int ret = chooser.showSaveDialog(this);
            if (ret != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                // Header CSV
                pw.println("Waktu,Aksi,Member,Info");

                for (Object[] row : logs) {
                    String line = Arrays.stream(row)
                            .map(o -> o == null ? "" : escapeCsv(o.toString()))
                            .collect(Collectors.joining(","));
                    pw.println(line);
                }

                JOptionPane.showMessageDialog(this, "Export berhasil: " + file.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage());
            }
        });

        splitAdmin.setLeftComponent(wrapperForm);
        splitAdmin.setRightComponent(rightWrapper);
        splitAdmin.setDividerLocation(350); // Lebar panel kiri

        // ============================================================
        // FINALISASI FRAME
        // ============================================================
        tabbedPane.addTab("Katalog Buku", new ImageIcon(), panelBuku, "Cari dan Lihat Buku");
        tabbedPane.addTab("Area Member", new ImageIcon(), panelMember, "Login dan Pinjam Buku");
        tabbedPane.addTab("Admin Dashboard", new ImageIcon(), splitAdmin, "Input Buku & Monitoring");

        add(tabbedPane);

        // Load data awal saat aplikasi dibuka
        refreshAllData();
    }

    // ============================================================
    // HELPER METHODS (REFRESH DATA)
    // ============================================================

    // Method Sentral utk refresh semua komponen UI
    private void refreshAllData() {
        refreshTabelBuku("");
        refreshAdminTables();
        refreshListPinjamanMember();
    }

    // Refresh Tabel Buku (Tab 1)
    private void refreshTabelBuku(String keyword) {
        modelBuku.setRowCount(0); // Hapus baris lama
        List<Buku> list = keyword.isEmpty() ? lib.getSemuaBuku() : lib.cariBukuByJudul(keyword);

        for (Buku b : list) {
            modelBuku.addRow(new Object[]{
                    b.getId(),
                    b.getJudul(),
                    b.getPengarang(),
                    b.isDipinjam() ? "Dipinjam" : "Tersedia",
                    b.getKategori()
            });
        }
    }

    // Refresh Text Area Pinjaman (Tab 2)
    private void refreshListPinjamanMember() {
        if (currentMember == null) {
            txtListPinjaman.setText("Silakan login terlebih dahulu.");
            return;
        }
        // Ambil data terbaru member dari DB
        currentMember = lib.getAnggota(currentMember.getNama());

        txtListPinjaman.setText("");
        List<Buku> list = currentMember.getDaftarDipinjam();

        if (list.isEmpty()) {
            txtListPinjaman.append("Anda tidak sedang meminjam buku apapun.");
        } else {
            txtListPinjaman.append("Daftar Buku yang Anda Pinjam:\n");
            txtListPinjaman.append("-------------------------------------------------\n");
            int i = 1;
            for (Buku b : list) {
                txtListPinjaman.append(i++ + ". " + b.getJudul() + " — " + b.getPengarang() + "\n");
            }
        }
    }

    // Refresh Tabel Admin (Tab 3)
    private void refreshAdminTables() {
        // 1. Log History
        modelLog.setRowCount(0);
        Object[][] logs = lib.getLogData(); // Method baru di Perpustakaan.java
        for (Object[] row : logs) modelLog.addRow(row);

        // 2. Global Pinjam
        modelGlobalPinjam.setRowCount(0);
        Object[][] loans = lib.getGlobalLoanData(); // Method baru di Perpustakaan.java
        for (Object[] row : loans) modelGlobalPinjam.addRow(row);

        // 3. Member List
        modelMember.setRowCount(0);
        Object[][] mems = lib.getMemberData(); // Method baru di Perpustakaan.java
        for (Object[] row : mems) modelMember.addRow(row);
    }

    // Utility untuk escape CSV sederhana (menggandakan quotes dan membungkus jika perlu)
    private static String escapeCsv(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return field;
    }

    public static void main(String[] args) {
        // Menggunakan Thread Swing agar GUI Aman
        seedData();
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
    private static void seedData() {
        lib.tambahBuku(new BukuNonFiksi("Clean Code", "Robert C. Martin", "Pemrograman"));
        lib.tambahBuku(new BukuNonFiksi("Design Patterns", "GoF", "Pemrograman"));
        lib.tambahBuku(new BukuNonFiksi("Pemrograman Java Dasar", "J. Arief", "Pemrograman"));
        lib.tambahBuku(new BukuNonFiksi("Struktur Data", "N. Wibowo", "Pemrograman"));
        lib.tambahBuku(new BukuFiksi("Harry Potter", "J.K. Rowling", "Fantasy"));
        lib.tambahBuku(new BukuFiksi("The Great Gatsby", "F. Scott Fitzgerald", "Novel"));
        lib.daftarAnggota("Justin", 3);
        lib.daftarAnggota("Richard", 2);
    }
}
