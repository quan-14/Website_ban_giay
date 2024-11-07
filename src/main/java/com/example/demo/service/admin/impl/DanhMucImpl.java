package com.example.demo.service.admin.impl;

import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.Size;
import com.example.demo.repository.admin.DanhMucRepository;
import com.example.demo.repository.admin.SizeRepository;
import com.example.demo.service.admin.DanhMucService;
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
public class DanhMucImpl implements DanhMucService {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Override
    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }

    @Override
    public String create(@Valid DanhMuc danhMuc, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("danhMuc", danhMuc);
            return "admin/san-pham/danh-muc/view";
        }

        if (danhMuc.getMa() == null || danhMuc.getMa().trim().isEmpty()) {
            danhMuc.setMa(generateRandomCode());
        }

        for (DanhMuc dm: danhMucRepository.findAll()) {
            if (danhMuc.getMa().equalsIgnoreCase(dm.getMa())) {
                model.addAttribute("errorMa", "Mã danh mục đã tồn tại trong CSDL!");
                model.addAttribute("danhMuc", danhMuc);
                return "admin/san-pham/danh-muc/view";
            }
        }

        danhMuc.setNguoiTao("Admin");
        danhMuc.setNgayTao(new Date());
        danhMuc.setTrangThai("Đang hoạt động");
        danhMucRepository.save(danhMuc);
        return "redirect:/admin/san-pham/danh-muc/view";
    }

    @Override
    public String update(@Valid DanhMuc danhMuc, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("danhMuc", danhMuc);
            return "admin/san-pham/danh-muc/view-update";
        }

        DanhMuc currentDanhMuc = danhMucRepository.getReferenceById(danhMuc.getId());
        if (currentDanhMuc == null) {
            model.addAttribute("errorMa", "Mã danh mục không tồn tại!");
            model.addAttribute("danhMuc", danhMuc);
            return "admin/san-pham/danh-muc/view-update";
        }

        if (danhMuc.getMa() == null || danhMuc.getMa().trim().isEmpty()) {
            danhMuc.setMa(generateRandomCode());
        }

        for (DanhMuc dm : danhMucRepository.findAll()) {
            if (!dm.getId().equals(danhMuc.getId()) && danhMuc.getMa().equalsIgnoreCase(dm.getMa())) {
                model.addAttribute("errorMa", "Mã danh mục đã tồn tại trong CSDL!");
                model.addAttribute("danhMuc", danhMuc);
                return "admin/san-pham/danh-muc/view-update";
            }
        }

        danhMuc.setNguoiSua("Admin");
        danhMuc.setNgaySua(new Date());
        danhMucRepository.save(danhMuc);
        return "redirect:/admin/san-pham/danh-muc/view";
    }

    @Override
    public String delete(int id) {
        DanhMuc danhMuc = danhMucRepository.findById(id).orElse(null);
        if (danhMuc != null) {
            danhMuc.setNgaySua(new Date());
            danhMuc.setDeleted(1);
            danhMucRepository.save(danhMuc);
        }
        return "redirect:/admin/san-pham/danh-muc/view";
    }

    @Override
    public DanhMuc findById(int id) {
        return danhMucRepository.findById(id).orElse(null);
    }

    @Override
    public Page<DanhMuc> getAllActive(Pageable pageable) {
        return danhMucRepository.getAllActiveDanhMuc(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Size không tồn tại"));

        if ("Đang hoạt động".equals(danhMuc.getTrangThai())) {
            danhMuc.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(danhMuc.getTrangThai())) {
            danhMuc.setTrangThai("Đang hoạt động");
        }

        danhMuc.setNguoiSua("Admin");
        danhMuc.setNgaySua(new Date());
        danhMucRepository.save(danhMuc);
    }

    @Override
    public Page<DanhMuc> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return danhMucRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<DanhMuc> getListActiveDanhMucs() {
        return danhMucRepository.getListActiveDanhMucs();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<DanhMuc> danhMucList = danhMucRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("DanhMuc");

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
            for (DanhMuc danhMuc : danhMucList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(danhMuc.getId());
                row.createCell(1).setCellValue(danhMuc.getMa());
                row.createCell(2).setCellValue(danhMuc.getTen());
                row.createCell(3).setCellValue(danhMuc.getTrangThai());
//                row.createCell(4).setCellValue(danhMuc.getNgayTao().toString());
//                row.createCell(5).setCellValue(danhMuc.getNgaySua().toString());
//                row.createCell(6).setCellValue(danhMuc.getNguoiTao());
//                row.createCell(7).setCellValue(danhMuc.getNguoiSua());
//                row.createCell(8).setCellValue(danhMuc.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<DanhMuc> importExcel(MultipartFile file) throws IOException {
        List<DanhMuc> danhMucList = new ArrayList<>();

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

                DanhMuc danhMuc = new DanhMuc();
                danhMuc.setMa(currentRow.getCell(0).getStringCellValue());
                danhMuc.setTrangThai(currentRow.getCell(1).getStringCellValue());
                danhMuc.setTen(currentRow.getCell(2).getStringCellValue());
                danhMuc.setNgayTao(currentRow.getCell(3).getDateCellValue());
                danhMuc.setNgaySua(currentRow.getCell(4).getDateCellValue());
                danhMuc.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                danhMuc.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                danhMuc.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                danhMucList.add(danhMuc);
            }
        }

        return danhMucList;
    }
}
