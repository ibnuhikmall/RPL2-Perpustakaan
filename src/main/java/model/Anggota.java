package model;

import jakarta.persistence.*;

@Entity
@Table(name = "anggota")
public class Anggota {

    @Id
    @Column(name = "no_anggota")
    private String noAnggota;

    private String nama;

    private String alamat;

    @Column(name = "no_hp")
    private String noHp;

    private String status;

    public Anggota() {
    }

    public String getNoAnggota() {
        return noAnggota;
    }

    public void setNoAnggota(String noAnggota) {
        this.noAnggota = noAnggota;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
