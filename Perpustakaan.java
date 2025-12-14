import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Perpustakaan {

    /* ==========================================================
       BAGIAN 1: SISTEM LOGGING (Riwayat Aktivitas)
       ========================================================== */

    // Method helper untuk mencatat aktivitas ke tabel 'perpustakaan'
    private void catatLog(String aksi, String judulBuku, String namaMember, String ket) {
        String sql = "INSERT INTO perpustakaan (aksi, judul_buku, nama_member, keterangan) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aksi);
            stmt.setString(2, judulBuku);
            stmt.setString(3, namaMember); // Bisa "-" jika aksinya oleh admin
            stmt.setString(4, ket);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal mencatat log sistem: " + e.getMessage());
        }
    }

    // Method untuk Menampilkan Riwayat Log (Fitur Admin Opsi 5)
    public void tampilkanRiwayatLog() {
        String sql = "SELECT * FROM perpustakaan ORDER BY tanggal DESC LIMIT 50"; // Tampilkan 50 terakhir

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== RIWAYAT LOG AKTIVITAS (50 Terakhir) ===");
            System.out.printf("%-20s %-15s %-15s %-30s%n", "Waktu", "Aksi", "Member", "Info");
            System.out.println("------------------------------------------------------------------------------------");

            while (rs.next()) {
                String member = rs.getString("nama_member");
                if (member == null) member = "-";

                System.out.printf("%-20s %-15s %-15s %-30s%n",
                        rs.getString("tanggal"),
                        rs.getString("aksi"),
                        member,
                        rs.getString("judul_buku") + " (" + rs.getString("keterangan") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ==========================================================
       BAGIAN 2: MANAJEMEN BUKU
       ========================================================== */

    // Menambah Buku Baru + Catat Log
    public void tambahBuku(Buku b) {
        String jenis = (b instanceof BukuFiksi) ? "Fiksi" : "NonFiksi";
        String sql = "INSERT INTO buku (judul, pengarang, jenis, info_khusus) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, b.getJudul());
            stmt.setString(2, b.getPengarang());
            stmt.setString(3, jenis);
            stmt.setString(4, b.getInfoKhusus());
            stmt.executeUpdate();

            // LOG: Catat penambahan buku
            catatLog("TAMBAH_BUKU", b.getJudul(), "-", "Admin menambah buku tipe " + jenis);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Buku> getSemuaBuku() {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku"; // Ambil semua buku

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToBuku(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Buku> cariBukuByJudul(String keyword) {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku WHERE LOWER(judul) LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToBuku(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper: Mengubah data baris database (ResultSet) menjadi Objek Buku Java
    private Buku mapResultSetToBuku(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String judul = rs.getString("judul");
        String pengarang = rs.getString("pengarang");
        String jenis = rs.getString("jenis");
        String info = rs.getString("info_khusus");
        boolean isDipinjam = rs.getBoolean("is_dipinjam");

        Buku b;
        if (jenis.equalsIgnoreCase("Fiksi")) {
            b = new BukuFiksi(judul, pengarang, info);
        } else {
            b = new BukuNonFiksi(judul, pengarang, info);
        }
        b.setId(id);
        b.setDipinjam(isDipinjam);
        return b;
    }

    /* ==========================================================
       BAGIAN 3: MANAJEMEN MEMBER
       ========================================================== */

    // Registrasi member dengan validasi email & no telepon untuk menghindari duplikasi nama
    public Member daftarAnggota(String nama, String email, String noTelepon, int batasPinjam) {
        // Validasi input tidak kosong
        if (nama.trim().isEmpty() || email.trim().isEmpty() || noTelepon.trim().isEmpty()) {
            return null; // Return null jika ada field kosong
        }

        // Cek apakah email sudah terdaftar (identitas unik)
        if (cekEmailSudahAda(email)) {
            return null; // Email sudah terdaftar
        }

        // Jika nama sama, gunakan email/telepon untuk membedakan
        Member existing = getAnggotaByNama(nama);
        if (existing != null && existing.getEmail().equals(email)) {
            return existing; // Nama dan email sama, return member yang ada
        }

        String sql = "INSERT INTO member (nama, email, no_telepon, batas_pinjam) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama);
            stmt.setString(2, email);
            stmt.setString(3, noTelepon);
            stmt.setInt(4, batasPinjam);
            stmt.executeUpdate();

            // LOG: Catat registrasi member baru
            catatLog("REGISTRASI_MEMBER", "-", nama, "Member baru terdaftar: " + email);

            return getAnggotaByEmail(email); // Ambil ulang dari DB agar semua field terbawa
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Overload method untuk backward compatibility (tanpa email/telepon)
    public Member daftarAnggota(String nama, int batasPinjam) {
        return daftarAnggota(nama, nama + "@perpus.local", "0000000000", batasPinjam);
    }

    public Member getAnggota(String nama) {
        String sql = "SELECT * FROM member WHERE LOWER(nama) = ? LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama.toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member m = new Member(rs.getString("nama"), rs.getInt("batas_pinjam"));
                m.setId(rs.getInt("id"));
                m.setEmail(rs.getString("email"));
                m.setNoTelepon(rs.getString("no_telepon"));
                // Penting: Load juga buku yang sedang dipinjam member ini dari DB
                m.setDaftarDipinjam(getBukuDipinjam(nama));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method baru: Cari member berdasarkan nama (bisa ada duplikat, return yang pertama)
    public Member getAnggotaByNama(String nama) {
        return getAnggota(nama);
    }

    // Method baru: Cari member berdasarkan email (UNIK)
    public Member getAnggotaByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member m = new Member(rs.getString("nama"), rs.getInt("batas_pinjam"));
                m.setId(rs.getInt("id"));
                m.setEmail(rs.getString("email"));
                m.setNoTelepon(rs.getString("no_telepon"));
                m.setDaftarDipinjam(getBukuDipinjam(rs.getString("nama")));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method baru: Cek apakah email sudah terdaftar
    private boolean cekEmailSudahAda(String email) {
        String sql = "SELECT COUNT(*) FROM member WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void tampilkanSemuaAnggota() {
        String sql = "SELECT * FROM member";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== DAFTAR MEMBER ===");
            int i = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                int batas = rs.getInt("batas_pinjam");

                // Hitung jumlah pinjaman real-time
                int pinjamanAktif = countPinjaman(id, conn);

                System.out.printf("%2d) %s — Batas: %d — Sedang Pinjam: %d buku%n",
                        i++, nama, batas, pinjamanAktif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper untuk menghitung jumlah buku yang sedang dipinjam member
    private int countPinjaman(int memberId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM buku WHERE peminjam_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /* ==========================================================
       BAGIAN 4: TRANSAKSI (PINJAM & KEMBALI)
       ========================================================== */

    // Transaksi Peminjaman + Catat Log
    public String pinjamBuku(String namaMember, Buku bukuDipilih) {
        Member m = getAnggota(namaMember);
        if (m == null) return "Member belum terdaftar.";

        // Validasi: Cek apakah buku sudah dipinjam orang lain (langsung ke DB)
        if (cekStatusPinjam(bukuDipilih.getId())) return "Buku sedang dipinjam orang lain.";

        // Validasi: Cek kuota member
        if (m.getDaftarDipinjam().size() >= m.getBatasPinjam()) return "Gagal: batas maksimum pinjaman tercapai.";

        // Update Database: Set buku jadi dipinjam oleh member ID ini
        String sql = "UPDATE buku SET is_dipinjam = 1, peminjam_id = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getId());
            stmt.setInt(2, bukuDipilih.getId());
            stmt.executeUpdate();

            // LOG: Catat peminjaman
            catatLog("PINJAM", bukuDipilih.getJudul(), namaMember, "Member meminjam buku");

            return "Berhasil meminjam: " + bukuDipilih.getJudul();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error database: " + e.getMessage();
        }
    }

    // Transaksi Pengembalian + Catat Log
    public String kembalikanBuku(String namaMember, String judulKeyword) {
        Member m = getAnggota(namaMember);
        if (m == null) return "Member tidak ditemukan.";

        // Cari buku target di daftar pinjaman member
        Buku target = null;
        for (Buku b : m.getDaftarDipinjam()) {
            if (b.getJudul().toLowerCase().contains(judulKeyword.toLowerCase())) {
                target = b;
                break;
            }
        }

        if (target == null) return "Anda tidak sedang meminjam buku dengan judul tersebut.";

        // Update Database: Set peminjam jadi NULL (kosong)
        String sql = "UPDATE buku SET is_dipinjam = 0, peminjam_id = NULL WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, target.getId());
            stmt.executeUpdate();

            // LOG: Catat pengembalian
            catatLog("KEMBALI", target.getJudul(), namaMember, "Buku dikembalikan");

            return "Buku dikembalikan: " + target.getJudul();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error database.";
        }
    }

    // Melihat buku yang dipinjam oleh member TERTENTU
    public List<Buku> getBukuDipinjam(String namaMember) {
        List<Buku> list = new ArrayList<>();
        // Join tabel buku & member
        String sql = "SELECT b.* FROM buku b JOIN member m ON b.peminjam_id = m.id WHERE LOWER(m.nama) = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, namaMember.toLowerCase());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToBuku(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Fitur Baru: Melihat SEMUA buku yang sedang dipinjam (Global View)
    public void tampilkanSemuaBukuDipinjam() {
        String sql = "SELECT b.judul, b.pengarang, m.nama FROM buku b " +
                "JOIN member m ON b.peminjam_id = m.id " +
                "WHERE b.is_dipinjam = 1";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== STATUS SIRKULASI (GLOBAL) ===");
            if (!rs.isBeforeFirst()) {
                System.out.println("(Tidak ada buku yang sedang dipinjam saat ini)");
                return;
            }

            System.out.printf("%-30s %-20s %-15s%n", "Judul Buku", "Pengarang", "Peminjam");
            System.out.println("------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-30s %-20s %-15s%n",
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper: Cek status buku langsung dari DB (Real-time)
    private boolean cekStatusPinjam(int bukuId) {
        String sql = "SELECT is_dipinjam FROM buku WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bukuId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBoolean("is_dipinjam");
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    public Object[][] getLogData() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT * FROM perpustakaan ORDER BY tanggal DESC LIMIT 100";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("tanggal"),
                        rs.getString("aksi"),
                        rs.getString("nama_member"),
                        rs.getString("judul_buku") + " (" + rs.getString("keterangan") + ")"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data.toArray(new Object[0][]);
    }

    // 2. Ambil data Peminjaman Global untuk JTable
    public Object[][] getGlobalLoanData() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT b.judul, b.pengarang, m.nama FROM buku b JOIN member m ON b.peminjam_id = m.id WHERE b.is_dipinjam = 1";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{ rs.getString("judul"), rs.getString("pengarang"), rs.getString("nama") });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data.toArray(new Object[0][]);
    }

    // 3. Ambil semua Member untuk JTable
    public Object[][] getMemberData() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT * FROM member";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                data.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("batas_pinjam"),
                        countPinjaman(id, conn) + " Buku"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data.toArray(new Object[0][]);
    }
}
