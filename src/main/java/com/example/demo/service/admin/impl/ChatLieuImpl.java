package com.example.demo.service.admin.impl;

import com.example.demo.entity.ChatLieu;
import com.example.demo.repository.admin.ChatLieuRepository;
import com.example.demo.service.admin.ChatLieuService;
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
public class ChatLieuImpl implements ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Override
    public List<ChatLieu> getAll() {
        return chatLieuRepository.findAll();
    }

    @Override
    public String create(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("chatLieu", chatLieu);
            return "admin/san-pham/chat-lieu/view";
        }

        if (chatLieu.getMa() == null || chatLieu.getMa().trim().isEmpty()) {
            chatLieu.setMa(generateRandomCode());
        }

        for (ChatLieu cg: chatLieuRepository.findAll()) {
            if (chatLieu.getMa().equalsIgnoreCase(cg.getMa())) {
                model.addAttribute("errorMa", "Mã Chất Liệu đã tồn tại trong CSDL!");
                model.addAttribute("chatLieu", chatLieu);
                return "admin/san-pham/chat-lieu/view";
            }
        }

        chatLieu.setNguoiTao("Admin");
        chatLieu.setNgayTao(new Date());
        chatLieu.setTrangThai("Đang hoạt động");
        chatLieuRepository.save(chatLieu);
        return "redirect:/admin/san-pham/chat-lieu/view";
    }

    @Override
    public String update(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("chatLieu", chatLieu);
            return "admin/san-pham/chat-lieu/view-update";
        }

        ChatLieu currentChatLieu = chatLieuRepository.getReferenceById(chatLieu.getId());
        if (currentChatLieu == null) {
            model.addAttribute("errorMa", "Mã Chất Liệu không tồn tại!");
            model.addAttribute("chatLieu", chatLieu);
            return "admin/san-pham/chat-lieu/view-update";
        }

        if (chatLieu.getMa() == null || chatLieu.getMa().trim().isEmpty()) {
            chatLieu.setMa(generateRandomCode());
        }

        for (ChatLieu dm :chatLieuRepository.findAll()) {
            if (!dm.getId().equals(chatLieu.getId()) && chatLieu.getMa().equalsIgnoreCase(dm.getMa())) {
                model.addAttribute("errorMa", "Mã Chất Liệu đã tồn tại trong CSDL!");
                model.addAttribute("chatLieu", chatLieu);
                return "admin/san-pham/chat-lieu/view-update";
            }
        }

        chatLieu.setNguoiSua("Admin");
        chatLieu.setNgaySua(new Date());
        chatLieuRepository.save(chatLieu);
        return "redirect:/admin/san-pham/chat-lieu/view";
    }

    @Override
    public String delete(int id) {
        ChatLieu chatLieu = chatLieuRepository.findById(id).orElse(null);
        if (chatLieu != null) {
            chatLieu.setNgaySua(new Date());
            chatLieu.setDeleted(1);
            chatLieuRepository.save(chatLieu);
        }
        return "redirect:/admin/san-pham/co-giay/view";
    }

    @Override
    public ChatLieu findById(int id) {
        return chatLieuRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ChatLieu> getAllActive(Pageable pageable) {
        return chatLieuRepository.getAllActiveChatLieu(pageable);
    }

    @Override
    public void toggleStatus(int id) {
        ChatLieu chatLieu = chatLieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chất Liệu không tồn tại"));

        if ("Đang hoạt động".equals(chatLieu.getTrangThai())) {
            chatLieu.setTrangThai("Ngừng hoạt động");
        } else if ("Ngừng hoạt động".equals(chatLieu.getTrangThai())) {
            chatLieu.setTrangThai("Đang hoạt động");
        }

        chatLieu.setNguoiSua("Admin");
        chatLieu.setNgaySua(new Date());
        chatLieuRepository.save(chatLieu);
    }

    @Override
    public Page<ChatLieu> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable) {
        return chatLieuRepository.listFilter(ma, trangThai1, trangThai2, pageable);
    }

    @Override
    public List<ChatLieu> getListActiveChatLieus() {
        return chatLieuRepository.getListActiveChatLieus();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<ChatLieu> chatLieuList = chatLieuRepository.findAll();

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
            for (ChatLieu chatLieu : chatLieuList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(chatLieu.getId());
                row.createCell(1).setCellValue(chatLieu.getMa());
                row.createCell(2).setCellValue(chatLieu.getTen());
                row.createCell(3).setCellValue(chatLieu.getTrangThai());
//                row.createCell(4).setCellValue(chatLieu.getNgayTao().toString());
//                row.createCell(5).setCellValue(chatLieu.getNgaySua().toString());
//                row.createCell(6).setCellValue(chatLieu.getNguoiTao());
//                row.createCell(7).setCellValue(chatLieu.getNguoiSua());
//                row.createCell(8).setCellValue(chatLieu.getDeleted());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<ChatLieu> importExcel(MultipartFile file) throws IOException {
        List<ChatLieu> chatLieuList = new ArrayList<>();

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

                ChatLieu chatLieu = new ChatLieu();
                chatLieu.setMa(currentRow.getCell(0).getStringCellValue());
                chatLieu.setTrangThai(currentRow.getCell(1).getStringCellValue());
                chatLieu.setTen(currentRow.getCell(2).getStringCellValue());
                chatLieu.setNgayTao(currentRow.getCell(3).getDateCellValue());
                chatLieu.setNgaySua(currentRow.getCell(4).getDateCellValue());
                chatLieu.setNguoiTao(currentRow.getCell(5).getStringCellValue());
                chatLieu.setNguoiSua(currentRow.getCell(6).getStringCellValue());
                chatLieu.setDeleted((int) currentRow.getCell(7).getNumericCellValue());

                chatLieuList.add(chatLieu);
            }
        }

        return chatLieuList;
    }

}
