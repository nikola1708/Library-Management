import java.util.ArrayList;
import java.util.List;

public class Member {
    private int id; // ID unik dari database
    private final String nama;
    private String email; // Tambahan field untuk identifikasi unik
    private String noTelepon; // Tambahan field untuk identifikasi unik
    private final int batasPinjam;
    // List ini sekarang hanya penampung sementara dari hasil query DB
    private List<Buku> daftarDipinjam = new ArrayList<>();

    public Member(String nama, int batasPinjam) {
        this.nama = nama;
        this.batasPinjam = batasPinjam;
    }

    public Member(String nama, String email, String noTelepon, int batasPinjam) {
        this.nama = nama;
        this.email = email;
        this.noTelepon = noTelepon;
        this.batasPinjam = batasPinjam;
    }

    // Getters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getNoTelepon() { return noTelepon; }
    public int getBatasPinjam() { return batasPinjam; }
    public List<Buku> getDaftarDipinjam() { return daftarDipinjam; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    public void setDaftarDipinjam(List<Buku> list) {
        this.daftarDipinjam = list;
    }

    public void tambahPinjamanLokal(Buku b) {
        daftarDipinjam.add(b);
    }
}