package com.example.demo.service.admin;

import com.example.demo.entity.ChatLieu;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Random;

public interface ChatLieuService {
    List<ChatLieu> getAll();

    String create(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model);

    String update(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model);

    String delete(int id);

    ChatLieu findById(int id);

    Page<ChatLieu> getAllActive(Pageable pageable);

    default String generateRandomCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "CL-" + sb.toString();
    }

    @Transactional
    void toggleStatus(int id);

    Page<ChatLieu> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    List<ChatLieu> getListActiveChatLieus();

}
