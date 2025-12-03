import java.util.ArrayList;
import java.util.List;

public class Member {
    private int id; // Tambahkan ID
    private final String nama;
    private final int batasPinjam;
    // List ini sekarang hanya penampung sementara dari hasil query DB
    private List<Buku> daftarDipinjam = new ArrayList<>();

    public Member(String nama, int batasPinjam) {
        this.nama = nama;
        this.batasPinjam = batasPinjam;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public int getBatasPinjam() { return batasPinjam; }

    public List<Buku> getDaftarDipinjam() { return daftarDipinjam; }


    public void setDaftarDipinjam(List<Buku> list) {
        this.daftarDipinjam = list;
    }

    public void tambahPinjamanLokal(Buku b) {
        daftarDipinjam.add(b);
    }
}