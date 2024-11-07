package com.example.demo.service.admin.impl;

import com.example.demo.entity.ThuongHieu;
import com.example.demo.repository.admin.ThuongHieuRepository;
import com.example.demo.service.admin.ThuongHieuService;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ThuongHieuImpl implements ThuongHieuService {

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Override
    public List<ThuongHieu> getAll() {
        return thuongHieuRepository.findAll();
    }

    @Override
    public String create(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("thuongHieu", thuongHieu);
            return "admin/san-pham/thuong-hieu/create";
        }

        if (thuongHieu.getMa() == null || thuongHieu.getMa().trim().isEmpty()) {
            thuongHieu.setMa(generateRandomCode());
        }

        for (ThuongHieu th : thuongHieuRepository.findAll()) {
            if (thuongHieu.getMa().equalsIgnoreCase(th.getMa())) {
                model.addAttribute("errorMa", "Mã thương hiệu đã tồn tại trong CSDL!");
                model.addAttribute("thuongHieu", thuongHieu);
                return "admin/san-pham/thuong-hieu/create";
            }
        }

        thuongHieu.setNguoiTao("Admin");
        thuongHieu.setNgayTao(new Date());
        thuongHieu.setTrangThai("Đang hoạt động");
        thuongHieuRepository.save(thuongHieu);
        return "redirect:/admin/san-pham/thuong-hieu/view";
    }

    @Override
    public String update(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("thuongHieu", thuongHieu);
            return "admin/san-pham/thuong-hieu/view-update";
        }

        ThuongHieu currentThuongHieu = thuongHieuRepository.getReferenceById(thuongHieu.getId());
        if (currentThuongHieu != null) {
            model.addAttribute("errorMa", "Mã thương hiệu không tồn tại!");
            model.addAttribute("thuongHieu", thuongHieu);
            return "admin/san-pham/thuong-hieu/view-update";
        }

        if (thuongHieu.getMa() == null || thuongHieu.getMa().trim().isEmpty()) {
            thuongHieu.setMa(generateRandomCode());
        }

        for (ThuongHieu ms : thuongHieuRepository.findAll()) {
            if (!ms.getId().equals(thuongHieu.getId()) && thuongHieu.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã thương hiệu đã tồn tại trong CSDL!");
                model.addAttribute("thuongHieu", thuongHieu);
                return "admin/san-pham/thuong-hieu/view-update";
            }
        }

        thuongHieu.setNguoiSua("Admin");
        thuongHieu.setNgaySua(new Date());
        thuongHieuRepository.save(thuongHieu);
        return "redirect:/admin/san-pham/thuong-hieu/view";
    }

    @Override
    public String delete(int id) {
        ThuongHieu thuongHieu = thuongHieuRepository.findById(id).orElse(null);
        if (thuongHieu != null) {
            thuongHieu.setNgaySua(new Date());
            thuongHieu.setDeleted(1);
            thuongHieuRepository.save(thuongHieu);
        }
        return "redirect:/admin/san-pham/thuong-hieu/view";
    }

    @Override
    public ThuongHieu findById(int id) {
        return thuongHieuRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ThuongHieu> getAllActive(Pageable pageable) {
        return thuongHieuRepository.findAllActiveThuongHieu(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        ThuongHieu thuongHieu = thuongHieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu không tồn tại"));

        if ("Đang hoạt động".equals(thuongHieu.getTrangThai())) {
            thuongHieu.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(thuongHieu.getTrangThai())) {
            thuongHieu.setTrangThai("Đang hoạt động");
        }

        thuongHieu.setNguoiSua("Admin");
        thuongHieu.setNgaySua(new Date());
        thuongHieuRepository.save(thuongHieu);
    }

    @Override
    public Page<ThuongHieu> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return null;
    }

    @Override
    public List<ThuongHieu> getListActiveThuongHieus() {
        return thuongHieuRepository.getListActiveThuongHieu();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<ThuongHieu> thuongHieuList = thuongHieuRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("ThuongHieu");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Mã");
            headerRow.createCell(2).setCellValue("Tên");
            headerRow.createCell(3).setCellValue("Trạng Thái");
//            headerRow.createCell(4).setCellValue("Ngày Tạo");
//            headerRow.createCell(5).setCellValue("Ngày Sửa");
//            headerRow.createCell(6).setCellValue("Người Tạo");
//            headerRow.createCell(7).setCellValue("Người Sửa");
//            headerRow.createCell(8).setCellValue("Deleted");

            // Ghi dữ liệu vào file Excel
            int rowIdx = 1;
            for (ThuongHieu thuongHieu : thuongHieuList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(thuongHieu.getId());
                row.createCell(1).setCellValue(thuongHieu.getMa());
                row.createCell(2).setCellValue(thuongHieu.getTen());
                row.createCell(3).setCellValue(thuongHieu.getTrangThai());
//                row.createCell(4).setCellValue(thuongHieu.getNgayTao().toString());
//                row.createCell(5).setCellValue(thuongHieu.getNgaySua().toString());
//                row.createCell(6).setCellValue(thuongHieu.getNguoiTao());
//                row.createCell(7).setCellValue(thuongHieu.getNguoiSua());
//                row.createCell(8).setCellValue(thuongHieu.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<ThuongHieu> importExcel(MultipartFile file) throws IOException {
        List<ThuongHieu> thuongHieuList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua dòng tiêu đề
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                ThuongHieu thuongHieu = new ThuongHieu();
                thuongHieu.setMa(currentRow.getCell(0).getStringCellValue());
                thuongHieu.setTrangThai(currentRow.getCell(1).getStringCellValue());
                thuongHieu.setTen(currentRow.getCell(2).getStringCellValue());
                thuongHieu.setNgayTao(currentRow.getCell(3).getDateCellValue());
                thuongHieu.setNgaySua(currentRow.getCell(4).getDateCellValue());
                thuongHieu.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                thuongHieu.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                thuongHieu.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                thuongHieuList.add(thuongHieu);
            }
        }

        return thuongHieuList;
    }
}
