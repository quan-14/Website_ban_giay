package com.example.demo.service.admin;

import com.example.demo.entity.DeGiay;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Random;

public interface DeGiayService {

    List<DeGiay> getAll();

    String create(@Valid DeGiay deGiay, BindingResult bindingResult, Model model);

    String update(@Valid DeGiay deGiay, BindingResult bindingResult, Model model);

    String delete(int id);

    DeGiay findById(int id);

    Page<DeGiay> getAllActive(Pageable pageable);

    default String generateRandomCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "DG-" + sb.toString();
    }

    @Transactional
    void toggleStatus(int id);

    Page<DeGiay> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    List<DeGiay> getListActiveDeGiays();
}
