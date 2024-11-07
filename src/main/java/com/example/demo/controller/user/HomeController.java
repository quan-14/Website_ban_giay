package com.example.demo.controller.user;

import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.repository.admin.HinhAnhRepository;
import com.example.demo.repository.admin.SanPhamChiTietRepository;
import com.example.demo.repository.admin.SanPhamRepository;
import com.example.demo.repository.user.HomeRepository;
import com.example.demo.service.admin.SanPhamService;
import com.example.demo.service.user.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user/sanpham")
public class HomeController {

    @Autowired
    HomeRepository homeRepository;
    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    HomeService homeService;
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    HinhAnhRepository hinhAnhRepository;

    public String viewSanPhamChiTiet(Model model) {
        List<SanPhamChiTiet> list = homeService.getUniqueSanPhamChiTiets();
        model.addAttribute("listsp", list);
        return "user/san-pham/sanpham";
    }

    @GetMapping("/home")
    public String viewHome(Model model) {
        List<SanPhamChiTiet> list = homeService.getTop5UniqueSanPhamChiTiets();
        model.addAttribute("list", list);
        List<SanPhamChiTiet> listsp = homeService.getTop5ProductSanPhamChiTiets();
        model.addAttribute("listsplist", listsp);
        Map<Integer, String> hinhAnhMap = new HashMap<>();
        for (SanPhamChiTiet spchiTiet : listsp) {
            List<String> hinhanhht = hinhAnhRepository.hinhAnhDuongDan(spchiTiet.getId());
            if (!hinhanhht.isEmpty()) {
                hinhAnhMap.put(spchiTiet.getId(), hinhanhht.get(0)); // Lấy ảnh đầu tiên
            } else {
                hinhAnhMap.put(spchiTiet.getId(), "default-image.jpg"); // Ảnh mặc định nếu không có
            }
        }
        model.addAttribute("hinhAnhMap", hinhAnhMap);
        return "user/home";
    }


    @GetMapping("/san-pham/detail/{id}")
    public String viewSanPhamChiTiet(@PathVariable("id") Integer id, Model model) {
        SanPhamChiTiet sanPhamCT = homeRepository.findSanPhamChiTietById(id);
        SanPham sanPham = sanPhamRepository.getReferenceById(sanPhamCT.getSanPham().getId());
        List<SanPhamChiTiet> lstsanphamCT = sanPhamChiTietRepository.findBySanPham(sanPhamCT.getSanPham().getId());
        List<SanPhamChiTiet> lstsanphammausac = new ArrayList<>();
        List<SanPhamChiTiet> sanPhamMoi = new ArrayList<>();
        for (SanPhamChiTiet spct : lstsanphamCT) {
            boolean isUniqueColor = true;
            for (SanPhamChiTiet spct1 : sanPhamMoi) {
                if (spct1.getMauSac().equals(spct.getMauSac())) {
                    isUniqueColor = false;
                    break;
                }
            }
            if (isUniqueColor) {
                sanPhamMoi.add(spct);
            }
        }

// Sau khi hoàn thành vòng lặp, thêm tất cả sản phẩm mới vào lstsanphammausac
        lstsanphammausac.addAll(sanPhamMoi);
        List<SanPhamChiTiet> lstsanphamsize = sanPhamChiTietRepository.findspctSizeBySanPhamandmausac(sanPhamCT.getSanPham().getId(),sanPhamCT.getMauSac().getId());
        model.addAttribute("lstsanphamsize", lstsanphamsize);
        model.addAttribute("lstsanphammausac", lstsanphammausac);

        if (sanPhamCT == null) {
            model.addAttribute("errorMessage", "Sản phẩm không tồn tại");
            return "error";
        }
        List<String> lsthinhAnh = hinhAnhRepository.hinhAnhDuongDan(id);
        String formattedDonGia = String.format("%,.0f", sanPhamCT.getDonGia());
        model.addAttribute("sanPhamCT", sanPhamCT);
        model.addAttribute("formattedDonGia", formattedDonGia);
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("lstsanphamCT", lstsanphamCT);
        for (int i = 0; i < lsthinhAnh.size(); i++) {
            model.addAttribute("lsthinhAnh"+i, lsthinhAnh.get(i));
        }
        return "user/chi-tiet-san-pham/chitietsanpham";
    }


    @GetMapping("/search")
    public String search(Model model, @RequestParam(defaultValue = "") String nameSearch){
        List<SanPhamChiTiet> list = homeRepository.search("%"+nameSearch+"%");
        model.addAttribute("listsp",list);
        return "user/san-pham/sanpham";
    }
}
