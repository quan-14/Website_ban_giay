package com.example.demo.service.admin.impl;

import com.example.demo.entity.MauSac;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.repository.admin.*;
import com.example.demo.service.admin.SanPhamService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
public class SanPhamImpl implements SanPhamService {

    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    XuatXuRepository xuatXuRepository;
    @Autowired
    NhaSanXuatRepository nhaSanXuatRepository;
    @Autowired
    ChatLieuRepository chatLieuRepository;
    @Autowired
    MauSacRepository mauSacRepository;
    @Autowired
    SizeRepository sizeRepository;

    @Override
    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    @Override
    public List<SanPham> getAllActiveListSanPhams() {
        return sanPhamRepository.getListActiveSanPham();
    }

    @Override
    public Page<SanPham> getAllSanPhams(String search, String trangThai, Pageable pageable) {
        if(search == null){
            search = "";
        }
        search = "%"+search+"%";
        Page<SanPham> page = null;
        System.out.println("search: "+search);
        if(trangThai != null){
            if(trangThai == ""){
                trangThai = null;
            }
        }
        if(trangThai == null){
            page = sanPhamRepository.findByParam(search, pageable);
        }
        else{
            page = sanPhamRepository.findByParamAndTrangThai(search, trangThai, pageable);
        }
        for(SanPham s : page.getContent()){
            Long soLuongTon = sanPhamChiTietRepository.totalByProduct(s.getId());
            s.setSoLuongTon(soLuongTon);
        }
        System.out.println("size page: "+page.getContent().size());
        return page;
    }

    @Override
    public SanPham findById(Integer id) {
        return sanPhamRepository.findById(id).orElse(null);
    }
    @Override
    public String create(@Valid SanPham sanPham, BindingResult bindingResult,
                         @RequestParam("selectedColors") List<String> selectedColors,
                         @RequestParam("selectedSizes") List<String> selectedSizes
            , Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sanPham", sanPham);
            return "/admin/san-pham/create";
        }

        if (sanPham.getMa() == null || sanPham.getMa().trim().isEmpty()) {
            sanPham.setMa(generateRandomCodeSP());
        }

        for (SanPham sp : sanPhamRepository.findAll()) {
            if (sanPham.getMa().equalsIgnoreCase(sp.getMa())) {
                model.addAttribute("errorMa", "Mã sản phẩm này đã tồn tại trong CSDL!");
                model.addAttribute("sanPham", sanPham);
                return "admin/san-pham/mau-sac/create";
            }
        }

        sanPham.setNguoiTao("Admin");
        sanPham.setNgayTao(new Date());
        sanPham.setTrangThai("Đang hoạt động");
        sanPhamRepository.save(sanPham);

        SanPhamChiTiet sanPhamChiTiet;
        List<SanPhamChiTiet> lstcheck =  new ArrayList<>();

        for (int i = 0; i < selectedColors.size() - 1 ; i++) {
            for (int j = 0; j < selectedSizes.size() - 1; j++) {

                sanPhamChiTiet = new SanPhamChiTiet();

                sanPhamChiTiet.setSanPham(sanPham);

                sanPhamChiTiet.setXuatXu(xuatXuRepository.getReferenceById(1));
                sanPhamChiTiet.setChatLieu(chatLieuRepository.getReferenceById(1));
                sanPhamChiTiet.setNhaSanXuat(nhaSanXuatRepository.getReferenceById(1));

                sanPhamChiTiet.setMauSac(mauSacRepository.getReferenceById(Integer.parseInt(selectedColors.get(i))));
                sanPhamChiTiet.setSize(sizeRepository.getReferenceById(Integer.parseInt(selectedSizes.get(j))));

                sanPhamChiTiet.setMa(generateRandomCodeSPCT());

                sanPhamChiTiet.setTrangThai("Đang hoạt động");
                sanPhamChiTiet.setDonGia(0d);
                sanPhamChiTiet.setSoLuong(0);
                sanPhamChiTiet.setNgayTao(new Date());
                sanPhamChiTiet.setNguoiTao("Admin");
                sanPhamChiTiet.setDonViTinh("gram");
                sanPhamChiTiet.setKhoiLuong(400);

                sanPhamChiTietRepository.save(sanPhamChiTiet);

                lstcheck.add(sanPhamChiTiet);

            }
        }

        return "redirect:/admin/san-pham/create/ren-ctsp?idSP="+sanPham.getId();

    }


    @Override
    public void toggleStatus(int id) {
        SanPham sanPham = sanPhamRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        if ("Đang hoạt động".equals(sanPham.getTrangThai())) {
            sanPham.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(sanPham.getTrangThai())) {
            sanPham.setTrangThai("Đang hoạt động");
        }

        sanPham.setNguoiSua("Admin");
        sanPham.setNgaySua(new Date());
        sanPhamRepository.save(sanPham);
    }

    @Override
    public String update(@Valid SanPham sanPham, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sanPham", sanPham);
            return "admin/san-pham/view-update";
        }

        SanPham currentSanPham = sanPhamRepository.getReferenceById(sanPham.getId());
        if (currentSanPham == null) {
            model.addAttribute("errorMa", "Mã sản phẩm không tồn tại!");
            model.addAttribute("sanPham", sanPham);
            return "admin/san-pham/mau-sac/view-update";
        }

        if (sanPham.getMa() == null || sanPham.getMa().trim().isEmpty()) {
            sanPham.setMa(generateRandomCodeSP());
        }

        for (SanPham sp : sanPhamRepository.findAll()) {
            if (!sp.getId().equals(sanPham.getId()) && sanPham.getMa().equalsIgnoreCase(sp.getMa())) {
                model.addAttribute("errorMa", "Mã sản phẩm đã tồn tại trong CSDL!");
                model.addAttribute("mauSac", sanPham);
                return "admin/san-pham/view-update";
            }
        }

        sanPham.setNguoiSua("Admin");
        sanPham.setNgaySua(new Date());
        sanPhamRepository.save(sanPham);
        return "redirect:/admin/san-pham/view";
    }

    @Override
    public String delete(int id) {
        SanPham sanPham = sanPhamRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        if (sanPham != null) {
            sanPham.setNgaySua(new Date());
            sanPham.setDeleted(1);
            sanPhamRepository.save(sanPham);
        }
        return "redirect:/admin/san-pham/view";
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<SanPham> sanPhamList = sanPhamRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("SanPham");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Mã Sản Phẩm");
            headerRow.createCell(2).setCellValue("Tên Sản Phẩm");
            headerRow.createCell(3).setCellValue("Cổ Giày");
            headerRow.createCell(4).setCellValue("Đế Giày");
            headerRow.createCell(5).setCellValue("Danh Mục");
            headerRow.createCell(6).setCellValue("Thương Hiệu");
            headerRow.createCell(7).setCellValue("Trạng Thái");
//            headerRow.createCell(8).setCellValue("Ngày Tạo");
//            headerRow.createCell(9).setCellValue("Ngày Sửa");
//            headerRow.createCell(10).setCellValue("Người Tạo");
//            headerRow.createCell(11).setCellValue("Người Sửa");
//            headerRow.createCell(12).setCellValue("Deleted");

            // Ghi dữ liệu vào file Excel
            int rowIdx = 1;
            for (SanPham sanPham : sanPhamList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(sanPham.getId());
                row.createCell(1).setCellValue(sanPham.getMa());
                row.createCell(2).setCellValue(sanPham.getTen());
                row.createCell(3).setCellValue(sanPham.getCoGiay().getTen());
                row.createCell(4).setCellValue(sanPham.getDeGiay().getTen());
                row.createCell(5).setCellValue(sanPham.getDanhMuc().getTen());
                row.createCell(6).setCellValue(sanPham.getThuongHieu().getTen());
                row.createCell(7).setCellValue(sanPham.getTrangThai());

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


}
