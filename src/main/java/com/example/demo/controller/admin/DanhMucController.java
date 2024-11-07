package com.example.demo.controller.admin;

import com.example.demo.entity.DanhMuc;
import com.example.demo.repository.admin.DanhMucRepository;
import com.example.demo.service.admin.DanhMucService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("admin/san-pham/danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        Pageable pageable = PageRequest.of(0, pagesize);
        Page<DanhMuc> DanhMucPage = danhMucService.getAllActive(pageable);
        model.addAttribute("danhMucLists", DanhMucPage);
        model.addAttribute("danhMuc", new DanhMuc());
        return "admin/san-pham/danh-muc/view";
    }

    @GetMapping("search")
    public String search(Model model,
                         @RequestParam(defaultValue = "") String nameSearch,
                         @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<DanhMuc> danhMucPage = danhMucRepository.search("%" +nameSearch+ "%",pageable);
        model.addAttribute("danhMucLists", danhMucPage);
        return "admin/san-pham/danh-muc/view";
    }

    @GetMapping("create")
    public String createForm(Model model) {
        model.addAttribute("danhMuc", new DanhMuc());
        return "admin/san-pham/danh-muc/create";
    }

    @PostMapping("create")
    public String create(@Valid DanhMuc danhMuc, BindingResult bindingResult, Model model) {
        return danhMucService.create(danhMuc, bindingResult, model);
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("danhMuc", danhMucService.findById(id));
        return "admin/san-pham/danh-muc/detail";
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("danhMuc", danhMucService.findById(id));
        return "admin/san-pham/danh-muc/view-update";
    }

    @PostMapping("update/{id}")
    public String update(@Valid DanhMuc danhMuc, BindingResult bindingResult, Model model) {
        return danhMucService.update(danhMuc, bindingResult, model);
    }

    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return danhMucService.delete(id);
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
//        Page<DanhMuc> danhMucPage = danhMucService.listFilter(ma, trangThai1, trangThai2, pageable);
//        model.addAttribute("danhMucLists", danhMucPage);
//        System.out.println("List danh muc: " + danhMucPage.getTotalElements());
//        return "admin/san-pham/danh-muc/search";
//    }
}
