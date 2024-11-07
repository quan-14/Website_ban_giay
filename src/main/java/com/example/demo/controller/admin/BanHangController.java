package com.example.demo.controller.admin;

import com.example.demo.entity.*;
import com.example.demo.repository.admin.*;
import com.example.demo.service.admin.EmailService;
import com.example.demo.service.admin.impl.GhnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/admin")
public class BanHangController {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HinhThucThanhToanRepository hinhThucThanhToanRepository;
    @Autowired
    HinhAnhRepository hinhAnhRepository;
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    DiaChiRepository diaChiRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @Autowired
    LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired
    KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;
    @Autowired
    HoaDonHinhThucThanhToanRepository hoaDonHinhThucThanhToanRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    private GhnService ghnService;

    @GetMapping("/ban-hang")
    public String hienThi(Model model, @RequestParam(required = false) Integer id) {
        List<HoaDon> hd = hoaDonRepository.listhoadonbh();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date sixAMToday = calendar.getTime();
        for (HoaDon hd1 : hd){
            if (hd1.getNgayTao().before(sixAMToday)){
                if (hoaDonHinhThucThanhToanRepository.findAllByHoaDon_Id(hd1.getId()).size()<1){
                    if (hd1.getPhieuGiamGia()!=null){
                        PhieuGiamGia pggd = hd1.getPhieuGiamGia();
                        pggd.setSoLuong(pggd.getSoLuong()+1);
                        phieuGiamGiaRepository.save(pggd);
                    }
                    List<HoaDonChiTiet> hdctl = hoaDonChiTietRepository.findallbyhoadon(hd1.getId());
                    if(hdctl.size()>0){
                        for (HoaDonChiTiet hdctc : hdctl) {
                            SanPhamChiTiet spctd = hdctc.getSanPhamChiTiet();
                            spctd.setSoLuong(spctd.getSoLuong()+hdctc.getSoLuong());
                            sanPhamChiTietRepository.save(spctd);
                            hoaDonChiTietRepository.delete(hdctc);
                        }
                    }
                    hoaDonRepository.delete(hd1);
                }
            }
        }
        model.addAttribute("hd", hd);
        if (id == null && !hd.isEmpty()) {
            id = hd.get(0).getId();
        }
        DiaChi diaChi = new DiaChi();
        model.addAttribute("diaChi", diaChi);
        // Khởi tạo danh sách rỗng cho hdct
        List<HoaDonChiTiet> hdct = new ArrayList<>();
        HoaDon hoaDon = new HoaDon();
        KhachHang khachHang = new KhachHang();
        DiaChi dc = new DiaChi();
        if (!hd.isEmpty()) {
            hdct = hoaDonChiTietRepository.findallbyhoadon(id); // Lấy danh sách chi tiết hóa đơn
            Map<Integer, String> hinhAnhMap = new HashMap<>();
            for (HoaDonChiTiet chiTiet : hdct) {
                List<String> hinhanhht = hinhAnhRepository.hinhAnhDuongDan(chiTiet.getSanPhamChiTiet().getId());
                if (!hinhanhht.isEmpty()) {
                    hinhAnhMap.put(chiTiet.getSanPhamChiTiet().getId(), hinhanhht.get(0)); // Lấy ảnh đầu tiên
                } else {
                    hinhAnhMap.put(chiTiet.getSanPhamChiTiet().getId(), "default-image.jpg"); // Ảnh mặc định nếu không có
                }
            }
            double totalAmount = hdct.stream()
                    .mapToDouble(item -> item.getDonGia()).sum();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedAmount = currencyFormat.format(totalAmount);
            model.addAttribute("formattedAmount", formattedAmount);
            hoaDon = hoaDonRepository.getReferenceById(id);
            PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();
            double giaTriGiam = 0;

            if (pgg != null) {
                if (pgg.getHinhThucGiam() == 0) {
                    // Giảm theo số tiền cố định
                    giaTriGiam = pgg.getSoTienGiam();
                } else {
                    // Giảm theo phần trăm, kiểm tra giảm tối đa
                    double phanTramGiam = totalAmount * (pgg.getPhanTramGiam() / 100);
                    giaTriGiam = Math.min(phanTramGiam, pgg.getGiamToiDa());
                }
            }
            model.addAttribute("giatriGiam", giaTriGiam);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("hinhAnhMap", hinhAnhMap);
            if (hoaDon.getKhachHang() != null) {

                khachHang = khachHangRepository.getReferenceById(hoaDon.getKhachHang().getId());
                dc = diaChiRepository.findDiaChiDefaultByKhachHangId(khachHang.getId());
                Province province = new Province();
                if (hoaDon.getTinhThanhPho() == null) {
                    province = ghnService.getProvinceByName(dc.getTinhThanhPho());
                } else {
                    province = ghnService.getProvinceByName(hoaDon.getTinhThanhPho());
                }
                List<Disctrict> districts = new ArrayList<>();
                List<Ward> wards = new ArrayList<>();
                if (province != null) {
                    // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                    Disctrict district = new Disctrict();
                    if (hoaDon.getQuanHuyen() == null) {
                        district = ghnService.getDistrictByName(dc.getQuanHuyen(), province.getProvinceID());
                    } else {
                        district = ghnService.getDistrictByName(hoaDon.getQuanHuyen(), province.getProvinceID());
                    }
                    districts = ghnService.getDistricts(province.getProvinceID());
                    model.addAttribute("selectedDistrict", district);
                    if (district != null) {
                        // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                        Ward ward = new Ward();
                        if (hoaDon.getPhuongXa() == null) {
                            ward = ghnService.getWardByName(dc.getPhuongXa(), district.getDistrictID());
                        } else {
                            ward = ghnService.getWardByName(hoaDon.getPhuongXa(), district.getDistrictID());
                        }
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
        }

        // Đảm bảo hdct luôn được khởi tạo, ngay cả khi trống
        model.addAttribute("hdct", hdct);
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("dc", dc);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("idhienthihd", id);
        List<HinhThucThanhToan> lsthttthd = hoaDonHinhThucThanhToanRepository.listfindhttt(id);
        double tienkhachtra = 0.0;
        tienkhachtra = lsthttthd.stream()
                .mapToDouble(item -> item.getSoTienThanhToan())
                .sum();
        model.addAttribute("tienkhachtra", tienkhachtra);
        return "admin/ban-hang/ban-hang";
    }


    @GetMapping("ban-hang/tim-kiem-kh")
    public String timKiemKhachHang(Model model, @RequestParam(defaultValue = "") String search1, @RequestParam(name = "page") Optional<Integer> page, @RequestParam(required = false) Integer id) {
        int checkpage = page.orElse(0);
        int pagesize = 5;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<KhachHang> khachHangfind = khachHangRepository.findkh(search1.trim(), pageable);
        model.addAttribute("khachhang", khachHangfind);
        model.addAttribute("idhienthihd", id);
        return "admin/ban-hang/add-khach-hang";
    }

    @GetMapping("/ban-hang/dskhachhang")
    public String hienThiHoaDon(Model model, @RequestParam(name = "page") Optional<Integer> page, @RequestParam(required = false) Integer id) {
        int checkpage = page.orElse(0);
        int pagesize = 5;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<KhachHang> khachHang = khachHangRepository.findAll(pageable);
        model.addAttribute("idhienthihd", id);
        model.addAttribute("khachhang", khachHang);
        List<Disctrict> districts = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();
        List<Province> provinces = ghnService.getProvinces();
        model.addAttribute("provinces", provinces);
        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
        model.addAttribute("diaChi", new DiaChi());
        return "admin/ban-hang/add-khach-hang";
    }

    @PostMapping("/ban-hang/addkh")
    public String addDC(@RequestParam(required = false) Integer id
            , @ModelAttribute DiaChi dcd
            , @RequestParam(value = "provinceId", required = false) String provinceId
            , @RequestParam(value = "districtId", required = false) String districtId
            , @RequestParam(value = "wardCode", required = false) String wardCode
            , @RequestParam(value = "email", required = false) String email
            , @RequestParam(value = "tenDiaChi", required = false) String tenDiaChi
            , @RequestParam(value = "soDienThoai", required = false) String soDienThoai
            , @RequestParam(value = "gioiTinh", required = false) Integer gioiTinh) {
        Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
        Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
        Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
        KhachHang khachHang = new KhachHang();
        khachHang.setTen(tenDiaChi);
        khachHang.setEmail(email);
        khachHang.setGioiTinh(gioiTinh);
        khachHang.setSdt(soDienThoai);
        khachHang.setHinhAnh("https://res.cloudinary.com/drkrb9gk0/image/upload/v1726556120/5b05fb8d-b203-4421-bc49-cd0516fc955b_user%20default.jpg");
        khachHang.setVaiTro("Khách hàng");
        khachHang.setTrangThai("Đang hoạt động");
        khachHang.setNguoiTao("Admin");
        List<String> listma = khachHangRepository.listma();
        String lastma = listma.get(0);
        String prefix = lastma.substring(0, 2);
        int numberPart = Integer.parseInt(lastma.substring(2));
        numberPart++;
        String newNumberPart = String.format("%03d", numberPart);
        khachHang.setMa(prefix + newNumberPart);
        khachHangRepository.save(khachHang);
        DiaChi dc = new DiaChi();
        dc.setKhachHang(khachHang);
        dc.setTinhThanhPho(province.getProvinceName());
        dc.setQuanHuyen(disctrict.getDistrictName());
        dc.setPhuongXa(ward.getWardName());
        dc.setDiaChiCuThe(dcd.getDiaChiCuThe());
        dc.setMacDinh(0);
        diaChiRepository.save(dc);
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        hd.setKhachHang(khachHang);
        hoaDonRepository.save(hd);
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @GetMapping("/ban-hang/dssanpham")
    public String hienThisanpham(Model model, @RequestParam(name = "page") Optional<Integer> page, @RequestParam(required = false) Integer id) {
        int checkpage = page.orElse(0);
        int pagesize = 5;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<SanPhamChiTiet> sanPhamChiTiet = sanPhamChiTietRepository.pagespct(pageable);
        List<SanPhamChiTiet> sanpctl = sanPhamChiTietRepository.findAll();
        Map<Integer, String> hinhAnhMap = new HashMap<>();
        for (SanPhamChiTiet spchiTiet : sanpctl) {
            List<String> hinhanhht = hinhAnhRepository.hinhAnhDuongDan(spchiTiet.getId());
            if (!hinhanhht.isEmpty()) {
                hinhAnhMap.put(spchiTiet.getId(), hinhanhht.get(0)); // Lấy ảnh đầu tiên
            } else {
                hinhAnhMap.put(spchiTiet.getId(), "default-image.jpg"); // Ảnh mặc định nếu không có
            }
        }
        model.addAttribute("hinhAnhMap", hinhAnhMap);
        model.addAttribute("idhienthihd", id);
        model.addAttribute("spctl", sanPhamChiTiet);
        return "admin/ban-hang/add-san-pham";
    }

    @PostMapping("/taohd")
    public String taoHd() {
        List<String> listma = hoaDonRepository.listma();
        String lastma = listma.get(0);
        String prefix = lastma.substring(0, 2);
        int numberPart = Integer.parseInt(lastma.substring(2));

        numberPart++;

        // Đảm bảo mã hóa đơn có độ dài 3 ký tự
        String newNumberPart = String.format("%03d", numberPart);
        HoaDon hd = new HoaDon();
        NhanVien nv = nhanVienRepository.findAll().get(0);
        hd.setMa(prefix + newNumberPart);
        hd.setNgayTao(new Date());
        hd.setNhanVien(nv);
        hd.setLoaiHoaDon(1);
        hd.setTrangThai("Tạo mới");
        hd.setTongTien(0.0);
        hd.setGhiChu("");
        hd.setNguoiTao(nv.getTen());
        hd.setDeleted(0);
        hoaDonRepository.save(hd);
        return "redirect:/admin/ban-hang";
    }

    @PostMapping("/updatekh")
    public String updateKh(@RequestParam("id") Integer id, @RequestParam("idkh") Integer idkh) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        KhachHang kh = khachHangRepository.getReferenceById(idkh);
        hd.setKhachHang(kh);
        hd.setPhiShip(0);
        hoaDonRepository.save(hd);
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @PostMapping("/ban-hang/updategh/{id}")
    public String updateGh(@PathVariable("id") Integer id) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        KhachHang kh = khachHangRepository.getReferenceById(hd.getKhachHang().getId());
        DiaChi dc = diaChiRepository.findDiaChiDefaultByKhachHangId(kh.getId());
        List<HoaDonChiTiet> hdctlist = hoaDonChiTietRepository.findallbyhoadon(id);
        int tongkhoiluonghd = 0;
        for (int i = 0; i < hdctlist.size(); i++) {
            tongkhoiluonghd += (hdctlist.get(i).getSanPhamChiTiet().getKhoiLuong() * hdctlist.get(i).getSoLuong());
        }
        if (hd.getPhiShip() == null || hd.getPhiShip() == 0) {
            if (hd.getTinhThanhPho() == null || hd.getQuanHuyen() == null || hd.getPhuongXa() == null) {
                Province province = ghnService.getProvinceByName(dc.getTinhThanhPho());
                if (province != null) {
                    // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                    Disctrict district = ghnService.getDistrictByName(dc.getQuanHuyen(), province.getProvinceID());
                    if (district != null) {
                        // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                        Ward ward = ghnService.getWardByName(dc.getPhuongXa(), district.getDistrictID());
                        hd.setPhiShip(ghnService.getShippingFee(district.getDistrictID(), ward.getWardCode(), tongkhoiluonghd));
                    }
                }
            } else {
                Province province = ghnService.getProvinceByName(hd.getTinhThanhPho());
                if (province != null) {
                    // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                    Disctrict district = ghnService.getDistrictByName(hd.getQuanHuyen(), province.getProvinceID());
                    System.out.println(province.getProvinceName());
                    System.out.println(district);
                    if (district != null) {
                        // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                        Ward ward = ghnService.getWardByName(hd.getPhuongXa(), district.getDistrictID());
                        hd.setPhiShip(ghnService.getShippingFee(district.getDistrictID(), ward.getWardCode(), tongkhoiluonghd));
                    }
                }
            }
        } else {
            hd.setPhiShip(null);
        }

        hoaDonRepository.save(hd);
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @PostMapping("/congsoluong")
    public String updateSpChi(@RequestParam("id") Integer id) {

        HoaDonChiTiet hdct = hoaDonChiTietRepository.getReferenceById(id);
        hdct.setSoLuong(hdct.getSoLuong() + 1);
        hdct.setDonGia(hdct.getSoLuong() * hdct.getSanPhamChiTiet().getDonGia());
        hoaDonChiTietRepository.save(hdct);
        setpgg(hdct.getHoaDon().getId());
        SanPhamChiTiet spct = sanPhamChiTietRepository.getReferenceById(hdct.getSanPhamChiTiet().getId());
        spct.setSoLuong(spct.getSoLuong() - 1);
        sanPhamChiTietRepository.save(spct);
        return "redirect:/admin/ban-hang?id=" + hdct.getHoaDon().getId();
    }

    public HoaDon setpgg(Integer id) {
        List<HoaDonChiTiet> lsthdct = hoaDonChiTietRepository.findallbyhoadon(id);
        HoaDon hoaDon = hoaDonRepository.getReferenceById(id);
        double totalAmount = lsthdct.stream()
                .mapToDouble(item -> item.getDonGia()).sum();
        List<PhieuGiamGia> lstpgg = phieuGiamGiaRepository.lstpgg();
        PhieuGiamGia pggMax = lstpgg.stream()
                .filter(pgg -> totalAmount >= pgg.getGiaTriDonToiThieu()) // Điều kiện tổng tiền > giá trị đơn tối thiểu
                .max((pgg1, pgg2) -> {
                    // Tính giá trị giảm của pgg1
                    double giaTriGiam1 = pgg1.getHinhThucGiam() == 0
                            ? pgg1.getSoTienGiam()
                            : Math.min(totalAmount * (pgg1.getPhanTramGiam() / 100), pgg1.getGiamToiDa());

                    double giaTriGiam2 = pgg2.getHinhThucGiam() == 0
                            ? pgg2.getSoTienGiam()
                            : Math.min(totalAmount * (pgg2.getPhanTramGiam() / 100), pgg2.getGiamToiDa());
                    // So sánh giá trị giảm
                    return Double.compare(giaTriGiam1, giaTriGiam2);
                }).orElse(null);
        if (pggMax != null) {
            if (pggMax != hoaDon.getPhieuGiamGia() && hoaDon.getPhieuGiamGia() != null) {
                PhieuGiamGia pgghd = hoaDon.getPhieuGiamGia();
                pgghd.setSoLuong(pgghd.getSoLuong() + 1);
                phieuGiamGiaRepository.save(pgghd);
                pggMax.setSoLuong(pggMax.getSoLuong() - 1);
                phieuGiamGiaRepository.save(pggMax);
                hoaDon.setPhieuGiamGia(pggMax);
            } else if (hoaDon.getPhieuGiamGia() == null) {
                pggMax.setSoLuong(pggMax.getSoLuong() - 1);
                phieuGiamGiaRepository.save(pggMax);
                hoaDon.setPhieuGiamGia(pggMax);
            } else if (hoaDon.getPhieuGiamGia() == pggMax) {
                hoaDon.setPhieuGiamGia(pggMax);
            }
            hoaDonRepository.save(hoaDon);
        }
        return hoaDon;
    }

    @PostMapping("/trusoluong")
    public String updateSpChi1(@RequestParam("id") Integer id) {
        HoaDonChiTiet hdct = hoaDonChiTietRepository.getReferenceById(id);
        hdct.setSoLuong(hdct.getSoLuong() - 1);
        hdct.setDonGia(hdct.getSoLuong() * hdct.getSanPhamChiTiet().getDonGia());
        hoaDonChiTietRepository.save(hdct);
        setpgg(hdct.getHoaDon().getId());
        SanPhamChiTiet spct = sanPhamChiTietRepository.getReferenceById(hdct.getSanPhamChiTiet().getId());
        spct.setSoLuong(spct.getSoLuong() + 1);
        sanPhamChiTietRepository.save(spct);
        return "redirect:/admin/ban-hang?id=" + hdct.getHoaDon().getId();
    }

    @PostMapping("/updatesphdct")
    public String updateSphdct(@RequestParam("id") Integer id, @RequestParam(value = "soluongadd", required = false) String soluongadd) {
        soluongadd = soluongadd.trim().replaceAll("[^\\w\\s]", ""); // Loại bỏ tất cả ký tự đặc biệt ngoại trừ chữ cái, chữ số, và khoảng trắng
        HoaDonChiTiet hdct = hoaDonChiTietRepository.getReferenceById(id);
        if (soluongadd == null || soluongadd.trim().isEmpty() || soluongadd.trim().equals("0")) {
            soluongadd = "1";
        } else {
            try {
                // Thử chuyển đổi soluongadd sang số nguyên
                int value = Integer.parseInt(soluongadd.trim());

                // Kiểm tra nếu giá trị nhỏ hơn 0
                if (value < 0) {
                    soluongadd = "1";
                }
                // Kiểm tra nếu giá trị lớn hơn số lượng cho phép
                else if (value > hdct.getSanPhamChiTiet().getSoLuong() + hdct.getSoLuong()) {
                    soluongadd = String.valueOf(hdct.getSanPhamChiTiet().getSoLuong() + hdct.getSoLuong());
                } else {
                    // Nếu hợp lệ, gán lại giá trị số nguyên đã kiểm tra
                    soluongadd = String.valueOf(value);
                }
            } catch (NumberFormatException e) {
                // Trường hợp soluongadd không phải là số nguyên
                soluongadd = "1";
            }
        }
        SanPhamChiTiet spct = sanPhamChiTietRepository.getReferenceById(hdct.getSanPhamChiTiet().getId());
        if (Integer.parseInt(soluongadd) > hdct.getSoLuong()) {
            spct.setSoLuong(spct.getSoLuong() - (Integer.parseInt(soluongadd.trim()) - hdct.getSoLuong()));
            sanPhamChiTietRepository.save(spct);
            hdct.setSoLuong(Integer.parseInt(soluongadd));
            hdct.setDonGia(hdct.getSanPhamChiTiet().getDonGia() * hdct.getSoLuong());
            hoaDonChiTietRepository.save(hdct);
        } else {
            spct.setSoLuong(spct.getSoLuong() + (hdct.getSoLuong() - Integer.parseInt(soluongadd.trim())));
            sanPhamChiTietRepository.save(spct);
            hdct.setSoLuong(Integer.parseInt(soluongadd));
            hdct.setDonGia(hdct.getSanPhamChiTiet().getDonGia() * hdct.getSoLuong());
            hoaDonChiTietRepository.save(hdct);
        }
        setpgg(hdct.getHoaDon().getId());

        return "redirect:/admin/ban-hang?id=" + hdct.getHoaDon().getId();
    }

    @GetMapping("/deletehdct")
    public String deleteHoaDonChiTiet(@RequestParam("id") Integer id) {
        HoaDonChiTiet hdct = hoaDonChiTietRepository.getReferenceById(id);
        Integer idhd = hdct.getHoaDon().getId();
        SanPhamChiTiet spct = sanPhamChiTietRepository.getReferenceById(hdct.getSanPhamChiTiet().getId());
        spct.setSoLuong(spct.getSoLuong() + hdct.getSoLuong());
        sanPhamChiTietRepository.save(spct);
        hoaDonChiTietRepository.delete(hdct);
        setpgg(idhd);

        return "redirect:/admin/ban-hang?id=" + idhd;
    }

    @PostMapping("/updatesp")
    public String updateSp(@RequestParam("id") Integer id, @RequestParam("idspct") Integer idspct, @RequestParam(value = "soluongadd", required = false) String soluongadd) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        SanPhamChiTiet spct = sanPhamChiTietRepository.getReferenceById(idspct);
        List<String> listma = hoaDonRepository.listma();
        String lastma = listma.get(0);
        String prefix = lastma.substring(0, 4);
        int numberPart = Integer.parseInt(lastma.substring(4));
        numberPart++;
        soluongadd = soluongadd.trim().replaceAll("[^\\w\\s]", ""); // Loại bỏ tất cả ký tự đặc biệt ngoại trừ chữ cái, chữ số, và khoảng trắng

        // Đảm bảo mã hóa đơn có độ dài 3 ký tự
        String newNumberPart = String.format("%03d", numberPart);
        if (soluongadd == null || soluongadd.trim().isEmpty() || soluongadd.trim().equals("0")) {
            soluongadd = "1";
        } else {
            try {
                // Thử chuyển đổi soluongadd sang số nguyên
                int value = Integer.parseInt(soluongadd.trim());

                // Kiểm tra nếu giá trị nhỏ hơn 0
                if (value < 0) {
                    soluongadd = "1";
                }
                // Kiểm tra nếu giá trị lớn hơn số lượng cho phép
                else if (value > spct.getSoLuong()) {
                    soluongadd = String.valueOf(spct.getSoLuong());
                } else {
                    // Nếu hợp lệ, gán lại giá trị số nguyên đã kiểm tra
                    soluongadd = String.valueOf(value);
                }
            } catch (NumberFormatException e) {
                // Trường hợp soluongadd không phải là số nguyên
                soluongadd = "1";
            }
        }

        List<HoaDonChiTiet> listhdct = hoaDonChiTietRepository.findallbyhoadon(id);
        boolean exists = false;
        for (HoaDonChiTiet hdct1 : listhdct) {
            if (hdct1.getSanPhamChiTiet().getId() == idspct) {
                hdct1.setSoLuong(hdct1.getSoLuong() + Integer.parseInt(soluongadd.trim()));
                hdct1.setDonGia(spct.getDonGia() * hdct1.getSoLuong());
                hoaDonChiTietRepository.save(hdct1);
                exists = true;
                break;
            }
        }
        if (!exists) {
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setMa(prefix + newNumberPart);
            hdct.setHoaDon(hd);
            hdct.setSanPhamChiTiet(spct);
            hdct.setSoLuong(Integer.parseInt(soluongadd.trim()));
            hdct.setDonGia(spct.getDonGia() * hdct.getSoLuong());
            hdct.setNgayTao(new Date());
            hdct.setDeleted(0);
            hoaDonChiTietRepository.save(hdct);
            setpgg(hdct.getHoaDon().getId());
        }
        spct.setSoLuong(spct.getSoLuong() - Integer.parseInt(soluongadd.trim()));
        sanPhamChiTietRepository.save(spct);
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @GetMapping("/ban-hang/pgg")
    public String getPGG(@RequestParam("id") Integer id, Model model) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
//        Page<PhieuGiamGia> pgg = phieuGiamGiaRepository.findAll(pageable);
        List<PhieuGiamGia> lstpgg = phieuGiamGiaRepository.lstpgg();
        if (hd.getKhachHang() != null) {
            List<PhieuGiamGia> lstpggkh = khachHangPhieuGiamGiaRepository.lstpggkh(hd.getKhachHang().getId());
            for (PhieuGiamGia pgg1 : lstpggkh) {
                lstpgg.add(pgg1);
            }
        }
        model.addAttribute("idhienthihd", id);
        model.addAttribute("lstpgg", lstpgg);
        return "admin/ban-hang/add-pgg";
    }

    @PostMapping("/ban-hang/updatepgg")
    public String updatepgg(@RequestParam("id") Integer id, @RequestParam("idpgg") Integer idpgg) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.getReferenceById(idpgg);
        if (phieuGiamGia.getId() != id) {
            PhieuGiamGia pgg = hd.getPhieuGiamGia();
            pgg.setSoLuong(pgg.getSoLuong() + 1);
            phieuGiamGiaRepository.save(pgg);
            phieuGiamGia.setSoLuong(phieuGiamGia.getSoLuong() - 1);
            phieuGiamGiaRepository.save(phieuGiamGia);
            hd.setPhieuGiamGia(phieuGiamGia);
            hoaDonRepository.save(hd);
        }
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @GetMapping("/ban-hang/khachthanhtoan")
    public String khachThanhToan(@RequestParam("id") Integer id, Model model) {
        List<HinhThucThanhToanHoaDon> lsthttt = hoaDonHinhThucThanhToanRepository.findAllByHoaDon_Id(id);
        model.addAttribute("lsthttt", lsthttt);
        List<HoaDonChiTiet> hdct = hoaDonChiTietRepository.findallbyhoadon(id);
        double totalAmount = hdct.stream()
                .mapToDouble(item -> item.getDonGia()).sum();
        HoaDon hoaDon = hoaDonRepository.getReferenceById(id);
        PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();
        double giaTriGiam = 0;
        if (pgg != null) {
            if (pgg.getHinhThucGiam() == 0) {
                // Giảm theo số tiền cố định
                giaTriGiam = pgg.getSoTienGiam();
            } else {
                // Giảm theo phần trăm, kiểm tra giảm tối đa
                double phanTramGiam = totalAmount * (pgg.getPhanTramGiam() / 100);
                giaTriGiam = Math.min(phanTramGiam, pgg.getGiamToiDa());
            }
        }
        if (hoaDon.getPhiShip() == null) {
            hoaDon.setPhiShip(0);
        }
        double tongThanhToan = totalAmount - giaTriGiam + hoaDon.getPhiShip();
        List<HinhThucThanhToan> lsthttthd = hoaDonHinhThucThanhToanRepository.listfindhttt(id);
        double tienkhachtra = 0.0;
        tienkhachtra = lsthttthd.stream()
                .mapToDouble(item -> item.getSoTienThanhToan())
                .sum();

        tongThanhToan = tongThanhToan - tienkhachtra;
        String formattedTongThanhToan = String.format("%.2f", tongThanhToan);
        if (tongThanhToan >= tienkhachtra) {
            model.addAttribute("tongThanhToan", formattedTongThanhToan);
        } else {
            model.addAttribute("tongThanhToan", tongThanhToan);

        }
        model.addAttribute("idhienthihd", id);
        return "admin/ban-hang/thanh-toan";
    }

    @PostMapping("/ban-hang/khachthanhtoantm")
    public String khachThanhToanTM(@RequestParam("id") Integer id, @RequestParam("tienmat") String tienmat) {
        HinhThucThanhToan hinhThucThanhToan = new HinhThucThanhToan();
        hinhThucThanhToan.setTen("Tiền mặt");
        hinhThucThanhToan.setLoaiGiaoDich("Trả Trước");
        hinhThucThanhToan.setSoTienThanhToan(Double.parseDouble(tienmat));
        hinhThucThanhToan.setNgayTao(new Date());
        hinhThucThanhToanRepository.save(hinhThucThanhToan);
        HoaDon hoaDon = hoaDonRepository.getReferenceById(id);
        HinhThucThanhToanHoaDon hinhThucThanhToanHoaDon = new HinhThucThanhToanHoaDon();
        hinhThucThanhToanHoaDon.setHinhThucThanhToan(hinhThucThanhToan);
        hinhThucThanhToanHoaDon.setHoaDon(hoaDon);
        hoaDonHinhThucThanhToanRepository.save(hinhThucThanhToanHoaDon);
        return "redirect:/admin/ban-hang/khachthanhtoan?id=" + id;
    }

    @GetMapping("/ban-hang/deletehttt")
    public String deleteHTTT(@RequestParam("id") Integer id) {
        HinhThucThanhToanHoaDon hinhThucThanhToanHoaDon = hoaDonHinhThucThanhToanRepository.getReferenceById(id);
        Integer idhd = hinhThucThanhToanHoaDon.getHoaDon().getId();
        hoaDonHinhThucThanhToanRepository.delete(hinhThucThanhToanHoaDon);
        return "redirect:/admin/ban-hang/khachthanhtoan?id=" + idhd;
    }

    @PostMapping("/ban-hang/capnhadiachihoadon")
    public String capnhatdchd(@RequestParam("id") Integer id,
                              @RequestParam("tenkh") String tenkh,
                              @RequestParam("sdtkh") String sdtkh,
                              @RequestParam("provinceId1") String provinceId,
                              @RequestParam("districtId1") String districtId,
                              @RequestParam("wardCode1") String wardCode,
                              @RequestParam("dccuthe") String dccuthe, Model model) {
        HoaDon hd = hoaDonRepository.getReferenceById(id);
        List<HoaDonChiTiet> hdctlist = hoaDonChiTietRepository.findallbyhoadon(id);
        int tongkhoiluonghd = 0;
        for (int i = 0; i < hdctlist.size(); i++) {
            tongkhoiluonghd += (hdctlist.get(i).getSanPhamChiTiet().getKhoiLuong() * hdctlist.get(i).getSoLuong());
        }
        if (tenkh.trim().isEmpty()) {
            model.addAttribute("tenkherror", "Vui Lòng Nhập Tên Khách Hàng");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        if (sdtkh.trim().isEmpty()) {
            model.addAttribute("sdterror", "Vui Lòng Nhập SĐT Khách Hàng");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        try {
            Integer sdt = Integer.parseInt(sdtkh);
        } catch (Exception e) {
            model.addAttribute("sdterror", "SĐT Khách Hàng Phải là số");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        if (dccuthe.trim().isEmpty()) {
            model.addAttribute("dccutheerror", "Vui Lòng Nhập Địa Chỉ Cụ Thể");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        if (provinceId.trim().isEmpty()) {
            model.addAttribute("provinceerror", "Vui Lòng Chọn Tỉnh/Thành Phố");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        if (districtId.trim().isEmpty()) {
            model.addAttribute("districterror", "Vui Lòng Chọn Quận/Huyện");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }
        if (wardCode.trim().isEmpty()) {
            model.addAttribute("warderror", "Vui Lòng Chọn Phường/Xã");
            hienThi(model, id);
            return "admin/ban-hang/ban-hang";

        }

        Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
        Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
        Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
        hd.setTenNguoiNhan(tenkh);
        hd.setTinhThanhPho(province.getProvinceName());
        hd.setQuanHuyen(disctrict.getDistrictName());
        hd.setPhuongXa(ward.getWardName());
        hd.setDiaChiCuThe(dccuthe);
        hd.setSoDienThoai(sdtkh);
        hd.setPhiShip(ghnService.getShippingFee(Integer.parseInt(districtId), wardCode, tongkhoiluonghd));
        hoaDonRepository.save(hd);
        return "redirect:/admin/ban-hang?id=" + id;
    }

    @PostMapping("/ban-hang/thanhtoan")
    public String thanhtoan(@RequestParam("id") Integer id, Model model) {
        HoaDon hoaDon = hoaDonRepository.getReferenceById(id);
        List<HoaDonChiTiet> lsthdct = hoaDonChiTietRepository.findallbyhoadon(id);
        double totalAmount = lsthdct.stream()
                .mapToDouble(item -> item.getDonGia()).sum();
        Integer phiship = 0;
        PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();
        double giaTriGiam = 0;
        if (pgg != null) {
            if (pgg.getHinhThucGiam() == 0) {
                // Giảm theo số tiền cố định
                giaTriGiam = pgg.getSoTienGiam();
            } else {
                // Giảm theo phần trăm, kiểm tra giảm tối đa
                double phanTramGiam = totalAmount * (pgg.getPhanTramGiam() / 100);
                giaTriGiam = Math.min(phanTramGiam, pgg.getGiamToiDa());
            }
        }
        if (hoaDon.getPhiShip() != null) {
            phiship = hoaDon.getPhiShip();
        }
        double tongThanhToan = totalAmount - giaTriGiam + phiship;
        for (HoaDonChiTiet hdct : lsthdct) {
            if (hdct.getSanPhamChiTiet().getTrangThai().equals("Ngừng hoạt động") || hdct.getSanPhamChiTiet().getSanPham().getTrangThai().equals("Ngừng hoạt động")) {
                model.addAttribute("thanhtoanerror", "Sản phẩm " + hdct.getSanPhamChiTiet().getSanPham().getTen() + " đã ngưng hoạt động");
                hienThi(model, id);
                return "admin/ban-hang/ban-hang";
            }
        }
        if (hoaDon.getPhiShip() == null || hoaDon.getPhiShip() < 1) {
            List<HinhThucThanhToan> lsthttthd = hoaDonHinhThucThanhToanRepository.listfindhttt(id);
            double tienkhachtra = 0.0;
            tienkhachtra = lsthttthd.stream()
                    .mapToDouble(item -> item.getSoTienThanhToan())
                    .sum();
            if (tienkhachtra < tongThanhToan) {
                model.addAttribute("thanhtoanerror", "Làm phiền thanh toán hết hóa đơn");
                hienThi(model, id);
                return "admin/ban-hang/ban-hang";
            }
            hoaDon.setTongTien(tongThanhToan);
            hoaDon.setTrangThai("Nhận Hàng Thành Công");
            hoaDonRepository.save(hoaDon);
        } else {
            List<HinhThucThanhToan> lsthttthd = hoaDonHinhThucThanhToanRepository.listfindhttt(id);
            double tienkhachtra = 0.0;
            tienkhachtra = lsthttthd.stream()
                    .mapToDouble(item -> item.getSoTienThanhToan())
                    .sum();
            if (tienkhachtra < tongThanhToan) {
                HinhThucThanhToan httt = new HinhThucThanhToan();
                httt.setTen("Tiền mặt");
                httt.setSoTienThanhToan(tongThanhToan - tienkhachtra);
                httt.setLoaiGiaoDich("Trả Sau");
                httt.setNgayTao(new Date());
                hinhThucThanhToanRepository.save(httt);
                HinhThucThanhToanHoaDon httthd = new HinhThucThanhToanHoaDon();
                httthd.setHoaDon(hoaDon);
                httthd.setHinhThucThanhToan(httt);
                hoaDonHinhThucThanhToanRepository.save(httthd);
            }
            hoaDon.setTongTien(tongThanhToan);
            hoaDon.setTrangThai("Xác Nhận");
            hoaDonRepository.save(hoaDon);
            LichSuHoaDon lshd = new LichSuHoaDon();
            lshd.setHoaDon(hoaDon);
            lshd.setNgayTao(new Date());
            lshd.setNhanVien(nhanVienRepository.getReferenceById(1));
            lshd.setKhachHang(hoaDon.getKhachHang());
            lshd.setNguoiTao(lshd.getNhanVien().getTen());
            lshd.setHanhDong("Tạo mới");
            lshd.setDeleted(0);
            lichSuHoaDonRepository.save(lshd);
            LichSuHoaDon lshd1 = new LichSuHoaDon();
            lshd1.setHoaDon(hoaDon);
            lshd1.setNgayTao(new Date());
            lshd1.setNhanVien(nhanVienRepository.getReferenceById(1));
            lshd1.setKhachHang(hoaDon.getKhachHang());
            lshd1.setNguoiTao(lshd.getNhanVien().getTen());
            lshd1.setHanhDong("Xác Nhận");
            lshd1.setDeleted(0);
            lichSuHoaDonRepository.save(lshd1);

        }
        return "redirect:/admin/ban-hang";
    }
}
