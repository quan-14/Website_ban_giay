package com.example.demo.controller.admin;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.MauSac;
import com.example.demo.repository.admin.ChatLieuRepository;
import com.example.demo.service.admin.ChatLieuService;
import com.example.demo.service.admin.impl.ChatLieuImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/san-pham/chat-lieu")
public class ChatLieuController {
    @Autowired
    private ChatLieuService chatLieuService;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    private ChatLieuImpl chatLieuImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        Pageable pageable = PageRequest.of(0, pagesize);
        Page<ChatLieu> chatLieuPage = chatLieuService.getAllActive(pageable);
        model.addAttribute("chatLieuLists", chatLieuPage);
        model.addAttribute("chatLieu", new ChatLieu());
        return "admin/san-pham/chat-lieu/view";
    }
    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<ChatLieu> chatLieuPage = chatLieuRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("chatLieuLists", chatLieuPage);
        return "admin/san-pham/chat-lieu/view";
    }
    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("chatLieu", new ChatLieu());
        return "admin/san-pham/chat-lieu/create";
    }

    @PostMapping("create")
    public String create(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model) {
        return chatLieuService.create(chatLieu, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("chatLieu", chatLieuService.findById(id));
        return "admin/san-pham/chat-lieu/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("chatLieu", chatLieuService.findById(id));
        return "admin/san-pham/chat-lieu/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid ChatLieu chatLieu, BindingResult bindingResult, Model model) {
        return chatLieuService.update(chatLieu, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return chatLieuService.delete(id);
    }

    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "chatlieu.xlsx";
        ByteArrayInputStream actualData = chatLieuImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<ChatLieu> chatLieuList = chatLieuImpl.importExcel(file);
            chatLieuRepository.saveAll(chatLieuList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/chat-lieu/view";
    }


//    @GetMapping("/search")
//    public String viewChatLieu(
//            @RequestParam(defaultValue = "") String ma,
//            @RequestParam(defaultValue = "") String trangThai,
//            Pageable pageable,
//            Model model) {
//        String trangThai1 = "";
//        String trangThai2 = "";
//        if (trangThai.equals("")) {
//            trangThai1 = "Đang hoạt động";
//            trangThai2 = "Ngừng hoạt động";
//        } else if (trangThai.equals("Đang hoạt động")) {
//            trangThai1 = "Đang hoạt động";
//            trangThai2 = "Đang hoạt động";
//        } else {
//            trangThai1 = "Ngừng hoạt động";
//            trangThai2 = "Ngừng hoạt động";
//        }
//        Page<ChatLieu> chatLieuPage = chatLieuService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("chatLieuLists", chatLieuPage);
//        System.out.println("List chatLieu: " + chatLieuPage.getTotalElements());
//        return "admin/san-pham/chat-lieu/search";
    //}
}
