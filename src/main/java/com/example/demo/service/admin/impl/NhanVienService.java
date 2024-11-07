package com.example.demo.service.admin.impl;

import com.example.demo.entity.NhanVien;
import com.example.demo.repository.admin.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

//    @Autowired
//    private EmailService emailService;

    @Transactional
    public void createNhanVien(String ma, String ten, String matKhau , String email, String soDienThoai, Date ngaySinh, String tinhThanhPho, String quanHuyen, String phuongXa, String diaChiCuThe, int gioiTinh, String trangThai, String vaiTro, String nguoiTao,String hinhAnh) {
        nhanVienRepository.insertNhanVien(ma, ten, matKhau , email, soDienThoai, ngaySinh, tinhThanhPho, quanHuyen, phuongXa, diaChiCuThe, gioiTinh, trangThai, vaiTro, nguoiTao,hinhAnh);
    }



    public void toggleStatus(int id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân Viên không tồn tại"));

        if ("Đang làm việc".equals(nhanVien.getTrangThai())) {
            nhanVien.setTrangThai("Ngừng Làm Việc");
        } else if ("Ngừng Làm Việc".equals(nhanVien.getTrangThai())) {
            nhanVien.setTrangThai("Đang làm việc");
        };
    }

//    public Page<NhanVien> getAllNVActive(Pageable pageable) {
//        return nhanVienRepository.getAllNVActive(pageable);
//    }


    public boolean isEmailExists(String email){
        return nhanVienRepository.existsByEmail(email);
    }
}
