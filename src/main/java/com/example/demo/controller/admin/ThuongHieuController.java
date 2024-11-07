package com.example.demo.controller.admin;

import com.example.demo.entity.ThuongHieu;
import com.example.demo.repository.admin.ThuongHieuRepository;
import com.example.demo.service.admin.ThuongHieuService;
import com.example.demo.service.admin.impl.ThuongHieuImpl;
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
@RequestMapping("admin/san-pham/thuong-hieu")
public class ThuongHieuController {
    @Autowired
    private ThuongHieuService thuongHieuService;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private ThuongHieuImpl thuongHieuImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pageThuongHieu = 10;
        Pageable pageable = PageRequest.of(0, pageThuongHieu);
        Page<ThuongHieu> thuongHieuPage = thuongHieuService.getAllActive(pageable);
        model.addAttribute("thuongHieuLists", thuongHieuPage);
        model.addAttribute("thuongHieu", new ThuongHieu());
        return "admin/san-pham/thuong-hieu/view";
    }
    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<ThuongHieu> thuongHieuPage = thuongHieuRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("thuongHieuLists", thuongHieuPage);
        return "admin/san-pham/thuong-hieu/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("thuongHieu", new ThuongHieu());
        return "admin/san-pham/thuong-hieu/create";
    }

    @PostMapping("create")
    public String create(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model) {
        return thuongHieuService.create(thuongHieu, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("thuongHieu", thuongHieuService.findById(id));
        return "admin/san-pham/thuong-hieu/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("thuongHieu", thuongHieuService.findById(id));
        return "admin/san-pham/thuong-hieu/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid ThuongHieu thuongHieu, BindingResult bindingResult, Model model) {
        return thuongHieuService.update(thuongHieu, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return thuongHieuService.delete(id);
    }


    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "thuonghieu.xlsx";
        ByteArrayInputStream actualData = thuongHieuImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<ThuongHieu> thuongHieuList = thuongHieuImpl.importExcel(file);
            thuongHieuRepository.saveAll(thuongHieuList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/thuong-hieu/view";
    }
//    @GetMapping("/search")
//    public String viewSize(
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
//        Page<ThuongHieu> thuongHieuPage = thuongHieuService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("thuongHieuLists", thuongHieuPage);
//        System.out.println("List thuongHieu: " + thuongHieuPage.getTotalElements());
//        return "admin/san-pham/thuong-hieu/search";
//    }
}
