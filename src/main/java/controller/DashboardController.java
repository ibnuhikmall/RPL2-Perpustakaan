package controller;

import dao.DashboardDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardController {

    private final DashboardDao dao;

    public DashboardController(DashboardDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public int jumlahBuku() {
        return (int) dao.jumlahBuku();
    }

    @Transactional(readOnly = true)
    public int jumlahAnggota() {
        return (int) dao.jumlahAnggota();
    }

    @Transactional(readOnly = true)
    public int jumlahDipinjam() {
        return (int) dao.jumlahDipinjam();
    }

    @Transactional(readOnly = true)
    public int jumlahTerlambat() {
        return (int) dao.jumlahTerlambat();
    }
}