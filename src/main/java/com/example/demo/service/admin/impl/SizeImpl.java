package com.example.demo.service.admin.impl;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.Size;
import com.example.demo.repository.admin.ChatLieuRepository;
import com.example.demo.repository.admin.MauSacRepository;
import com.example.demo.repository.admin.SizeRepository;
import com.example.demo.service.admin.ChatLieuService;
import com.example.demo.service.admin.SizeService;
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
public class SizeImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    @Override
    public String create(@Valid Size size, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("size", size);
            return "admin/san-pham/size/view";
        }

        if (size.getMa() == null || size.getMa().trim().isEmpty()) {
            size.setMa(generateRandomCode());
        }

        for (Size sz: sizeRepository.findAll()) {
            if (size.getMa().equalsIgnoreCase(sz.getMa())) {
                model.addAttribute("errorMa", "Mã size đã tồn tại trong CSDL!");
                model.addAttribute("size", size);
                return "admin/san-pham/size/view";
            }
        }

        size.setNguoiTao("Admin");
        size.setNgayTao(new Date());
        size.setTrangThai("Đang hoạt động");
        sizeRepository.save(size);
        return "redirect:/admin/san-pham/size/view";
    }

    @Override
    public String update(@Valid Size size, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("size", size);
            return "admin/san-pham/size/view-update";
        }

        Size currentSize = sizeRepository.getReferenceById(size.getId());
        if (currentSize == null) {
            model.addAttribute("errorMa", "Mã size không tồn tại!");
            model.addAttribute("size", size);
            return "admin/san-pham/size/view-update";
        }

        if (size.getMa() == null || size.getMa().trim().isEmpty()) {
            size.setMa(generateRandomCode());
        }

        for (Size sz : sizeRepository.findAll()) {
            if (!sz.getId().equals(size.getId()) && size.getMa().equalsIgnoreCase(sz.getMa())) {
                model.addAttribute("errorMa", "Mã size đã tồn tại trong CSDL!");
                model.addAttribute("size", size);
                return "admin/san-pham/size/view-update";
            }
        }

        size.setNguoiSua("Admin");
        size.setNgaySua(new Date());
        sizeRepository.save(size);
        return "redirect:/admin/san-pham/size/view";
    }

    @Override
    public String delete(int id) {
        Size size = sizeRepository.findById(id).orElse(null);
        if (size != null) {
            size.setNgaySua(new Date());
            size.setDeleted(1);
            sizeRepository.save(size);
        }
        return "redirect:/admin/san-pham/size/view";
    }

    @Override
    public Size findById(int id) {
        return sizeRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Size> getAllActive(Pageable pageable) {
        return sizeRepository.getAllActiveSize(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size không tồn tại"));

        if ("Đang hoạt động".equals(size.getTrangThai())) {
            size.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(size.getTrangThai())) {
            size.setTrangThai("Đang hoạt động");
        }

        size.setNguoiSua("Admin");
        size.setNgaySua(new Date());
        sizeRepository.save(size);
    }

    @Override
    public Page<Size> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return sizeRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<Size> getListActiveSizes() {
        return sizeRepository.getListActiveSize();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<Size> sizeList = sizeRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Size");

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
            for (Size size : sizeList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(size.getId());
                row.createCell(1).setCellValue(size.getMa());
                row.createCell(2).setCellValue(size.getTen());
                row.createCell(3).setCellValue(size.getTrangThai());
//                row.createCell(4).setCellValue(size.getNgayTao().toString());
//                row.createCell(5).setCellValue(size.getNgaySua().toString());
//                row.createCell(6).setCellValue(size.getNguoiTao());
//                row.createCell(7).setCellValue(size.getNguoiSua());
//                row.createCell(8).setCellValue(size.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<Size> importExcel(MultipartFile file) throws IOException {
        List<Size> sizeList = new ArrayList<>();

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

                Size size = new Size();
                size.setMa(currentRow.getCell(0).getStringCellValue());
                size.setTrangThai(currentRow.getCell(1).getStringCellValue());
                size.setTen(currentRow.getCell(2).getStringCellValue());
                size.setNgayTao(currentRow.getCell(3).getDateCellValue());
                size.setNgaySua(currentRow.getCell(4).getDateCellValue());
                size.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                size.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                size.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                sizeList.add(size);
            }
        }

        return sizeList;
    }

}
