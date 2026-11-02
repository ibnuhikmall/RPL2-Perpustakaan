-- =====================================================
-- Database Sistem Manajemen Perpustakaan
-- Untuk MySQL / MariaDB (XAMPP, phpMyAdmin)
-- =====================================================

CREATE DATABASE IF NOT EXISTS db_perpustakaan
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE db_perpustakaan;

-- Hapus tabel lama (urutan: anak dulu, induk belakangan)
DROP TABLE IF EXISTS peminjaman;
DROP TABLE IF EXISTS anggota;
DROP TABLE IF EXISTS buku;
DROP TABLE IF EXISTS users;

-- -----------------------------------------------------
-- 1. Tabel users (pengguna aplikasi: Admin dan Petugas)
-- -----------------------------------------------------
CREATE TABLE users (
  id_user       INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  password      VARCHAR(255) NOT NULL,          -- disimpan dalam bentuk hash SHA-256
  nama_lengkap  VARCHAR(100) NOT NULL,
  role          ENUM('Admin','Petugas') NOT NULL DEFAULT 'Petugas'
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 2. Tabel buku
-- -----------------------------------------------------
CREATE TABLE buku (
  kode_buku     VARCHAR(10)  PRIMARY KEY,
  judul         VARCHAR(150) NOT NULL,
  pengarang     VARCHAR(100) NOT NULL,
  penerbit      VARCHAR(100) NOT NULL,
  tahun_terbit  INT          NOT NULL,
  kategori      VARCHAR(50)  NOT NULL,
  stok          INT          NOT NULL DEFAULT 0,
  CONSTRAINT chk_stok CHECK (stok >= 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 3. Tabel anggota
-- -----------------------------------------------------
CREATE TABLE anggota (
  no_anggota    VARCHAR(10)  PRIMARY KEY,
  nama          VARCHAR(100) NOT NULL,
  alamat        TEXT,
  no_hp         VARCHAR(15),
  status        ENUM('Aktif','Nonaktif') NOT NULL DEFAULT 'Aktif'
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 4. Tabel peminjaman (transaksi)
-- -----------------------------------------------------
CREATE TABLE peminjaman (
  id_pinjam        INT AUTO_INCREMENT PRIMARY KEY,
  no_anggota       VARCHAR(10) NOT NULL,
  kode_buku        VARCHAR(10) NOT NULL,
  id_user          INT         NOT NULL,         -- petugas yang mencatat transaksi
  tgl_pinjam       DATE        NOT NULL,
  tgl_jatuh_tempo  DATE        NOT NULL,         -- tgl_pinjam + 7 hari
  tgl_kembali      DATE        NULL,             -- NULL selama belum dikembalikan
  denda            INT         NOT NULL DEFAULT 0,
  status           ENUM('Dipinjam','Dikembalikan') NOT NULL DEFAULT 'Dipinjam',
  CONSTRAINT fk_pinjam_anggota FOREIGN KEY (no_anggota)
    REFERENCES anggota(no_anggota) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pinjam_buku FOREIGN KEY (kode_buku)
    REFERENCES buku(kode_buku) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pinjam_user FOREIGN KEY (id_user)
    REFERENCES users(id_user) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- DATA AWAL
-- =====================================================

-- Akun login (password di-hash dengan SHA-256)
--   admin   / admin123
--   petugas / petugas123
INSERT INTO users (username, password, nama_lengkap, role) VALUES
('admin',   SHA2('admin123', 256),   'Administrator',   'Admin'),
('petugas', SHA2('petugas123', 256), 'Petugas Perpus',  'Petugas');

-- Data buku contoh
-- (stok B001 dan B002 sudah dikurangi karena sedang dipinjam pada data contoh di bawah)
INSERT INTO buku (kode_buku, judul, pengarang, penerbit, tahun_terbit, kategori, stok) VALUES
('B001', 'Laskar Pelangi',                'Andrea Hirata',        'Bentang Pustaka', 2005, 'Novel',    4),
('B002', 'Bumi Manusia',                  'Pramoedya Ananta Toer','Hasta Mitra',     1980, 'Novel',    2),
('B003', 'Algoritma dan Pemrograman',     'Rinaldi Munir',        'Informatika',     2016, 'Komputer', 5),
('B004', 'Basis Data',                    'Fathansyah',           'Informatika',     2018, 'Komputer', 3),
('B005', 'Sejarah Indonesia Modern',      'M.C. Ricklefs',        'Serambi',         2008, 'Sejarah',  2);

-- Data anggota contoh
INSERT INTO anggota (no_anggota, nama, alamat, no_hp, status) VALUES
('A001', 'Budi Santoso',  'Jl. Melati No. 5, Depok',    '081234567890', 'Aktif'),
('A002', 'Siti Aminah',   'Jl. Kenanga No. 12, Depok',  '081298765432', 'Aktif'),
('A003', 'Rudi Hartono',  'Jl. Mawar No. 8, Bogor',     '085711122233', 'Nonaktif');

-- Data peminjaman contoh
-- 1) Sudah lewat jatuh tempo (terlambat 3 hari) -> untuk menguji denda
-- 2) Masih berjalan, belum jatuh tempo
INSERT INTO peminjaman (no_anggota, kode_buku, id_user, tgl_pinjam, tgl_jatuh_tempo, status) VALUES
('A001', 'B001', 2, CURDATE() - INTERVAL 10 DAY, CURDATE() - INTERVAL 3 DAY, 'Dipinjam'),
('A002', 'B002', 2, CURDATE() - INTERVAL 2 DAY,  CURDATE() + INTERVAL 5 DAY, 'Dipinjam');

-- =====================================================
-- CONTOH QUERY UNTUK DIPAKAI DI JAVA (NetBeans)
-- =====================================================

-- Login (di Java pakai PreparedStatement, tanda ? diisi dari JTextField):
--   SELECT * FROM users WHERE username = ? AND password = SHA2(?, 256);

-- Dashboard:
--   SELECT COUNT(*) FROM buku;
--   SELECT COUNT(*) FROM anggota;
--   SELECT COUNT(*) FROM peminjaman WHERE status = 'Dipinjam';
--   SELECT COUNT(*) FROM peminjaman WHERE status = 'Dipinjam' AND tgl_jatuh_tempo < CURDATE();

-- Peminjaman (jalankan dua perintah ini dalam satu transaksi):
--   INSERT INTO peminjaman (no_anggota, kode_buku, id_user, tgl_pinjam, tgl_jatuh_tempo)
--     VALUES (?, ?, ?, CURDATE(), CURDATE() + INTERVAL 7 DAY);
--   UPDATE buku SET stok = stok - 1 WHERE kode_buku = ? AND stok > 0;

-- Pengembalian (denda Rp1.000 per hari terlambat):
--   UPDATE peminjaman
--      SET tgl_kembali = CURDATE(),
--          status = 'Dikembalikan',
--          denda = GREATEST(DATEDIFF(CURDATE(), tgl_jatuh_tempo), 0) * 1000
--    WHERE id_pinjam = ?;
--   UPDATE buku SET stok = stok + 1 WHERE kode_buku = ?;

-- Laporan peminjaman per periode:
--   SELECT p.id_pinjam, a.nama, b.judul, p.tgl_pinjam, p.tgl_kembali, p.denda, p.status
--     FROM peminjaman p
--     JOIN anggota a ON p.no_anggota = a.no_anggota
--     JOIN buku b ON p.kode_buku = b.kode_buku
--    WHERE p.tgl_pinjam BETWEEN ? AND ?;
