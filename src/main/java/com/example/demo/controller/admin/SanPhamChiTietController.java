package com.example.demo.controller.admin;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.CoGiay;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.NhaSanXuat;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.entity.XuatXu;
import com.example.demo.service.admin.ChatLieuService;
import com.example.demo.service.admin.MauSacService;
import com.example.demo.service.admin.NhaSanXuatService;
import com.example.demo.service.admin.SanPhamChiTietService;
import com.example.demo.service.admin.SanPhamService;
import com.example.demo.service.admin.XuatXuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RequestMapping("admin/san-pham/san-pham-chi-tiet")

@Repository
public class SanPhamChiTietController {

    @Autowired
    SanPhamService sanPhamService;

    @Autowired
    MauSacService mauSacService;

    @Autowired
    XuatXuService xuatXuService;

    @Autowired
    NhaSanXuatService nhaSanXuatService;

    @Autowired
    ChatLieuService chatLieuService;

    @Autowired
    SanPhamChiTietService sanPhamChiTietService;

    @ModelAttribute("sanPhamLists")
    public List<SanPham> getSanPhamLists() {
        return sanPhamService.getAllActiveListSanPhams();
    }

    @ModelAttribute("mauSacLists")
    public List<MauSac> getMauSacLists() {
        return mauSacService.getListActiveMauSacs();
    }

    @ModelAttribute("xuatXuLists")
    public List<XuatXu> getXuatXuLists() {
        return xuatXuService.getListActiveXuatXus();
    }

    @ModelAttribute("nhaSanXuatLists")
    public List<NhaSanXuat> getNhaSanXuatLists() {
        return nhaSanXuatService.getListActiveNhaSanXuats();
    }

    @ModelAttribute("chatLieuLists")
    public List<ChatLieu> getChatLieuLists() {
        return chatLieuService.getListActiveChatLieus();
    }

    //    @GetMapping("view/{id}")
    //    public String view(@PathVariable Integer id, Model model, @RequestParam("page") Optional<Integer> page) {
    //        int checkpage = page.orElse(0);
    //        int pagesize = 3;
    //        checkpage = Math.max(checkpage, 0);
    //        Pageable pageable = PageRequest.of(checkpage, pagesize);
    //        Page<SanPhamChiTiet> sanPhamChiTietPage = sanPhamChiTietService.getPageSPCT(id, pageable);
    //        model.addAttribute("sanPhamChiTietLists", sanPhamChiTietPage);
    //        return "admin/san-pham/san-pham-chi-tiet/view";
    //    }

        @GetMapping("view/{id}")
    public String viewChiTiet(@PathVariable Integer id, Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<SanPhamChiTiet> sanPhamChiTietPage = sanPhamChiTietService.getPageSPCT(id, pageable);
        model.addAttribute("sanPham", sanPhamService.findById(id));
        String spToString = sanPhamService.findById(id).toString();
        int pageSPCTSize = (int) sanPhamChiTietPage.getTotalElements();
        model.addAttribute("sanPhamChiTietLists", sanPhamChiTietPage);
        return "admin/san-pham/san-pham-chi-tiet/view";
    }
//    @GetMapping("view/{id}")
//    public String viewChiTiet(@PathVariable("id") Integer id, Model model) {
//
//        List<SanPhamChiTiet> sanPhamChiTietPage = sanPhamChiTietService.getListSPCT(id);
//        model.addAttribute("sanPham", sanPhamService.findById(id));
//
//        model.addAttribute("sanPhamChiTietLists", sanPhamChiTietPage);
//        return "admin/san-pham/san-pham-chi-tiet/view";
//    }
}
