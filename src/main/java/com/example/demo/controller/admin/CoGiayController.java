package com.example.demo.controller.admin;

import com.example.demo.entity.CoGiay;
import com.example.demo.repository.admin.CoGiayRepository;
import com.example.demo.service.admin.CoGiayService;
import com.example.demo.service.admin.impl.CoGiayImpl;
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
@RequestMapping("admin/san-pham/co-giay")
public class CoGiayController {

    @Autowired
    private CoGiayService coGiayService;

    @Autowired
    private CoGiayRepository coGiayRepository;

    @Autowired
    private CoGiayImpl coGiayImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        Pageable pageable = PageRequest.of(0, pagesize);
        Page<CoGiay> CoGiayPage = coGiayService.getAllActive(pageable);
        model.addAttribute("coGiayLists", CoGiayPage);
        model.addAttribute("coGiay", new CoGiay());
        return "admin/san-pham/co-giay/view";
    }

    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<CoGiay> coGiayPage = coGiayRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("coGiayLists", coGiayPage);
        return "admin/san-pham/co-giay/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("coGiay", new CoGiay());
        return "admin/san-pham/co-giay/create";
    }

    @PostMapping("create")
    public String create(@Valid CoGiay coGiay, BindingResult bindingResult, Model model) {
        return coGiayService.create(coGiay, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("coGiay", coGiayService.findById(id));
        return "admin/san-pham/co-giay/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("coGiay", coGiayService.findById(id));
        return "admin/san-pham/co-giay/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid CoGiay coGiay, BindingResult bindingResult, Model model) {
        return coGiayService.update(coGiay, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return coGiayService.delete(id);
    }

    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "cogiay.xlsx";
        ByteArrayInputStream actualData = coGiayImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<CoGiay> coGiayList = coGiayImpl.importExcel(file);
            coGiayRepository.saveAll(coGiayList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/co-giay/view";
    }


//    @GetMapping("/search")
//    public String viewCoGiay(
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
//        Page<CoGiay> coGiayPage = coGiayService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("coGiayLists", coGiayPage);
//        System.out.println("List coGiay: " + coGiayPage.getTotalElements());
//        return "admin/san-pham/co-giay/search";
//    }
}
