package com.example.demo.service.admin;

import com.example.demo.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

public interface PhieuGiamGiaService {
    @Transactional
    String toggleStatus(int id);

    default String generateRandomCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "PGG-" + sb.toString();
    }

    Page<PhieuGiamGia> filterPGG(LocalDate tuNgay, LocalDate denNgay, Integer kieu, Integer loai, String trangThai, Pageable pageable);
}
