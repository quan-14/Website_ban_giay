package com.example.demo.service.admin.impl;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.ThuongHieu;
import com.example.demo.entity.XuatXu;
import com.example.demo.repository.admin.CoGiayRepository;
import com.example.demo.repository.admin.ThuongHieuRepository;
import com.example.demo.repository.admin.XuatXuRepository;
import com.example.demo.service.admin.CoGiayService;
import com.example.demo.service.admin.XuatXuService;
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
public class XuatXuImpl implements XuatXuService {

    @Autowired
    private XuatXuRepository xuatXuRepository;

    @Override
    public List<XuatXu> getAll() {
        return xuatXuRepository.findAll();
    }

    @Override
    public String create(@Valid XuatXu xuatXu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("xuatXu", xuatXu);
            return "admin/san-pham/xuat-xu/create";
        }

        if (xuatXu.getMa() == null || xuatXu.getMa().trim().isEmpty()) {
            xuatXu.setMa(generateRandomCode());
        }

        for (XuatXu th : xuatXuRepository.findAll()) {
            if (xuatXu.getMa().equalsIgnoreCase(th.getMa())) {
                model.addAttribute("errorMa", "Mã Xuất Xứ đã tồn tại trong CSDL!");
                model.addAttribute("xuatXu", xuatXu);
                return "admin/san-pham/xuat-xu/create";
            }
        }

        xuatXu.setNguoiTao("Admin");
        xuatXu.setNgayTao(new Date());
        xuatXu.setTrangThai("Đang hoạt động");
        xuatXuRepository.save(xuatXu);
        return "redirect:/admin/san-pham/xuat-xu/view";
    }

    @Override
    public String update(@Valid XuatXu xuatXu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("xuatXu", xuatXu);
            return "admin/san-pham/xuat-xu/view-update";
        }

        XuatXu currentXuatXu = xuatXuRepository.getReferenceById(xuatXu.getId());
        if (currentXuatXu != null) {
            model.addAttribute("errorMa", "Mã Xuất Xứ không tồn tại!");
            model.addAttribute("xuatXu", xuatXu);
            return "admin/san-pham/xuat-xu/view-update";
        }

        if (xuatXu.getMa() == null || xuatXu.getMa().trim().isEmpty()) {
            xuatXu.setMa(generateRandomCode());
        }

        for (XuatXu ms : xuatXuRepository.findAll()) {
            if (!ms.getId().equals(xuatXu.getId()) && xuatXu.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã Xuất Xứ đã tồn tại trong CSDL!");
                model.addAttribute("xuatXu", xuatXu);
                return "admin/san-pham/xuat-xu/view-update";
            }
        }

        xuatXu.setNguoiSua("Admin");
        xuatXu.setNgaySua(new Date());
        xuatXuRepository.save(xuatXu);
        return "redirect:/admin/san-pham/xuat-xu/view";
    }

    @Override
    public String delete(int id) {
        XuatXu xuatXu = xuatXuRepository.findById(id).orElse(null);
        if (xuatXu != null) {
            xuatXu.setNgaySua(new Date());
            xuatXu.setDeleted(1);
            xuatXuRepository.save(xuatXu);
        }
        return "redirect:/admin/san-pham/xuat-xu/view";
    }

    @Override
    public XuatXu findById(int id) {
        return xuatXuRepository.findById(id).orElse(null);
    }

    @Override
    public Page<XuatXu> getAllActive(Pageable pageable) {
        return xuatXuRepository.findAllActiveXuatXus(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        XuatXu xuatXu = xuatXuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thương hiệu không tồn tại"));

        if ("Đang hoạt động".equals(xuatXu.getTrangThai())) {
            xuatXu.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(xuatXu.getTrangThai())) {
            xuatXu.setTrangThai("Đang hoạt động");
        }

        xuatXu.setNguoiSua("Admin");
        xuatXu.setNgaySua(new Date());
        xuatXuRepository.save(xuatXu);
    }

    @Override
    public Page<XuatXu> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return null;
    }

    @Override
    public List<XuatXu> getListActiveXuatXus() {
        return xuatXuRepository.getListActiveXuatXus();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<XuatXu> xuatXuList = xuatXuRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("XuatXu");

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
            for (XuatXu xuatXu : xuatXuList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(xuatXu.getId());
                row.createCell(1).setCellValue(xuatXu.getMa());
                row.createCell(2).setCellValue(xuatXu.getTen());
                row.createCell(3).setCellValue(xuatXu.getTrangThai());
//                row.createCell(4).setCellValue(xuatXu.getNgayTao().toString());
//                row.createCell(5).setCellValue(xuatXu.getNgaySua().toString());
//                row.createCell(6).setCellValue(xuatXu.getNguoiTao());
//                row.createCell(7).setCellValue(xuatXu.getNguoiSua());
//                row.createCell(8).setCellValue(xuatXu.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<XuatXu> importExcel(MultipartFile file) throws IOException {
        List<XuatXu> xuatXuList = new ArrayList<>();

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

                XuatXu xuatXu = new XuatXu();
                xuatXu.setMa(currentRow.getCell(0).getStringCellValue());
                xuatXu.setTrangThai(currentRow.getCell(1).getStringCellValue());
                xuatXu.setTen(currentRow.getCell(2).getStringCellValue());
                xuatXu.setNgayTao(currentRow.getCell(3).getDateCellValue());
                xuatXu.setNgaySua(currentRow.getCell(4).getDateCellValue());
                xuatXu.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                xuatXu.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                xuatXu.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                xuatXuList.add(xuatXu);
            }
        }

        return xuatXuList;
    }
}
