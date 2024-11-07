package com.example.demo.service.admin.impl;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.DanhMuc;
import com.example.demo.repository.admin.CoGiayRepository;
import com.example.demo.repository.admin.DanhMucRepository;
import com.example.demo.service.admin.CoGiayService;
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
public class CoGiayImpl implements CoGiayService {

    @Autowired
    private CoGiayRepository coGiayRepository;

    @Override
    public List<CoGiay> getAll() {
        return coGiayRepository.findAll();
    }

    @Override
    public String create(@Valid CoGiay coGiay, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("coGiay", coGiay);
            return "admin/san-pham/co-giay/view";
        }

        if (coGiay.getMa() == null || coGiay.getMa().trim().isEmpty()) {
            coGiay.setMa(generateRandomCode());
        }

        for (CoGiay cg: coGiayRepository.findAll()) {
            if (coGiay.getMa().equalsIgnoreCase(cg.getMa())) {
                model.addAttribute("errorMa", "Mã Cổ Giày đã tồn tại trong CSDL!");
                model.addAttribute("coGiay", coGiay);
                return "admin/san-pham/co-giay/view";
            }
        }

        coGiay.setNguoiTao("Admin");
        coGiay.setNgayTao(new Date());
        coGiay.setTrangThai("Đang hoạt động");
        coGiayRepository.save(coGiay);
        return "redirect:/admin/san-pham/co-giay/view";
    }

    @Override
    public String update(@Valid CoGiay coGiay, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("coGiay", coGiay);
            return "admin/san-pham/co-giay/view-update";
        }

        CoGiay currentCoGiay = coGiayRepository.getReferenceById(coGiay.getId());
        if (currentCoGiay == null) {
            model.addAttribute("errorMa", "Mã cổ giày không tồn tại!");
            model.addAttribute("coGiay", coGiay);
            return "admin/san-pham/co-giay/view-update";
        }

        if (coGiay.getMa() == null || coGiay.getMa().trim().isEmpty()) {
            coGiay.setMa(generateRandomCode());
        }

        for (CoGiay dm :coGiayRepository.findAll()) {
            if (!dm.getId().equals(coGiay.getId()) && coGiay.getMa().equalsIgnoreCase(dm.getMa())) {
                model.addAttribute("errorMa", "Mã cổ giày đã tồn tại trong CSDL!");
                model.addAttribute("coGiay", coGiay);
                return "admin/san-pham/co-giay/view-update";
            }
        }

        coGiay.setNguoiSua("Admin");
        coGiay.setNgaySua(new Date());
        coGiayRepository.save(coGiay);
        return "redirect:/admin/san-pham/co-giay/view";
    }

    @Override
    public String delete(int id) {
        CoGiay coGiay = coGiayRepository.findById(id).orElse(null);
        if (coGiay != null) {
            coGiay.setNgaySua(new Date());
            coGiay.setDeleted(1);
            coGiayRepository.save(coGiay);
        }
        return "redirect:/admin/san-pham/co-giay/view";
    }

    @Override
    public CoGiay findById(int id) {
        return coGiayRepository.findById(id).orElse(null);
    }

    @Override
    public Page<CoGiay> getAllActive(Pageable pageable) {
        return coGiayRepository.findAllActiveCoGiay(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        CoGiay coGiay = coGiayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cổ giày không tồn tại"));

        if ("Đang hoạt động".equals(coGiay.getTrangThai())) {
            coGiay.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(coGiay.getTrangThai())) {
            coGiay.setTrangThai("Đang hoạt động");
        }

        coGiay.setNguoiSua("Admin");
        coGiay.setNgaySua(new Date());
        coGiayRepository.save(coGiay);
    }

    @Override
    public Page<CoGiay> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return coGiayRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<CoGiay> getListActiveCoGiays() {
        return coGiayRepository.getListActiveCoGiays();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<CoGiay> coGiayList = coGiayRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("MauSac");

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
            for (CoGiay coGiay : coGiayList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(coGiay.getId());
                row.createCell(1).setCellValue(coGiay.getMa());
                row.createCell(2).setCellValue(coGiay.getTen());
                row.createCell(3).setCellValue(coGiay.getTrangThai());
//                row.createCell(4).setCellValue(coGiay.getNgayTao().toString());
//                row.createCell(5).setCellValue(coGiay.getNgaySua().toString());
//                row.createCell(6).setCellValue(coGiay.getNguoiTao());
//                row.createCell(7).setCellValue(coGiay.getNguoiSua());
//                row.createCell(8).setCellValue(coGiay.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<CoGiay> importExcel(MultipartFile file) throws IOException {
        List<CoGiay> coGiayList = new ArrayList<>();

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

                CoGiay coGiay = new CoGiay();
                coGiay.setMa(currentRow.getCell(0).getStringCellValue());
                coGiay.setTrangThai(currentRow.getCell(1).getStringCellValue());
                coGiay.setTen(currentRow.getCell(2).getStringCellValue());
                coGiay.setNgayTao(currentRow.getCell(3).getDateCellValue());
                coGiay.setNgaySua(currentRow.getCell(4).getDateCellValue());
                coGiay.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                coGiay.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                coGiay.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                coGiayList.add(coGiay);
            }
        }

        return coGiayList;
    }
}
