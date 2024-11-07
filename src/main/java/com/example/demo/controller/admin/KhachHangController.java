package com.example.demo.controller.admin;

import com.example.demo.entity.*;
import com.example.demo.repository.admin.DiaChiRepository;
import com.example.demo.repository.admin.KhachHangRepository;
import com.example.demo.service.admin.impl.GhnService;
import com.example.demo.service.admin.impl.UploadImageFileImpl;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;


@Controller
@RequestMapping("/admin")
public class KhachHangController {
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    UploadImageFileImpl uploadImageFile;
    @Autowired
    DiaChiRepository diaChiRepository;
    @Autowired
    private GhnService ghnService;


    @GetMapping("/khachhang/hienthi")
    public String timKiemKhachHang(Model model, @RequestParam(defaultValue = "") String search1
            , @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date1
            , @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date2
            ,  @RequestParam(required = false) String loaihoadontim, @RequestParam(name = "page") Optional<Integer> page) {
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
            calendar.add(Calendar.YEAR, -100);
            date1 = calendar.getTime();
        }
        Integer loai1Int = null;
        if (loaihoadontim != null && !loaihoadontim.isEmpty()) {
            loai1Int = Integer.parseInt(loaihoadontim);
        }

        Page<KhachHang> listKhachHang = khachHangRepository.searchKhachHang(
                search1.trim(), loai1Int, date1, date2, pageable);
        model.addAttribute("listKH", listKhachHang);
        model.addAttribute("search1", search1);
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("loaihoadontim", loaihoadontim != null ? loaihoadontim : "");
        return "admin/khach-hang/khach-hang";
    }

    @GetMapping("/khachhang/viewdc")
    public String viewDC(@RequestParam("id") Integer id, Model model) {
        List<DiaChi> listdc = diaChiRepository.findDiaChibyKH(id);
        model.addAttribute("listdc", listdc);
        List<Disctrict> districts = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();
        List<Province> provinces = ghnService.getProvinces();
        model.addAttribute("provinces", provinces);
        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
        model.addAttribute("diaChi", new DiaChi());
        model.addAttribute("idkh", id);
        return "admin/khach-hang/khach-hang-dia-chi";
    }
    @GetMapping("/khachhang/viewupdatedc")
    public String viewupdateDC(@RequestParam("id") Integer id, Model model) {
        DiaChi dc = diaChiRepository.getReferenceById(id);
        model.addAttribute("diaChi", dc);
        if (dc != null) {
            // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
            Province province = ghnService.getProvinceByName(dc.getTinhThanhPho());
            List<Disctrict> disctrict = new ArrayList<>();
            List<Ward> wards = new ArrayList<>();
            if (province != null) {
                // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                Disctrict district = ghnService.getDistrictByName(dc.getQuanHuyen(), province.getProvinceID());
                disctrict = ghnService.getDistricts(province.getProvinceID());
                model.addAttribute("selectedDistrict", district);
                if (district != null) {
                    // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                    Ward ward = ghnService.getWardByName(dc.getPhuongXa(), district.getDistrictID());
                    wards = ghnService.getWards(district.getDistrictID());
                    model.addAttribute("selectedWard", ward);
                }
            }
            List<Province> provinces = ghnService.getProvinces();
            model.addAttribute("provinces", provinces);
            model.addAttribute("districts", disctrict);
            model.addAttribute("wards", wards);
            model.addAttribute("selectedProvince", province);
            model.addAttribute("diaChiCuThedc", dc.getDiaChiCuThe());
        }

        return "admin/khach-hang/viewAddDiaChi";
    }


    @PostMapping("/khachhang/adddc")
    public String addDC(
            @RequestParam("id") Integer id, @Valid @ModelAttribute DiaChi dcd
            , @RequestParam(value = "provinceId", required = false) String provinceId
            , @RequestParam(value = "districtId", required = false) String districtId
            , @RequestParam(value = "wardCode", required = false) String wardCode) {


        Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
        Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
        Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
        DiaChi dc = new DiaChi();
        dc.setKhachHang(khachHangRepository.getReferenceById(id));
        dc.setTinhThanhPho(province.getProvinceName());
        dc.setQuanHuyen(disctrict.getDistrictName());
        dc.setPhuongXa(ward.getWardName());
        dc.setDiaChiCuThe(dcd.getDiaChiCuThe());
        dc.setMacDinh(1);
        diaChiRepository.save(dc);
        return "redirect:/admin/khachhang/viewdc?id=" + dc.getKhachHang().getId();
    }
    @PostMapping("/khachhang/updatedc")
    public String updateDC(
            @RequestParam("id") Integer id, @Valid DiaChi dcd, BindingResult bindingResult,Model model
            , @RequestParam(value = "provinceId", required = false) String provinceId
            , @RequestParam(value = "districtId", required = false) String districtId
            , @RequestParam(value = "wardCode", required = false) String wardCode) {
        if (bindingResult.hasErrors()){
            if (provinceId.trim().isEmpty()) {
                model.addAttribute("errortinhThanhPho", "Bạn Chưa điền tỉnh thành phố");
            }
            if (districtId.trim().isEmpty()) {
                model.addAttribute("errortinhQuanHuyen", "Bạn Chưa điền quận huyện");
            }
            if (wardCode.trim().isEmpty()) {
                model.addAttribute("errortinhphuongXa", "Bạn Chưa điền tỉnh phường xã");
            }
            return "admin/khach-hang/viewAddDiaChi";
        }
        Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
        Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
        Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
        DiaChi dc = diaChiRepository.getReferenceById(id);
        dc.setTinhThanhPho(province.getProvinceName());
        dc.setQuanHuyen(disctrict.getDistrictName());
        dc.setPhuongXa(ward.getWardName());
        dc.setDiaChiCuThe(dcd.getDiaChiCuThe());
        diaChiRepository.save(dc);
        return "redirect:/admin/khachhang/viewdc?id=" + dc.getKhachHang().getId();
    }

    @GetMapping("/khachhang/viewadd")
    public String viewKhachHang(Model model) {
        // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
        List<Disctrict> districts = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();
        List<Province> provinces = ghnService.getProvinces();
        model.addAttribute("provinces", provinces);
        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
        model.addAttribute("khachHang", new KhachHang());
        return "admin/khach-hang/viewAddKhachHang";
    }


    @GetMapping("/khachhang/viewupdate")
    public String viewupdateKhachHang(@RequestParam("id") Integer id, Model model) {
        // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
        KhachHang kh = khachHangRepository.getReferenceById(id);
        DiaChi dc = diaChiRepository.findDiaChiDefaultByKhachHangId(id);
        if (kh != null) {
            // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
            Province province = ghnService.getProvinceByName(dc.getTinhThanhPho());
            List<Disctrict> disctrict = new ArrayList<>();
            List<Ward> wards = new ArrayList<>();
            if (province != null) {
                // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
                Disctrict district = ghnService.getDistrictByName(dc.getQuanHuyen(), province.getProvinceID());
                disctrict = ghnService.getDistricts(province.getProvinceID());
                model.addAttribute("selectedDistrict", district);
                if (district != null) {
                    // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                    Ward ward = ghnService.getWardByName(dc.getPhuongXa(), district.getDistrictID());
                    wards = ghnService.getWards(district.getDistrictID());
                    model.addAttribute("selectedWard", ward);
                }
            }
            List<Province> provinces = ghnService.getProvinces();
            model.addAttribute("provinces", provinces);
            model.addAttribute("districts", disctrict);
            model.addAttribute("wards", wards);
            model.addAttribute("selectedProvince", province);
            model.addAttribute("diaChiCuThedc", dc.getDiaChiCuThe());
            model.addAttribute("khachHang", kh);
        }
        return "admin/khach-hang/updateKhachHang";
    }


    @GetMapping("/khachhang/deletedc/{id}")
    public String deletedc(@PathVariable("id") Integer id) {
        DiaChi dc =  diaChiRepository.getReferenceById(id);
        Integer idkh = dc.getKhachHang().getId();
        diaChiRepository.delete(dc);
        return "redirect:/admin/khachhang/viewdc?id="+idkh;
    }
    @PostMapping("/khachhang/update")
    public String updateKhachHang(@Valid KhachHang khachHang, BindingResult bindingResult
            , @RequestParam("id") Integer id
            , @RequestParam(value = "files", required = false) MultipartFile[] files
            , @RequestParam(value = "provinceId", required = false) String provinceId
            , @RequestParam(value = "districtId", required = false) String districtId
            , @RequestParam(value = "wardCode", required = false) String wardCode
            , @RequestParam(value = "diaChiCuThedc", required = false) String diaChiCuThedc
            , @RequestParam(value = "ngaySinh1", required = false) String ngaySinh1
            , Model model) {

        if (bindingResult.hasErrors()) {
            if (provinceId.trim().isEmpty()) {
                model.addAttribute("errortinhThanhPho", "Bạn Chưa điền tỉnh thành phố");
            }
            if (districtId.trim().isEmpty()) {
                model.addAttribute("errortinhQuanHuyen", "Bạn Chưa điền quận huyện");
            }
            if (wardCode.trim().isEmpty()) {
                model.addAttribute("errortinhphuongXa", "Bạn Chưa điền tỉnh phường xã");
            }
            if (diaChiCuThedc.trim().isEmpty()) {
                model.addAttribute("errortinhcuthe", "Bạn Chưa điền địa chỉ cụ thể");
            }

            if (ngaySinh1.trim().isEmpty()) {
                model.addAttribute("errorngaySinh", "Bạn Chưa điền ngày sinh");
            }
            viewupdateKhachHang(id,model);
            return "admin/khach-hang/updateKhachHang";
        }
        try {
            // Chuyển đổi chuỗi ngày sinh thành java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(ngaySinh1);
            java.sql.Date ngaySinh = new java.sql.Date(parsedDate.getTime());
            LocalDate birthDate = ngaySinh.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            if (age < 15) {
                model.addAttribute("errorngaySinh", "Bạn phải ít nhất 15 tuổi để đăng ký");
                return "admin/khach-hang/updateKhachHang"; // Trả về view hiện tại hoặc trang hiển thị lỗi
            }


            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {  // Kiểm tra xem tệp có trống hay không
                    try {
                        String url = uploadImageFile.uploadImage(file);
                        urls.add(url);  // Thêm URL của tệp đã tải lên vào danh sách
                    } catch (IOException e) {
                        e.printStackTrace();  // Xử lý lỗi tải lên
                    }
                } else {
                    // Log thông báo tệp rỗng
                    System.out.println("Tệp rỗng: " + file.getOriginalFilename());
                }
            }


            Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
            Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
            Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
            KhachHang kh = khachHangRepository.getReferenceById(id);
            kh.setTen(khachHang.getTen());
            kh.setNgaySinh(ngaySinh);
            kh.setSdt(khachHang.getSdt());
            kh.setEmail(khachHang.getEmail());
            kh.setGioiTinh(khachHang.getGioiTinh());
            if (!urls.isEmpty()) {
                kh.setHinhAnh(urls.get(0));  // Truy cập phần tử đầu tiên nếu danh sách không rỗng
            }
            khachHangRepository.save(kh);
            DiaChi dc = diaChiRepository.findDiaChiDefaultByKhachHangId(id);
            dc.setTinhThanhPho(province.getProvinceName());
            dc.setQuanHuyen(disctrict.getDistrictName());
            dc.setPhuongXa(ward.getWardName());
            dc.setDiaChiCuThe(diaChiCuThedc);
            dc.setMacDinh(0);
            diaChiRepository.save(dc);
            return "redirect:/admin/khachhang/hienthi";
        } catch (ParseException e) {
            model.addAttribute("errorngaySinh", "Ngày sinh không hợp lệ");
            return "admin/khach-hang/updateKhachHang"; // Trả về view hiện tại hoặc trang hiển thị lỗi
        }
    }

    @PostMapping("/khachhang/add")
    public String addKhachHang(@Valid KhachHang khachHang, BindingResult bindingResult
            , @RequestParam("files") MultipartFile[] files
            , @RequestParam(value = "provinceId", required = false) String provinceId
            , @RequestParam(value = "districtId", required = false) String districtId
            , @RequestParam(value = "wardCode", required = false) String wardCode
            , @RequestParam(value = "diaChiCuThedc", required = false) String diaChiCuThedc
            , @RequestParam(value = "ngaySinh1", required = false) String ngaySinh1
            , Model model) {

        if (bindingResult.hasErrors()) {
            if (provinceId.trim().isEmpty()) {
                model.addAttribute("errortinhThanhPho", "Bạn Chưa điền tỉnh thành phố");
            }
            if (districtId.trim().isEmpty()) {
                model.addAttribute("errortinhQuanHuyen", "Bạn Chưa điền quận huyện");
            }
            if (wardCode.trim().isEmpty()) {
                model.addAttribute("errortinhphuongXa", "Bạn Chưa điền tỉnh phường xã");
            }
            if (diaChiCuThedc.trim().isEmpty()) {
                model.addAttribute("errortinhcuthe", "Bạn Chưa điền địa chỉ cụ thể");
            }

            if (ngaySinh1.trim().isEmpty()) {
                model.addAttribute("errorngaySinh", "Bạn Chưa điền ngày sinh");
            }
            return "admin/khach-hang/viewAddKhachHang";
        }
        try {
            // Chuyển đổi chuỗi ngày sinh thành java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(ngaySinh1);
            java.sql.Date ngaySinh = new java.sql.Date(parsedDate.getTime());
            LocalDate birthDate = ngaySinh.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            if (age < 15) {
                model.addAttribute("errorngaySinh", "Bạn phải ít nhất 15 tuổi để đăng ký");
                return "admin/khach-hang/viewAddKhachHang"; // Trả về view hiện tại hoặc trang hiển thị lỗi
            }


            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {  // Kiểm tra xem tệp có trống hay không
                    try {
                        String url = uploadImageFile.uploadImage(file);
                        urls.add(url);  // Thêm URL của tệp đã tải lên vào danh sách
                    } catch (IOException e) {
                        e.printStackTrace();  // Xử lý lỗi tải lên
                    }
                } else {
                    // Log thông báo tệp rỗng

                    urls.add("https://res.cloudinary.com/drkrb9gk0/image/upload/v1726556120/5b05fb8d-b203-4421-bc49-cd0516fc955b_user%20default.jpg");
                }
            }


            List<String> listma = khachHangRepository.listma();
            Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
            Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
            Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
            String lastma = listma.get(0);
            String prefix = lastma.substring(0, 2);
            int numberPart = Integer.parseInt(lastma.substring(2));

            numberPart++;

            // Đảm bảo mã hóa đơn có độ dài 3 ký tự
            String newNumberPart = String.format("%03d", numberPart);
            khachHangRepository.insertKhachHang(prefix + newNumberPart
                    , khachHang.getTen()
                    , convertToSHA1(khachHang.getSdt())
                    , khachHang.getEmail()
                    , khachHang.getSdt()
                    , ngaySinh
                    , khachHang.getGioiTinh(), "Đang hoạt động", "Khách hàng", "Admin", urls.get(0));
            KhachHang kh = khachHangRepository.findByMa(prefix + newNumberPart);
            DiaChi dc = new DiaChi();
            dc.setTinhThanhPho(province.getProvinceName());
            dc.setQuanHuyen(disctrict.getDistrictName());
            dc.setPhuongXa(ward.getWardName());
            dc.setDiaChiCuThe(diaChiCuThedc);
            dc.setKhachHang(kh);
            dc.setMacDinh(0);
            diaChiRepository.save(dc);
            return "redirect:/admin/khachhang/hienthi";
        } catch (ParseException e) {
            model.addAttribute("errorngaySinh", "Ngày sinh không hợp lệ");
            return "admin/khach-hang/viewAddKhachHang"; // Trả về view hiện tại hoặc trang hiển thị lỗi
        }
    }

    public static String convertToSHA1(String input) {
        try {
            // Tạo instance của MessageDigest với thuật toán SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // Chuyển chuỗi đầu vào thành byte và băm nó
            byte[] messageDigest = md.digest(input.getBytes());

            // Chuyển đổi byte thành định dạng hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Trả về chuỗi đã băm dưới dạng hex
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/khachhang/setmacdinhdc")
    public String setMacDinhDc(@RequestParam("id") Integer iddc) {
        DiaChi dc = diaChiRepository.getReferenceById(iddc);
        List<DiaChi> listdc = diaChiRepository.findDiaChibyKH(dc.getKhachHang().getId());
        for (DiaChi d : listdc) {
            if (d.getMacDinh() == 0) {
                d.setMacDinh(1);
                diaChiRepository.save(d);
            }
        }
        dc.setMacDinh(0);
        diaChiRepository.save(dc);
        return "redirect:/admin/khachhang/viewdc?id=" + dc.getKhachHang().getId();
    }
}
