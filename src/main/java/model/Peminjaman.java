package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "peminjaman")
public class Peminjaman {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pinjam")
    private int idPinjam;

    @ManyToOne
    @JoinColumn(name = "no_anggota")
    private Anggota anggota;

    @ManyToOne
    @JoinColumn(name = "kode_buku")
    private Buku buku;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "tgl_pinjam")
    private LocalDate tglPinjam;

    @Column(name = "tgl_jatuh_tempo")
    private LocalDate tglJatuhTempo;

    @Column(name = "tgl_kembali")
    private LocalDate tglKembali;

    private int denda;

    private String status;

    public Peminjaman() {
    }

    public int getIdPinjam() {
        return idPinjam;
    }

    public void setIdPinjam(int idPinjam) {
        this.idPinjam = idPinjam;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public void setAnggota(Anggota anggota) {
        this.anggota = anggota;
    }

    public Buku getBuku() {
        return buku;
    }

    public void setBuku(Buku buku) {
        this.buku = buku;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(LocalDate tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    public LocalDate getTglJatuhTempo() {
        return tglJatuhTempo;
    }

    public void setTglJatuhTempo(LocalDate tglJatuhTempo) {
        this.tglJatuhTempo = tglJatuhTempo;
    }

    public LocalDate getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(LocalDate tglKembali) {
        this.tglKembali = tglKembali;
    }

    public int getDenda() {
        return denda;
    }

    public void setDenda(int denda) {
        this.denda = denda;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
