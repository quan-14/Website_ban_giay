package com.example.demo.service.admin;

import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;

public interface SanPhamService {

    List<SanPham> getAll();

    List<SanPham> getAllActiveListSanPhams();

    Page<SanPham> getAllSanPhams(String search, String trangThai, Pageable pageable);

    SanPham findById(Integer id);

    String create(@Valid SanPham sanPham, BindingResult bindingResult, @RequestParam("selectedColors") List<String> selectedColors,
                  @RequestParam("selectedSizes") List<String> selectedSizes, Model model);

    default String generateRandomCodeSP() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "SP-" + sb.toString();
    }
    default String generateRandomCodeSPCT() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "SPCT-" + sb.toString();
    }

    @Transactional
    void toggleStatus(int id);

    String update(@Valid SanPham sanPham, BindingResult bindingResult, Model model);

    String delete(int id);

}
