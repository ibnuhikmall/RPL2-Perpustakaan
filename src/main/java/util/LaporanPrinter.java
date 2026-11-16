package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.LaporanRow;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

// Satu-satunya class yang berhubungan langsung dengan JasperReports.
public class LaporanPrinter {

    // Template hasil kompilasi dari Jaspersoft Studio (ada di src/main/resources/reports)
    private static final String TEMPLATE = "/reports/Laporan_Peminjaman.jasper";

    // Mengisi template dengan data -> menghasilkan JasperPrint (laporan yang siap tampil/ekspor)
    private static JasperPrint isi(List<LaporanRow> data, String periode, String dicetakOleh)
            throws JRException {
        int totalDenda = 0;
        for (LaporanRow r : data) {
            totalDenda += r.getDenda();
        }

        // Parameter: nama harus sama dengan Parameter di template Jasper
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("PERIODE", periode);
        parameter.put("DICETAK_OLEH", dicetakOleh);
        parameter.put("TOTAL_DENDA", totalDenda);

        try (InputStream template = LaporanPrinter.class.getResourceAsStream(TEMPLATE)) {
            if (template == null) {
                throw new JRException("Template tidak ditemukan: " + TEMPLATE);
            }
            return JasperFillManager.fillReport(template, parameter, new JRBeanCollectionDataSource(data));
        } catch (IOException e) {
            throw new JRException(e);
        }
    }

    // Menampilkan laporan di jendela pratinjau (ada tombol cetak dan simpan)
    public static void lihat(List<LaporanRow> data, String periode, String dicetakOleh)
            throws JRException {
        JasperViewer.viewReport(isi(data, periode, dicetakOleh), false); // false = tidak menutup aplikasi
    }

    // Menyimpan laporan langsung sebagai file PDF
    public static void simpanPdf(List<LaporanRow> data, String periode, String dicetakOleh, String lokasiFile)
            throws JRException {
        JasperExportManager.exportReportToPdfFile(isi(data, periode, dicetakOleh), lokasiFile);
    }
}
