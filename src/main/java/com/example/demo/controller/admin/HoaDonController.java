package com.example.demo.controller.admin;


import com.example.demo.entity.*;
import com.example.demo.repository.admin.*;
import com.example.demo.service.admin.EmailService;
import com.example.demo.service.admin.impl.GhnService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/admin")
public class HoaDonController {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @Autowired
    LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired
    HoaDonHinhThucThanhToanRepository hoaDonHinhThucThanhToanRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    HinhAnhRepository hinhAnhRepository;
    @Autowired
    private GhnService ghnService;

    @GetMapping("/hoadon/hien-thi")
    public String hienThiHoaDon(Model model, @RequestParam(name = "page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<HoaDon> hd = hoaDonRepository.listkhongtaomoi(pageable);
//        List<HoaDon> hd = hoaDonRepository.findAll();
        model.addAttribute("hoadon", hd);
        return "admin/hoa-don/hien-thi";
    }

    @GetMapping("/hoadon/timkiem")
    public String hienThitimkiem(Model model, @RequestParam(defaultValue = "") String search1
            , @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date1
            , @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date2
            , @RequestParam(required = false) Integer loaihoadontim
            , @RequestParam(defaultValue = "") String trangthaihd
            , @RequestParam(name = "page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Calendar calendar = Calendar.getInstance();
        // Nếu date2 không được cung cấp, đặt nó là ngày hiện tại
        if (date2 == null) {
            date2 = calendar.getTime();
        }

        // Nếu date1 không được cung cấp, đặt nó là ngày này của năm ngoái
        if (date1 == null) {
            calendar.add(Calendar.YEAR, -1);
            date1 = calendar.getTime();
        }
        Page<HoaDon> hd = hoaDonRepository.searchHoaDon(search1.trim(), loaihoadontim,trangthaihd, date1, date2, pageable);
//        List<HoaDon> hd = hoaDonRepository.findAll();
        model.addAttribute("hoadon", hd);
        return "admin/hoa-don/hien-thi";
    }

    @PostMapping("/hoadon/update/{id}")
    public String suahoadon(@ModelAttribute("hoadon") HoaDon hoaDon
            , @PathVariable("id") Integer id
            , Model model
            , @RequestParam("provinceId") int provinceId
            , @RequestParam("districtId") int districtId
            , @RequestParam("wardCode") String wardCode
            , @RequestParam(name = "page") Optional<Integer> page
            ) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        List<HoaDonChiTiet> hdctlist = hoaDonChiTietRepository.findallbyhoadon(id);
        int tongkhoiluonghd = 0;
        for (int i = 0; i < hdctlist.size(); i++) {
            tongkhoiluonghd += (hdctlist.get(i).getSanPhamChiTiet().getKhoiLuong() * hdctlist.get(i).getSoLuong());
        }
        Province province = ghnService.getProvinceByID(provinceId);
        Disctrict disctrict = ghnService.getDistrictByID(districtId,provinceId);
        Ward ward = ghnService.getWardByID(wardCode,districtId);
        hd.setTenNguoiNhan(hoaDon.getTenNguoiNhan());
        hd.setNgaySua(new Date());
        hd.setTinhThanhPho(province.getProvinceName());
        hd.setQuanHuyen(disctrict.getDistrictName());
        hd.setPhuongXa(ward.getWardName());
        hd.setPhiShip(ghnService.getShippingFee(districtId,wardCode,tongkhoiluonghd));
        hoaDonRepository.save(hd);
        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepository.findAllByHoaDon_Id(id);
        model.addAttribute("lichsuhoadon", lichSuHoaDonList);
        return "redirect:/admin/hoadon/view-hoadon/" + id;
    }

    //    @PostMapping ("/hoadon/add")
//    public String addhoadon(Model model,@ModelAttribute("hoadon")HoaDon hoaDon){
//        if (hoaDon.getPhieuGiamGia() == null) {
//            hoaDon.setPhieuGiamGia(null);
//        }
//        hoaDon.setNguoiTao(hoaDon.getNhanVien().getTen());
//        hoaDon.setTenNguoiNhan(hoaDon.getKhachHang().getTen());
//        hoaDon.setNgayTao(new Date());
//        hoaDon.setDeleted(0);
//        hoaDon.setTrangThai("Đang Chờ");
//        hoaDon.setTongTien(0.0);
//        hoaDon.setLoaiHoaDon(1);
//        hoaDonRepository.save(hoaDon);
//        LichSuHoaDon lshd = new LichSuHoaDon();
//        lshd.setHoaDon(hoaDon);
//        lshd.setHanhDong("Tạo Mới");
//        lshd.setNgayTao(new Date());
//        lshd.setNhanVien(hoaDon.getNhanVien());
//        lshd.setKhachHang(hoaDon.getKhachHang());
//        lshd.setNguoiTao(hoaDon.getNhanVien().getTen());
//        lichSuHoaDonRepository.save(lshd);
//        return "redirect:/hoadon/hien-thi";
//    }
    @ModelAttribute("khachhang")
    public List<KhachHang> getKh() {
        return khachHangRepository.findAll();
    }

    @ModelAttribute("phieugiamgia")
    public List<PhieuGiamGia> getph() {
        return phieuGiamGiaRepository.findAll();
    }

    @ModelAttribute("nhanvienlist")
    public List<NhanVien> getnv() {
        System.out.println("Danh sách nhân viên: " + nhanVienRepository.findAll());
        return nhanVienRepository.findAll();
    }
//    lấy tất cả tỉnh thành của api
    @GetMapping("/provinces")
    public String showProvinces(Model model) {
        List<Province> provinces = ghnService.getProvinces();
        model.addAttribute("provinces", provinces);
        return "diachi";
    }
//    lấy tất cả quận huyện của api

    @GetMapping("/districts")
    @ResponseBody
    public List<Disctrict> getDistricts(@RequestParam("provinceId") int provinceId) {
        return ghnService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    @ResponseBody
    public List<Ward> getWards(@RequestParam("districtId") int districtId) {
        return ghnService.getWards(districtId);
    }
//    lấy địa chỉ api

    @GetMapping("/hoadon/view-hoadon/{id}")
    public String viewhoadon(Model model, @PathVariable("id") Integer id, @RequestParam(name = "page") Optional<Integer> page) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        model.addAttribute("hoadon", hd);
        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepository.findAllByHoaDon_Id(id);
        double giamGia = 0.0;
        if (hd.getPhieuGiamGia().getHinhThucGiam() == 0) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getPhieuGiamGia().getGiaTriDonToiThieu();
            }
        } else if (hd.getPhieuGiamGia().getHinhThucGiam() == 1) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getTongTien() * hd.getPhieuGiamGia().getPhanTramGiam() / 100;
                if (giamGia > hd.getPhieuGiamGia().getGiamToiDa()) {
                    giamGia = hd.getPhieuGiamGia().getGiamToiDa();
                }
            }
        }
        model.addAttribute("giamgia", giamGia);
        List<HinhThucThanhToanHoaDon> hinhThucThanhToanHoaDonList = hoaDonHinhThucThanhToanRepository.findAllByHoaDon_Id(id);
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<HoaDonChiTiet> hdct = hoaDonChiTietRepository.findAllByHoaDon_Id(id, pageable);
        List<HoaDonChiTiet> hdctlist = hoaDonChiTietRepository.findallbyhoadon(id);
        double totalAmount = hdctlist.stream()
                .mapToDouble(item -> item.getDonGia()).sum();
        model.addAttribute("totalAmount", totalAmount);
        Map<Integer, String> hinhAnhMap = new HashMap<>();
        for (HoaDonChiTiet chiTiet : hdctlist) {
            List<String> hinhanhht = hinhAnhRepository.hinhAnhDuongDan(chiTiet.getSanPhamChiTiet().getId());
            if (!hinhanhht.isEmpty()) {
                hinhAnhMap.put(chiTiet.getSanPhamChiTiet().getId(), hinhanhht.get(0)); // Lấy ảnh đầu tiên
            } else {
                hinhAnhMap.put(chiTiet.getSanPhamChiTiet().getId(), "default-image.jpg"); // Ảnh mặc định nếu không có
            }
        }
        model.addAttribute("hinhAnhMap", hinhAnhMap);
        model.addAttribute("hoadonchitiet", hdct);
        model.addAttribute("hoadonchitietlist", hdctlist);
        model.addAttribute("lichsuhoadon", lichSuHoaDonList);
        if (lichSuHoaDonList.size()>1){
            model.addAttribute("lichsuhoadonphantucuoihanhdong", lichSuHoaDonList.get(lichSuHoaDonList.size()-1).getHanhDong());
        }
        model.addAttribute("lichsuhoadonfull", lichSuHoaDonRepository.findAllByHoaDon_Id1(id));
        model.addAttribute("hinhThucThanhToanHoaDonList", hinhThucThanhToanHoaDonList);

        if (hd != null) {
            // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
            Province province = ghnService.getProvinceByName(hd.getTinhThanhPho());
            List<Disctrict> districts = new ArrayList<>();
            List<Ward> wards = new ArrayList<>();
            if (province != null) {
                // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                Disctrict district = ghnService.getDistrictByName(hd.getQuanHuyen(), province.getProvinceID());
                districts = ghnService.getDistricts(province.getProvinceID());
                model.addAttribute("selectedDistrict", district);
                if (district != null) {
                    // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                    Ward ward = ghnService.getWardByName(hd.getPhuongXa(), district.getDistrictID());
                    wards = ghnService.getWards(district.getDistrictID());
                    model.addAttribute("selectedWard", ward);
                }
            }

            List<Province> provinces = ghnService.getProvinces();
            model.addAttribute("provinces", provinces);
            model.addAttribute("districts", districts);
            model.addAttribute("wards", wards);
            model.addAttribute("selectedProvince", province);
        }


//        model.addAttribute("vouchergiam",vouchergiam);
//        model.addAttribute()
        return "admin/hoa-don/view-hoadon";
    }

    @PostMapping("/hoadon/addlshd/{id}")
    public String addlshoadon(Model model
            , @PathVariable("id") Integer id
            , @RequestParam(value = "nutbuttonls",required = false) String nutbutton
            , @RequestParam(value = "nuthuy",required = false) String nuthuy) throws MessagingException {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepository.findAllByHoaDon_Id(id);
        double giamGia = 0.0;
        if (hd.getPhieuGiamGia().getHinhThucGiam() == 0) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getPhieuGiamGia().getGiaTriDonToiThieu();
            }
        } else if (hd.getPhieuGiamGia().getHinhThucGiam() == 1) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getTongTien() * hd.getPhieuGiamGia().getPhanTramGiam() / 100;
                if (giamGia > hd.getPhieuGiamGia().getGiamToiDa()) {
                    giamGia = hd.getPhieuGiamGia().getGiamToiDa();
                }
            }
        }
        if ("Xác Nhận".equals(nutbutton)) {
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo("quannvph37802@fpt.edu.vn");
            dataMail.setSubject("[ChinShoes] Đơn hàng của bạn đã được tiếp nhận ✅");
            Map<String, Object> props = new HashMap<>();
            props.put("hoadon", hd);
            props.put("lichsuhoadonfull", lichSuHoaDonRepository.findAllByHoaDon_Id1(id));
            props.put("hoadonchitietlist", hoaDonChiTietRepository.findallbyhoadon(id));
            props.put("giamgia", giamGia);
            dataMail.setProps(props);
                emailService.sendHtmlMail(dataMail, "admin/hoa-don/email-template");
//                emailService.htmlToPdf(dataMail, "emailtemplate",xuatpdf);
        }
        LichSuHoaDon lshd = new LichSuHoaDon();
        lshd.setHoaDon(hd);
        if ("Hủy Đơn Hàng".equals(nuthuy)){
            lshd.setHanhDong(nuthuy);
        }else{
            lshd.setHanhDong(nutbutton);
        }
        lshd.setNgayTao(new Date());
        lshd.setNhanVien(hd.getNhanVien());
        lshd.setKhachHang(hd.getKhachHang());
        lshd.setNguoiTao(hd.getNhanVien().getTen());
        lshd.setDeleted(0);
        lichSuHoaDonRepository.save(lshd);
        hd.setTrangThai(lshd.getHanhDong());
        hoaDonRepository.save(hd);
        model.addAttribute("lichsuhoadon", lichSuHoaDonList);
        return "redirect:/admin/hoadon/view-hoadon/{id}";
    }

    @GetMapping("/hoadon/quaylai/{id}")
    public String removelshoadon(Model model, @PathVariable("id") Integer id, @ModelAttribute("lichsuhoadon") LichSuHoaDon lshd1) {
        List<LichSuHoaDon> llshd = lichSuHoaDonRepository.findAllByHoaDon_Id(id);
        LichSuHoaDon lshd = lichSuHoaDonRepository.getReferenceById(llshd.get(llshd.size() - 1).getId());
        lshd1 = lshd;
        lshd1.setDeleted(1);
        lshd1.setNgaySua(new Date());
        lichSuHoaDonRepository.save(lshd1);
        model.addAttribute("lichsuhoadon", llshd);
        return "redirect:/admin/hoadon/view-hoadon/{id}";
    }


    //    @GetMapping("/hoadon/xuatpdf/{id}")
//    public String xuathoadon(@PathVariable("id") Integer id) throws MessagingException {
//        HoaDon hd = hoaDonRepository.getReferenceById(id);
//        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepository.findAllByHoaDon_Id(id);
//        double giamGia = 0.0;
//        if (hd.getPhieuGiamGia().getHinhThucGiam() == 0) {
//            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
//                giamGia = hd.getPhieuGiamGia().getGiaTriDonToiThieu();
//            }
//        } else if (hd.getPhieuGiamGia().getHinhThucGiam() == 1) {
//            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
//                giamGia = hd.getTongTien() * hd.getPhieuGiamGia().getPhanTramGiam() / 100;
//                if (giamGia > hd.getPhieuGiamGia().getGiamToiDa()) {
//                    giamGia = hd.getPhieuGiamGia().getGiamToiDa();
//                }
//            }
//        }
//        try {
//            DataMailDTO dataMail = new DataMailDTO();
//            dataMail.setTo("baotmph39390@fpt.edu.vn");
//            dataMail.setSubject("Hóa Đơn");
//            Map<String, Object> props = new HashMap<>();
//            props.put("hoadon", hd);
//            props.put("lichsuhoadonfull", lichSuHoaDonRepository.findAllByHoaDon_Id1(id));
//            props.put("hoadonchitietlist", hoaDonChiTietRepository.findallbyhoadon(id));
//            props.put("giamgia", giamGia);
//            dataMail.setProps(props);
//            emailService.htmlToPdf(dataMail, "emailtemplate");
//        } catch (IOException exp) {
//            exp.printStackTrace();
//        }
//        return "redirect:/admin/hoadon/view-hoadon/{id}";
//    }
    @GetMapping("/hoadon/xuatpdf/{id}")
    public String xuathoadon(@PathVariable("id") Integer id, Model model) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        double giamGia = 0.0;
        if (hd.getPhieuGiamGia().getHinhThucGiam() == 0) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getPhieuGiamGia().getGiaTriDonToiThieu();
            }
        } else if (hd.getPhieuGiamGia().getHinhThucGiam() == 1) {
            if (hd.getPhieuGiamGia().getGiaTriDonToiThieu() <= hd.getTongTien()) {
                giamGia = hd.getTongTien() * hd.getPhieuGiamGia().getPhanTramGiam() / 100;
                if (giamGia > hd.getPhieuGiamGia().getGiamToiDa()) {
                    giamGia = hd.getPhieuGiamGia().getGiamToiDa();
                }
            }
        }
        model.addAttribute("hoadon", hd);
        model.addAttribute("lichsuhoadonfull", lichSuHoaDonRepository.findAllByHoaDon_Id1(id));
        model.addAttribute("hoadonchitietlist", hoaDonChiTietRepository.findallbyhoadon(id));
        model.addAttribute("giamgia", giamGia);
        return "admin/hoa-don/email-template";
    }

}
