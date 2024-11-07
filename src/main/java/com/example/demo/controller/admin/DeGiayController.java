package com.example.demo.controller.admin;

import com.example.demo.entity.DeGiay;
import com.example.demo.repository.admin.DanhMucRepository;
import com.example.demo.repository.admin.DeGiayRepository;
import com.example.demo.service.admin.DeGiayService;
import com.example.demo.service.admin.impl.DeGiayImpl;
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
@RequestMapping("admin/san-pham/de-giay")
public class DeGiayController {
    @Autowired
    private DeGiayService deGiayService;

    @Autowired
    private DeGiayRepository deGiayRepository;

    @Autowired
    private DeGiayImpl deGiayImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        Pageable pageable = PageRequest.of(0, pagesize);
        Page<DeGiay> DeGiayPage = deGiayService.getAllActive(pageable);
        model.addAttribute("deGiayLists", DeGiayPage);
        model.addAttribute("deGiay", new DeGiay());
        return "admin/san-pham/de-giay/view";
    }

    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<DeGiay> deGiayPage = deGiayRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("deGiayLists", deGiayPage);
        return "admin/san-pham/de-giay/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("deGiay", new DeGiay());
        return "admin/san-pham/de-giay/create";
    }

    @PostMapping("create")
    public String create(@Valid DeGiay deGiay, BindingResult bindingResult, Model model) {
        return deGiayService.create(deGiay, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("deGiay", deGiayService.findById(id));
        return "admin/san-pham/de-giay/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("deGiay", deGiayService.findById(id));
        return "admin/san-pham/de-giay/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid DeGiay deGiay, BindingResult bindingResult, Model model) {
        return deGiayService.update(deGiay, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return deGiayService.delete(id);
    }

    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "degiay.xlsx";
        ByteArrayInputStream actualData = deGiayImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<DeGiay> deGiayList = deGiayImpl.importExcel(file);
            deGiayRepository.saveAll(deGiayList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/de-giay/view";
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
//        Page<DeGiay> deGiayPage = deGiayService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("deGiayLists", deGiayPage);
//        System.out.println("List De Giay: " + deGiayPage.getTotalElements());
//        return "admin/san-pham/de-giay/search";
//    }
}
