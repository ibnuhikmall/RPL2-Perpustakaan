package dao;

import java.time.LocalDate;
import java.util.List;
import model.Peminjaman;

public interface LaporanDao {
    // status = null berarti semua status
    List<Peminjaman> cariPeminjaman(LocalDate awal, LocalDate akhir, String status);
}