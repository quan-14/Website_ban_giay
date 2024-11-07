package com.example.demo.service.admin.impl;

import com.example.demo.entity.NhaSanXuat;
import com.example.demo.repository.admin.NhaSanXuatRepository;
import com.example.demo.service.admin.NhaSanXuatService;
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
public class NhaSanXuatImpl implements NhaSanXuatService {

    @Autowired
    private NhaSanXuatRepository nhaSanXuatRepository;

    @Override
    public List<NhaSanXuat> getAll() {
        return nhaSanXuatRepository.findAll();
    }

    @Override
    public String create(@Valid NhaSanXuat nhaSanXuat, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("nhaSanXuat", nhaSanXuat);
            return "admin/san-pham/nha-san-xuat/view";
        }

        if (nhaSanXuat.getMa() == null || nhaSanXuat.getMa().trim().isEmpty()) {
            nhaSanXuat.setMa(generateRandomCode());
        }

        for (NhaSanXuat ms: nhaSanXuatRepository.findAll()) {
            if (nhaSanXuat.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã Nhà Sản Xuất đã tồn tại trong CSDL!");
                model.addAttribute("nhaSanXuat", nhaSanXuat);
                return "admin/san-pham/nha-san-xuat/view";
            }
        }

        nhaSanXuat.setNguoiTao("Admin");
        nhaSanXuat.setNgayTao(new Date());
        nhaSanXuat.setTrangThai("Đang hoạt động");
        nhaSanXuatRepository.save(nhaSanXuat);
        return "redirect:/admin/san-pham/nha-san-xuat/view";
    }

    @Override
    public String update(@Valid NhaSanXuat nhaSanXuat, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("nhaSanXuat", nhaSanXuat);
            return "admin/san-pham/nha-san-xuat/view-update";
        }

        NhaSanXuat currentNhaSanXuat = nhaSanXuatRepository.getReferenceById(nhaSanXuat.getId());
        if (currentNhaSanXuat == null) {
            model.addAttribute("errorMa", "Mã Nhà Sản Xuất không tồn tại!");
            model.addAttribute("nhaSanXuat", nhaSanXuat);
            return "admin/san-pham/nha-san-xuat/view-update";
        }

        if (nhaSanXuat.getMa() == null || nhaSanXuat.getMa().trim().isEmpty()) {
            nhaSanXuat.setMa(generateRandomCode());
        }

        for (NhaSanXuat ms : nhaSanXuatRepository.findAll()) {
            if (!ms.getId().equals(nhaSanXuat.getId()) && nhaSanXuat.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã Nhà Sản Xuất đã tồn tại trong CSDL!");
                model.addAttribute("nhaSanXuat", nhaSanXuat);
                return "admin/san-pham/nha-san-xuat/view-update";
            }
        }

        nhaSanXuat.setNguoiSua("Admin");
        nhaSanXuat.setNgaySua(new Date());
        nhaSanXuatRepository.save(nhaSanXuat);
        return "redirect:/admin/nha-san-xuat/mau-sac/view";
    }

    @Override
    public String delete(int id) {
        NhaSanXuat nhaSanXuat = nhaSanXuatRepository.findById(id).orElse(null);
        if (nhaSanXuat != null) {
            nhaSanXuat.setNgaySua(new Date());
            nhaSanXuat.setDeleted(1);
            nhaSanXuatRepository.save(nhaSanXuat);
        }
        return "redirect:/admin/san-pham/nha-san-xuat/view";
    }

    @Override
    public NhaSanXuat findById(int id) {
        return nhaSanXuatRepository.findById(id).orElse(null);
    }

    @Override
    public Page<NhaSanXuat> getAllActive(Pageable pageable) {
        return nhaSanXuatRepository.findAllActiveNhaSanXuats(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        NhaSanXuat nhaSanXuat = nhaSanXuatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));

        if ("Đang hoạt động".equals(nhaSanXuat.getTrangThai())) {
            nhaSanXuat.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(nhaSanXuat.getTrangThai())) {
            nhaSanXuat.setTrangThai("Đang hoạt động");
        }

        nhaSanXuat.setNguoiSua("Admin");
        nhaSanXuat.setNgaySua(new Date());
        nhaSanXuatRepository.save(nhaSanXuat);
    }

    @Override
    public Page<NhaSanXuat> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return nhaSanXuatRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<NhaSanXuat> getListActiveNhaSanXuats() {
        return nhaSanXuatRepository.getListActiveNhaSanXuats();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<NhaSanXuat> nhaSanXuatList = nhaSanXuatRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("NhaSanXuat");

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
            for (NhaSanXuat nhaSanXuat : nhaSanXuatList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(nhaSanXuat.getId());
                row.createCell(1).setCellValue(nhaSanXuat.getMa());
                row.createCell(2).setCellValue(nhaSanXuat.getTen());
                row.createCell(3).setCellValue(nhaSanXuat.getTrangThai());
//                row.createCell(4).setCellValue(nhaSanXuat.getNgayTao().toString());
//                row.createCell(5).setCellValue(nhaSanXuat.getNgaySua().toString());
//                row.createCell(6).setCellValue(nhaSanXuat.getNguoiTao());
//                row.createCell(7).setCellValue(nhaSanXuat.getNguoiSua());
//                row.createCell(8).setCellValue(nhaSanXuat.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<NhaSanXuat> importExcel(MultipartFile file) throws IOException {
        List<NhaSanXuat> nhaSanXuatList = new ArrayList<>();

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

                NhaSanXuat nhaSanXuat = new NhaSanXuat();
                nhaSanXuat.setMa(currentRow.getCell(0).getStringCellValue());
                nhaSanXuat.setTrangThai(currentRow.getCell(1).getStringCellValue());
                nhaSanXuat.setTen(currentRow.getCell(2).getStringCellValue());
                nhaSanXuat.setNgayTao(currentRow.getCell(3).getDateCellValue());
                nhaSanXuat.setNgaySua(currentRow.getCell(4).getDateCellValue());
                nhaSanXuat.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                nhaSanXuat.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                nhaSanXuat.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                nhaSanXuatList.add(nhaSanXuat);
            }
        }

        return nhaSanXuatList;
    }
}
