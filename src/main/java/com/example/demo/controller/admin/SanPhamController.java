package com.example.demo.controller.admin;

import com.example.demo.entity.*;
import com.example.demo.repository.admin.SanPhamChiTietRepository;
import com.example.demo.repository.admin.SanPhamRepository;
import com.example.demo.service.admin.*;
import com.example.demo.service.admin.impl.SanPhamImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/san-pham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private CoGiayService coGiayService;

    @Autowired
    private ThuongHieuService thuongHieuService;

    @Autowired
    private DeGiayService deGiayService;

    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamImpl sanPhamImpl;
    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private SizeService sizeService;
    @ModelAttribute("coGiayLists")
    public List<CoGiay> getCoGiayLists() {
        return coGiayService.getListActiveCoGiays();
    }

    @ModelAttribute("danhMucLists")
    public List<DanhMuc> getDanhMucLists() {
        return danhMucService.getListActiveDanhMucs();
    }

    @ModelAttribute("thuongHieuLists")
    public List<ThuongHieu> getThuongHieuLists() {
        return thuongHieuService.getListActiveThuongHieus();
    }

    @ModelAttribute("deGiayLists")
    public List<DeGiay> getDeGiayLists() {
        return deGiayService.getListActiveDeGiays();
    }

    @ModelAttribute("colors")
    public List<MauSac> getMauSac() {
        return mauSacService.getAll();
    }

    @ModelAttribute("sizes")
    public List<Size> getSize() {
        return sizeService.getAll();
    }

    @GetMapping("view")
    public String view(Model model, @RequestParam("page") Optional<Integer> page,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String trangThai) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        Sort sort = Sort.by("id").descending();

        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize,sort);
        Page<SanPham> sanPhamPage = sanPhamService.getAllSanPhams(search, trangThai,pageable);
        model.addAttribute("sanPhamLists", sanPhamPage);
        return "admin/san-pham/view";
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("sanPham", sanPhamService.findById(id));
        return "admin/san-pham/detail";
    }


    @GetMapping("create")
    public String create(Model model) {
        SanPham sanPham = new SanPham();
        sanPham.setMa(sanPhamService.generateRandomCodeSP());
        model.addAttribute("sanPham", sanPham);

        return "admin/san-pham/create";
    }
    @GetMapping("create/ren-ctsp")
    public String renctsp(Model model,@RequestParam Integer idSP ) {

        List<SanPhamChiTiet> renSPCT = sanPhamChiTietRepository.renSPCT(idSP);
        SanPham sanPham = sanPhamRepository.getReferenceById(idSP);
        model.addAttribute("tenSP",sanPham);
        model.addAttribute("renctsp", renSPCT);

        return "admin/san-pham/view-ren-ctsp";
    }
    @PostMapping("create")
    public String create(@Valid SanPham sanPham, BindingResult bindingResult,
                         @RequestParam("selectedColors") List<String> selectedColors,
                         @RequestParam("selectedSizes") List<String> selectedSizes,
                         Model model) {
        return sanPhamService.create(sanPham, bindingResult, selectedColors, selectedSizes, model);
    }

    @GetMapping("view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("sanPham", sanPhamService.findById(id));
        return "admin/san-pham/view-update";
    }
    @GetMapping("delete-renctsp/{id}")

    public String delterenctsp(@PathVariable Integer id, Model model) {

        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
        List<SanPhamChiTiet> renSPCT = sanPhamChiTietRepository.renSPCT(sanPhamChiTiet.getSanPham().getId());
        model.addAttribute("renctsp", renSPCT);
        if (sanPhamChiTietRepository.existsById(id)) {
            sanPhamChiTietRepository.deleteById(id);
        }

        return "admin/san-pham/view-ren-ctsp";
    }
    @PostMapping("update/{id}")
    public String update(@Valid SanPham sanPham, BindingResult bindingResult, Model model) {
        return sanPhamService.update(sanPham, bindingResult, model);
    }
    @PostMapping("/done-renctsp")
    public ResponseEntity<String> saveTableData(@RequestBody List<SanPhamChiTietDTO> sanPhamList) {
        try {
            SanPhamChiTiet sanPhamChiTiet;
            for (SanPhamChiTietDTO sanPham : sanPhamList) {
                sanPhamChiTiet = new SanPhamChiTiet();
                if(sanPhamChiTietRepository.existsById(sanPham.getIdCtsp())){
                    sanPhamChiTiet = sanPhamChiTietRepository.getReferenceById(sanPham.getIdCtsp());
                    sanPhamChiTiet.setDonGia(sanPham.getDonGia());
                    sanPhamChiTiet.setSoLuong(sanPham.getSoLuong());

                    sanPhamChiTietRepository.save(sanPhamChiTiet);
                }
            }
            return ResponseEntity.ok("Lưu thành công!");
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để kiểm tra chi tiết
            return ResponseEntity.badRequest().body("Đã xảy ra lỗi!");
        }
    }
    @GetMapping("remove/{id}")
    public String remove(@PathVariable Integer id) {
        return sanPhamService.delete(id);
    }
    @PostMapping("san-pham-chi-tiet/update")
    public String updateSanPhamChiTiet(@RequestParam("id") Integer id,
                                       @RequestParam("soLuong") Integer soLuong,
                                       @RequestParam("donGia") Double donGia
    ) {

        // Logic để cập nhật số lượng và đơn giá vào cơ sở dữ liệu
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.getReferenceById(id);
        sanPhamChiTiet.setSoLuong(soLuong);
        sanPhamChiTiet.setDonGia(donGia);
        sanPhamChiTietRepository.save(sanPhamChiTiet);



        return "redirect:/admin/san-pham/san-pham-chi-tiet/view/" + id; // Redirect về trang danh sách sau khi cập nhật
    }
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable int id) {
        sanPhamService.toggleStatus(id);
        return "redirect:/admin/san-pham/view";
    }

    @GetMapping("san-pham-chi-tiet/view/{id}")
    public String viewChiTiet(@PathVariable Integer id,
                              @RequestParam(required = false) Integer chatLieuId,
                              @RequestParam(required = false) Integer coGiayId,
                              @RequestParam(required = false) Integer deGiayId,
                              @RequestParam(required = false) Integer thuongHieuId,
                              @RequestParam(required = false) String trangThai,
                              @RequestParam(required = false) Integer mauSacId,
                              @RequestParam(required = false) Integer kichCoId,
                              @RequestParam(required = false) String khoanggia,
                              Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 10;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
//        Page<SanPhamChiTiet> sanPhamChiTietPage = sanPhamChiTietService.getPageSPCT(id, pageable);
//        model.addAttribute("sanPhamChiTietLists", sanPhamChiTietPage);
        Page<SanPhamChiTiet> sanPhamChiTietPage = sanPhamChiTietService.findProductsByCriteria(chatLieuId, coGiayId, deGiayId, thuongHieuId, trangThai, mauSacId, kichCoId, id, khoanggia, pageable);
        model.addAttribute("sanPhamChiTietLists", sanPhamChiTietPage);
        return "admin/san-pham/san-pham-chi-tiet/view";
    }
    @GetMapping("/inexcel")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        String fileName = "sanpham.xlsx";
        ByteArrayInputStream actualData = sanPhamImpl.exportToExcel();
        InputStreamResource file = new InputStreamResource(actualData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }


    @PostMapping("san-pham-chi-tiet/ren-spct")
    public String renSPCT(@RequestParam("selectedColors") List<MauSac> selectedColors,
                          @RequestParam("selectedSizes") List<Size> selectedSizes,
                          Model model) {

        System.out.println(selectedSizes);
        System.out.println(selectedColors);
        return "redirect:/admin/san-pham/create";
    }

}