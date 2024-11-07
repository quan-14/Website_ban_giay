package com.example.demo.service.admin.impl;

import com.example.demo.entity.MauSac;
import com.example.demo.repository.admin.MauSacRepository;
import com.example.demo.service.admin.MauSacService;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class MauSacImpl implements MauSacService {

    @Autowired
    private MauSacRepository mauSacRepository;

    @Override
    public List<MauSac> getAll() {
        return mauSacRepository.findAll();
    }

    @Override
    public String create(@Valid MauSac mauSac, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mauSac", mauSac);
            return "admin/san-pham/mau-sac/view";
        }

        if (mauSac.getMa() == null || mauSac.getMa().trim().isEmpty()) {
            mauSac.setMa(generateRandomCode());
        }

        for (MauSac ms: mauSacRepository.findAll()) {
            if (mauSac.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã màu sắc đã tồn tại trong CSDL!");
                model.addAttribute("mauSac", mauSac);
                return "admin/san-pham/mau-sac/view";
            }
        }

        mauSac.setNguoiTao("Admin");
        mauSac.setNgayTao(new Date());
        mauSac.setTrangThai("Đang hoạt động");
        mauSacRepository.save(mauSac);
        return "redirect:/admin/san-pham/mau-sac/view";
    }

    @Override
    public String update(@Valid MauSac mauSac, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mauSac", mauSac);
            return "admin/san-pham/mau-sac/view-update";
        }

        MauSac currentMauSac = mauSacRepository.getReferenceById(mauSac.getId());
        if (currentMauSac == null) {
            model.addAttribute("errorMa", "Mã màu sắc không tồn tại!");
            model.addAttribute("mauSac", mauSac);
            return "admin/san-pham/mau-sac/view-update";
        }

        if (mauSac.getMa() == null || mauSac.getMa().trim().isEmpty()) {
            mauSac.setMa(generateRandomCode());
        }

        for (MauSac ms : mauSacRepository.findAll()) {
            if (!ms.getId().equals(mauSac.getId()) && mauSac.getMa().equalsIgnoreCase(ms.getMa())) {
                model.addAttribute("errorMa", "Mã màu sắc đã tồn tại trong CSDL!");
                model.addAttribute("mauSac", mauSac);
                return "admin/san-pham/mau-sac/view-update";
            }
        }

        mauSac.setNguoiSua("Admin");
        mauSac.setNgaySua(new Date());
        mauSacRepository.save(mauSac);
        return "redirect:/admin/san-pham/mau-sac/view";
    }

    @Override
    public String delete(int id) {
        MauSac mauSac = mauSacRepository.findById(id).orElse(null);
        if (mauSac != null) {
            mauSac.setNgaySua(new Date());
            mauSac.setDeleted(1);
            mauSacRepository.save(mauSac);
        }
        return "redirect:/admin/san-pham/mau-sac/view";
    }

    @Override
    public MauSac findById(int id) {
        return mauSacRepository.findById(id).orElse(null);
    }

    @Override
    public Page<MauSac> getAllActive(Pageable pageable) {
        return mauSacRepository.getAllActiveMauSac(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        MauSac mauSac = mauSacRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));

        if ("Đang hoạt động".equals(mauSac.getTrangThai())) {
            mauSac.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(mauSac.getTrangThai())) {
            mauSac.setTrangThai("Đang hoạt động");
        }

        mauSac.setNguoiSua("Admin");
        mauSac.setNgaySua(new Date());
        mauSacRepository.save(mauSac);
    }

    @Override
    public Page<MauSac> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return mauSacRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<MauSac> getListActiveMauSacs() {
        return mauSacRepository.getListActiveMauSac();
    }


    public ByteArrayInputStream exportToExcel() throws IOException {
        List<MauSac> mauSacList = mauSacRepository.findAll();

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
            for (MauSac mauSac : mauSacList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(mauSac.getId());
                row.createCell(1).setCellValue(mauSac.getMa());
                row.createCell(2).setCellValue(mauSac.getTen());
                row.createCell(3).setCellValue(mauSac.getTrangThai());
//                row.createCell(4).setCellValue(mauSac.getNgayTao().toString());
//                row.createCell(5).setCellValue(mauSac.getNgaySua().toString());
//                row.createCell(6).setCellValue(mauSac.getNguoiTao());
//                row.createCell(7).setCellValue(mauSac.getNguoiSua());
//                row.createCell(8).setCellValue(mauSac.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<MauSac> importExcel(MultipartFile file) throws IOException {
        List<MauSac> mauSacList = new ArrayList<>();

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

                MauSac mauSac = new MauSac();
                mauSac.setMa(currentRow.getCell(0).getStringCellValue());
                mauSac.setTrangThai(currentRow.getCell(1).getStringCellValue());
                mauSac.setTen(currentRow.getCell(2).getStringCellValue());
                mauSac.setNgayTao(currentRow.getCell(3).getDateCellValue());
                mauSac.setNgaySua(currentRow.getCell(4).getDateCellValue());
                mauSac.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                mauSac.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                mauSac.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                mauSacList.add(mauSac);
            }
        }

        return mauSacList;
    }

}
