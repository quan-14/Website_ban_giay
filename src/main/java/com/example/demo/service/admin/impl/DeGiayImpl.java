package com.example.demo.service.admin.impl;

import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.DeGiay;
import com.example.demo.repository.admin.DeGiayRepository;
import com.example.demo.service.admin.DeGiayService;
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
public class DeGiayImpl implements DeGiayService {


    @Autowired
    private DeGiayRepository deGiayRepository;

    @Override
    public List<DeGiay> getAll() {
        return deGiayRepository.findAll();
    }

    @Override
    public String create(@Valid DeGiay deGiay, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("deGiay", deGiay);
            return "admin/san-pham/de-giay/view";
        }

        if (deGiay.getMa() == null || deGiay.getMa().trim().isEmpty()) {
            deGiay.setMa(generateRandomCode());
        }

        for (DeGiay dg: deGiayRepository.findAll()) {
            if (deGiay.getMa().equalsIgnoreCase(dg.getMa())) {
                model.addAttribute("errorMa", "Mã Đế Giày đã tồn tại trong CSDL!");
                model.addAttribute("deGiay", deGiay);
                return "admin/san-pham/de-giay/view";
            }
        }

        deGiay.setNguoiTao("Admin");
        deGiay.setNgayTao(new Date());
        deGiay.setTrangThai("Đang hoạt động");
        deGiayRepository.save(deGiay);
        return "redirect:/admin/san-pham/de-giay/view";
    }

    @Override
    public String update(@Valid DeGiay deGiay, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("deGiay", deGiay);
            return "admin/san-pham/de-giay/view-update";
        }

        DeGiay currentDeGiay = deGiayRepository.getReferenceById(deGiay.getId());
        if (currentDeGiay== null) {
            model.addAttribute("errorMa", "Mã Đế Giày không tồn tại!");
            model.addAttribute("deGiay", deGiay);
            return "admin/san-pham/de-giay/view-update";
        }

        if (deGiay.getMa() == null || deGiay.getMa().trim().isEmpty()) {
            deGiay.setMa(generateRandomCode());
        }

        for (DeGiay dg : deGiayRepository.findAll()) {
            if (!dg.getId().equals(deGiay.getId()) && deGiay.getMa().equalsIgnoreCase(dg.getMa())) {
                model.addAttribute("errorMa", "Mã Đế Giày đã tồn tại trong CSDL!");
                model.addAttribute("deGiay", deGiay);
                return "admin/san-pham/de-giay/view-update";
            }
        }

        deGiay.setNguoiSua("Admin");
        deGiay.setNgaySua(new Date());
        deGiayRepository.save(deGiay);
        return "redirect:/admin/san-pham/de-giay/view";
    }

    @Override
    public String delete(int id) {
        DeGiay deGiay = deGiayRepository.findById(id).orElse(null);
        if (deGiay != null) {
            deGiay.setNgaySua(new Date());
            deGiay.setDeleted(1);
            deGiayRepository.save(deGiay);
        }
        return "redirect:/admin/san-pham/de-giay/view";
    }

    @Override
    public DeGiay findById(int id) {
        return deGiayRepository.findById(id).orElse(null);
    }

    @Override
    public Page<DeGiay> getAllActive(Pageable pageable) {
        return deGiayRepository.getAllActiveDeGiay(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        DeGiay deGiay = deGiayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đế Giày không tồn tại"));

        if ("Đang hoạt động".equals(deGiay.getTrangThai())) {
            deGiay.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(deGiay.getTrangThai())) {
            deGiay.setTrangThai("Đang hoạt động");
        }

        deGiay.setNguoiSua("Admin");
        deGiay.setNgaySua(new Date());
        deGiayRepository.save(deGiay);
    }

    @Override
    public Page<DeGiay> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return deGiayRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }


    @Override
    public List<DeGiay> getListActiveDeGiays() {
        return deGiayRepository.getListActiceDeGiays();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<DeGiay> deGiayList = deGiayRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("DeGiay");

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
            for (DeGiay deGiay : deGiayList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(deGiay.getId());
                row.createCell(1).setCellValue(deGiay.getMa());
                row.createCell(2).setCellValue(deGiay.getTen());
                row.createCell(3).setCellValue(deGiay.getTrangThai());
//                row.createCell(4).setCellValue(deGiay.getNgayTao().toString());
//                row.createCell(5).setCellValue(deGiay.getNgaySua().toString());
//                row.createCell(6).setCellValue(deGiay.getNguoiTao());
//                row.createCell(7).setCellValue(deGiay.getNguoiSua());
//                row.createCell(8).setCellValue(deGiay.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<DeGiay> importExcel(MultipartFile file) throws IOException {
        List<DeGiay> deGiayList = new ArrayList<>();

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

                DeGiay deGiay = new DeGiay();
                deGiay.setMa(currentRow.getCell(0).getStringCellValue());
                deGiay.setTrangThai(currentRow.getCell(1).getStringCellValue());
                deGiay.setTen(currentRow.getCell(2).getStringCellValue());
                deGiay.setNgayTao(currentRow.getCell(3).getDateCellValue());
                deGiay.setNgaySua(currentRow.getCell(4).getDateCellValue());
                deGiay.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                deGiay.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                deGiay.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                deGiayList.add(deGiay);
            }
        }

        return deGiayList;
    }
}
