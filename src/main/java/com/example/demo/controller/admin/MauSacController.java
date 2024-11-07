package com.example.demo.controller.admin;

import com.example.demo.entity.MauSac;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.admin.MauSacRepository;
import com.example.demo.service.admin.MauSacService;
import com.example.demo.service.admin.impl.MauSacImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.core.io.Resource

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/san-pham/mau-sac")
public class MauSacController {

    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private MauSacImpl mauSacImpl;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        Pageable pageable = PageRequest.of(0, pagesize);
        Page<MauSac> mauSacPage = mauSacService.getAllActive(pageable);
        model.addAttribute("mauSacLists", mauSacPage);
        model.addAttribute("mauSac", new MauSac());
        return "admin/san-pham/mau-sac/view";
    }

    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<MauSac> mauSacPage = mauSacRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("mauSacLists", mauSacPage);
        return "admin/san-pham/mau-sac/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("mauSac", new MauSac());
        return "admin/san-pham/mau-sac/create";
    }

    @PostMapping("create")
    public String create(@Valid MauSac mauSac, BindingResult bindingResult, Model model) {
        return mauSacService.create(mauSac, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("mauSac", mauSacService.findById(id));
        return "admin/san-pham/mau-sac/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("mauSac", mauSacService.findById(id));
        return "admin/san-pham/mau-sac/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid MauSac mauSac, BindingResult bindingResult, Model model) {
        return mauSacService.update(mauSac, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return mauSacService.delete(id);
    }


    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "mausac.xlsx";
        ByteArrayInputStream actualData = mauSacImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<MauSac> mauSacList = mauSacImpl.importExcel(file);
            mauSacRepository.saveAll(mauSacList);
            model.addAttribute("message", "Tải lên thành công!");
        } catch (IOException e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/mau-sac/view";
    }

//    @PostMapping("/toggle-status/{id}")
//    public String toggleStatus(@PathVariable int id) {
//        mauSacService.toggleStatus(id);
//        return "redirect:/admin/san-pham/mau-sac/view";
//    }

//    @GetMapping("/search")
//    public String viewMauSac(
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
//        Page<MauSac> mauSacPage = mauSacService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("mauSacLists", mauSacPage);
//        System.out.println("List size: " + mauSacPage.getTotalElements());
//        return "admin/san-pham/mau-sac/search";
//    }


}
