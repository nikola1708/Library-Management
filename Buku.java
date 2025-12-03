public abstract class Buku {
    protected int id; // ID dari Database
    private final String judul;
    private final String pengarang;
    private boolean dipinjam = false;

    public Buku(String judul, String pengarang) {
        this.judul = judul;
        this.pengarang = pengarang;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJudul() { return judul; }
    public String getPengarang() { return pengarang; }

    public boolean isDipinjam() { return dipinjam; }
    public void setDipinjam(boolean status) { this.dipinjam = status; }

    public abstract String getKategori();
    public abstract String getInfoKhusus(); // Helper untuk insert DB

    @Override
    public String toString() {
        return String.format("[%d] %s — %s [%s] (%s)",
                id, judul, pengarang, dipinjam ? "Dipinjam" : "Tersedia", getKategori());
    }
}

class BukuFiksi extends Buku {
    private final String genre;

    public BukuFiksi(String judul, String pengarang, String genre) {
        super(judul, pengarang);
        this.genre = genre;
    }

    public String getGenre() { return genre; }

    @Override
    public String getKategori() { return "Fiksi - " + genre; }

    @Override
    public String getInfoKhusus() { return genre; }
}

class BukuNonFiksi extends Buku {
    private final String subjek;

    public BukuNonFiksi(String judul, String pengarang, String subjek) {
        super(judul, pengarang);
        this.subjek = subjek;
    }

    public String getSubjek() { return subjek; }

    @Override
    public String getKategori() { return "Non-Fiksi - " + subjek; }

    @Override
    public String getInfoKhusus() { return subjek; }
}