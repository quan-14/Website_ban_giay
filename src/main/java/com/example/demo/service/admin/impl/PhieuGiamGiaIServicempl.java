package com.example.demo.service.admin.impl;

import com.example.demo.entity.PhieuGiamGia;
import com.example.demo.repository.admin.PhieuGiamGiaRepository;
import com.example.demo.service.admin.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class PhieuGiamGiaIServicempl implements PhieuGiamGiaService {

    @Autowired
    PhieuGiamGiaRepository phieuGiamGiaRepository;

    //    @Override
//    public void toggleStatus(int id) {
//        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại"));
//        LocalDateTime now = LocalDateTime.now();
//        if ("Đang diễn ra".equals(phieuGiamGia.getTrangThai()) ||"Sắp diễn ra".equals(phieuGiamGia.getTrangThai())) {
//            phieuGiamGia.setTrangThai("Ngừng hoạt động");
//        } else if ("Ngừng hoạt động".equals(phieuGiamGia.getTrangThai())) {
//            if (now.isBefore(phieuGiamGia.getNgayBatDau())) {
//                phieuGiamGia.setTrangThai("Sắp diễn ra");
//            } else if (now.isAfter(phieuGiamGia.getNgayBatDau()) && now.isBefore(phieuGiamGia.getNgayKetThuc())) {
//                phieuGiamGia.setTrangThai("Đang diễn ra");
//            }
//        }
//
//        phieuGiamGia.setNguoiSua("Admin");
//        phieuGiamGia.setNgaySua(new Date());
//        phieuGiamGiaRepository.save(phieuGiamGia);
//    }

    @Transactional
    public String toggleStatus(int id) {
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại"));

        LocalDateTime now = LocalDateTime.now();

        if ("Đang diễn ra".equals(pgg.getTrangThai()) || "Sắp diễn ra".equals(pgg.getTrangThai())) {
            pgg.setTrangThai("Kết thúc");
        } else if (now.isBefore(pgg.getNgayBatDau())) {
            pgg.setTrangThai("Sắp diễn ra");
        } else if (now.isAfter(pgg.getNgayBatDau()) && now.isBefore(pgg.getNgayKetThuc())) {
            pgg.setTrangThai("Đang diễn ra");
        }

        pgg.setNguoiSua("Admin");
        pgg.setNgaySua(new Date());
        phieuGiamGiaRepository.save(pgg);

        return pgg.getTrangThai();
    }

    @Override
    public Page<PhieuGiamGia> filterPGG(LocalDate tuNgay, LocalDate denNgay, Integer kieu, Integer loai, String trangThai, Pageable pageable) {
        Page<PhieuGiamGia> phieuGiamGiaPage = phieuGiamGiaRepository.searchPGG(tuNgay, denNgay, kieu, loai, trangThai, pageable);

        return phieuGiamGiaPage;
    }

//    public PhieuGiamGiaResponseDTO convertDTO(PhieuGiamGia phieuGiamGias) {
//        PhieuGiamGiaResponseDTO phieuGiamGiaResponseDTO = new PhieuGiamGiaResponseDTO();
//        phieuGiamGiaResponseDTO.setTrangThai(phieuGiamGias.getTrangThai());
//        phieuGiamGiaResponseDTO.setLoai(phieuGiamGias.getHinhThucGiam());
//        phieuGiamGiaResponseDTO.setKieu(phieuGiamGias.getHinhThucSuDung());
//        phieuGiamGiaResponseDTO.setNgayBatDau(phieuGiamGias.getNgayBatDau());
//        phieuGiamGiaResponseDTO.setNgayKetThuc(phieuGiamGias.getNgayKetThuc());
//        return phieuGiamGiaResponseDTO;
//    }

}
