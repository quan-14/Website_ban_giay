package com.example.demo.service.admin;

import com.example.demo.entity.MauSac;
import com.example.demo.entity.ThuongHieu;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Random;

public interface ThuongHieuService {

    List<ThuongHieu> getAll();

    String create(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model);

    String update(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model);

    String delete(int id);

    ThuongHieu findById(int id);

    Page<ThuongHieu> getAllActive(Pageable pageable);

    default String generateRandomCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "TH-" + sb.toString();
    }

    @Transactional
    void toggleStatus(int id);

    Page<ThuongHieu> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    List<ThuongHieu> getListActiveThuongHieus();

}
