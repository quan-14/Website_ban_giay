package com.example.demo.service.admin;

import com.example.demo.entity.Size;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Random;

public interface SizeService {
    List<Size> getAll();

    String create(@Valid Size size, BindingResult bindingResult, Model model);

    String update(@Valid Size size, BindingResult bindingResult, Model model);

    String delete(int id);

    Size findById(int id);

    Page<Size> getAllActive(Pageable pageable);

    default String generateRandomCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "SZ-" + sb.toString();
    }

    @Transactional
    void toggleStatus(int id);

    Page<Size> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    List<Size> getListActiveSizes();


}
