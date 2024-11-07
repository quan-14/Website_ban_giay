package com.example.demo.controller.admin;

import com.example.demo.entity.XuatXu;
import com.example.demo.repository.admin.ThuongHieuRepository;
import com.example.demo.repository.admin.XuatXuRepository;
import com.example.demo.service.admin.XuatXuService;
import com.example.demo.service.admin.impl.XuatXuImpl;
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
@RequestMapping("admin/san-pham/xuat-xu")
public class XuatXuController {

    @Autowired
    private XuatXuService xuatXuService;

    @Autowired
    private XuatXuRepository xuatXuRepository;

    @Autowired
    private XuatXuImpl xuatXuImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pageXuatXu = 10;
        Pageable pageable = PageRequest.of(0, pageXuatXu);
        Page<XuatXu> xuatXuPage = xuatXuService.getAllActive(pageable);
        model.addAttribute("xuatXuLists", xuatXuPage);
        model.addAttribute("xuatXu", new XuatXu());
        return "admin/san-pham/xuat-xu/view";
    }
    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<XuatXu> xuatXuPage = xuatXuRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("xuatXuLists", xuatXuPage);
        return "admin/san-pham/xuat-xu/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("xuatXu", new XuatXu());
        return "admin/san-pham/xuat-xu/create";
    }

    @PostMapping("create")
    public String create(@Valid XuatXu xuatXu, BindingResult bindingResult, Model model) {
        return xuatXuService.create(xuatXu, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("xuatXu", xuatXuService.findById(id));
        return "admin/san-pham/xuat-xu/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("xuatXu", xuatXuService.findById(id));
        return "admin/san-pham/xuat-xu/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid XuatXu xuatXu, BindingResult bindingResult, Model model) {
        return xuatXuService.update(xuatXu, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return xuatXuService.delete(id);
    }


    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "xuatxu.xlsx";
        ByteArrayInputStream actualData = xuatXuImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<XuatXu> xuatXuList = xuatXuImpl.importExcel(file);
            xuatXuRepository.saveAll(xuatXuList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/xuat-xu/view";
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
//        Page<XuatXu> xuatXuPage = xuatXuService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("xuatXuLists", xuatXuPage);
//        System.out.println("List xuatXu: " + xuatXuPage.getTotalElements());
//        return "admin/san-pham/xuat-xu/search";
//    }
}
