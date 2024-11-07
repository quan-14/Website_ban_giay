package com.example.demo.controller.admin;

import com.example.demo.entity.*;
import com.example.demo.repository.admin.DiaChiRepository;
import com.example.demo.repository.admin.NhanVienRepository;
import com.example.demo.service.admin.EmailService;
import com.example.demo.service.admin.UploadImageFile;
import com.example.demo.service.admin.impl.GhnService;
import com.example.demo.service.admin.impl.NhanVienService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class NhanVienController {


    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private GhnService ghnService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UploadImageFile uploadImageFile;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DiaChiRepository diaChiRepository;

    public NhanVienController() {
    }


    @GetMapping("/nhan-vien/hien-thi")
    public String view(Model model, @RequestParam(defaultValue = "") String search1,
                       @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date1
            , @RequestParam(defaultValue = "", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date2
            , @RequestParam(required = false) String loaihoadontim,
                       @RequestParam(name = "page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 5;
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

        Page<NhanVien> nhanVienPage = nhanVienRepository.searchNhanVien(search1.trim(), loai1Int, date1, date2, pageable);
        model.addAttribute("listNV", nhanVienPage);
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("search1", search1);
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("loaihoadontim", loaihoadontim != null ? loaihoadontim : "");
        return "admin/nhan-vien/nhanVien";
    }


    @GetMapping("/nhan-vien/viewAddNhanVien")
    public String viewNhanVien(@ModelAttribute("nhanVien") NhanVien nhanVien, Model model) {
        List<String> trangThaiList = Arrays.asList("Đang làm việc", "Ngừng Làm Việc");

        Province province = ghnService.getProvinceByName(nhanVien.getTinhThanhPho());
        List<Disctrict> districts = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();
        if (province != null) {
            // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
            Disctrict district = ghnService.getDistrictByName(nhanVien.getQuanHuyen(), province.getProvinceID());
            if (district != null) {
                // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                Ward ward = ghnService.getWardByName(nhanVien.getPhuongXa(), district.getDistrictID());
                wards = ghnService.getWards(district.getDistrictID());
                model.addAttribute("selectedWard", ward);
            }
            districts = ghnService.getDistricts(province.getProvinceID());
            model.addAttribute("selectedDistrict", district);
        }

        List<Province> provinces = ghnService.getProvinces();

        model.addAttribute("provinces", provinces);
        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
        model.addAttribute("selectedProvince", province);
        model.addAttribute("trangThaiList", trangThaiList);
        return "admin/nhan-vien/viewAddNhanVien";
    }


    @ModelAttribute("listNhanVien")
    public List<NhanVien> listNhanVien() {
        return nhanVienRepository.findAll();
    }


    @PostMapping("/nhan-vien/addNhanVien")
    public String addNhanVien(
            Model model,
            @Valid NhanVien nhanVien,
            BindingResult bindingResult,
            @RequestParam("ten") String ten,
            @RequestParam("email") String email,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("ngaySinh") String ngaySinh1,
            @RequestParam("provinceId") String provinceId,
            @RequestParam("districtId") String districtId,
            @RequestParam("wardCode") String wardCode,
            @RequestParam("diaChiCuThe") String diaChiCuThe,
            @RequestParam(value = "gioiTinh") int gioiTinh,
//            @RequestParam(value = "trangThai") String trangThai,
            @RequestParam(value = "vaiTro", required = false, defaultValue = "ROLE_EMPLOYEE") String vaiTro,
            @RequestParam(value = "nguoiTao", required = false, defaultValue = "ADMIN") String nguoiTao,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes) throws MessagingException {


        if (bindingResult.hasErrors()) {
            if (nhanVienService.isEmailExists(nhanVien.getEmail())){
                redirectAttributes.addFlashAttribute("messageerorEmail", "Email đã tồn tại");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (soDienThoai.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền số điện thoại");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (email.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền Email");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if(ten.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tên");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (provinceId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tỉnh thành phố");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (districtId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền quận huyện");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (wardCode.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tỉnh phường xã");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (diaChiCuThe.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền địa chỉ cụ thể");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            if (ngaySinh1.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền ngày sinh");
                return "redirect:/admin/nhan-vien/viewAddNhanVien";
            }
            return "admin/nhan-vien/viewAddNhanVien";
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(ngaySinh1);
            java.sql.Date ngaySinh = new java.sql.Date(parsedDate.getTime());
            LocalDate birthDate = ngaySinh.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();
            if (age < 15) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn phải ít nhất 15 tuổi để đăng ký");
                return "redirect:/admin/nhan-vien/viewAddNhanVien"; // Trả về view hiện tại hoặc trang hiển thị lỗi
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
            Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
            Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
            Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
            String ma = generateMa();
            String matKhau = generateRandomPassword();
            String matKhauMaHoa = passwordEncoder.encode(matKhau);
            nhanVienService.createNhanVien(ma, ten, matKhauMaHoa, email, soDienThoai, ngaySinh, province.getProvinceName(), disctrict.getDistrictName(), ward.getWardName(), diaChiCuThe, gioiTinh, "Đang làm việc", vaiTro, nguoiTao, urls.get(0));
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo(email);
            dataMail.setSubject("[ChinShoes] Tài Khoản Của Bạn Đã Được Tạo ✅");
            Map<String, Object> props = new HashMap<>();
            props.put("tenNhanVien", ten);
            props.put("matKhau", matKhau);
            dataMail.setProps(props);
            emailService.sendHtmlMail(dataMail, "admin/nhan-vien/email-nhan-vien-sender");
            String to = email;
            String subject = "Chào " + ten + " bạn vừa tạo tài khoản thành công Shop bán giày thể thao ChinShoes";
            String body = "Mật khẩu của bạn là: " + matKhau + ". Vui lòng đổi mật khẩu, nếu không chúng tôi không chịu trách nhiệm!";
            emailService.sendEmail(to, subject, body);
            redirectAttributes.addFlashAttribute("messagesucces", "Đăng ký tài khoản thành công");
            return "redirect:/admin/nhan-vien/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageeror", "Ngày sinh không hợp lệ");
            return "admin/nhan-vien/viewAddNhanVien";
        }


    }

    private String generateMa() {
        return "NV-" + UUID.randomUUID().toString();
    }

    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    @PostMapping("/nhan-vien/toggle-status/{id}")
    public String toggleStatus(@PathVariable int id) {
        nhanVienService.toggleStatus(id);
        return "redirect:/admin/nhan-vien/hien-thi";
    }

    @GetMapping("/nhan-vien/view-update/{id}")
    public String viewUpdate(@PathVariable Integer id, Model model) {
//        DiaChi diaChi = diaChiRepository.getReferenceById(id);
        NhanVien nhanVien = nhanVienRepository.getReferenceById(id);
        // Lấy thông tin tỉnh/thành phố từ GHN API dựa trên tên
        Province province = ghnService.getProvinceByName(nhanVien.getTinhThanhPho());
        List<Disctrict> districts = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();

        if (province != null) {
            // Lấy thông tin quận/huyện từ GHN API dựa trên tên và ID tỉnh/thành phố
            Disctrict district = ghnService.getDistrictByName(nhanVien.getQuanHuyen(), province.getProvinceID());

            if (district != null) {
                // Lấy thông tin phường/xã từ GHN API dựa trên tên và ID quận/huyện
                Ward ward = ghnService.getWardByName(nhanVien.getPhuongXa(), district.getDistrictID());
                wards = ghnService.getWards(district.getDistrictID());
                model.addAttribute("selectedWard", ward);
            }
            districts = ghnService.getDistricts(province.getProvinceID());
            model.addAttribute("selectedDistrict", district);
        }

        List<Province> provinces = ghnService.getProvinces();
        model.addAttribute("provinces", provinces);
        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
        model.addAttribute("selectedProvince", province);
        model.addAttribute("nhanVien", nhanVien);
        return "admin/nhan-vien/updateNhanVien";
    }

    @PostMapping("/nhan-vien/update/{id}")
    public String update(@ModelAttribute("nhanVien") @Valid NhanVien nhanVien, @PathVariable("id") Integer id, BindingResult bindingResult, Model model,
                         @RequestParam(value = "files", required = false) MultipartFile[] files,
                         @RequestParam("provinceId") String provinceId,
                         @RequestParam("districtId") String districtId,
                         @RequestParam("wardCode") String wardCode,
                         @RequestParam("ten") String ten,
                         @RequestParam("email") String email,
                         @RequestParam("soDienThoai") String soDienThoai,
                         @RequestParam("ngaySinh") String ngaySinh1,
                         @RequestParam("diaChiCuThe") String diaChiCuThe,
                         @RequestParam(value = "gioiTinh") int gioiTinh,
//                         @RequestParam(value = "vaiTro", required = false, defaultValue = "Nhân Viên Bán Hàng") String vaiTro,
                         @RequestParam(value = "trangThai") String trangThai,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            if (soDienThoai.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền số điện thoại");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (email.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền Email");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if(ten.trim().isEmpty()){
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tên");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (provinceId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tỉnh thành phố");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (districtId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền quận huyện");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (wardCode.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền tỉnh phường xã");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (diaChiCuThe.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền địa chỉ cụ thể");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            if (ngaySinh1.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn Chưa điền ngày sinh");
                return "redirect:/admin/nhan-vien/view-update/{id}";
            }
            return "admin/nhan-vien/updateNhanVien";
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(ngaySinh1);
            java.sql.Date ngaySinh = new java.sql.Date(parsedDate.getTime());
            LocalDate birthDate = ngaySinh.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            if (age < 15) {
                redirectAttributes.addFlashAttribute("messageeror", "Bạn phải ít nhất 15 tuổi để đăng ký");
                return "redirect:/admin/nhan-vien/view-update/{id}"; // Trả về view hiện tại hoặc trang hiển thị lỗi
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
            NhanVien nv = nhanVienRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id trong" + id));
            Province province = ghnService.getProvinceByID(Integer.parseInt(provinceId));
            Disctrict disctrict = ghnService.getDistrictByID(Integer.parseInt(districtId), Integer.parseInt(provinceId));
            Ward ward = ghnService.getWardByID(wardCode, Integer.parseInt(districtId));
            nhanVien = nv;
            nhanVien.setTen(ten);
            nhanVien.setNgaySinh(ngaySinh);
            nhanVien.setSoDienThoai(soDienThoai);
            nhanVien.setTrangThai(trangThai);
            nhanVien.setDiaChiCuThe(diaChiCuThe);
            nhanVien.setTinhThanhPho(province.getProvinceName());
            nhanVien.setQuanHuyen(disctrict.getDistrictName());
            nhanVien.setPhuongXa(ward.getWardName());
            nhanVien.setEmail(email);
            nhanVien.setId(id);
            if (!urls.isEmpty()) {
                nhanVien.setHinhAnh(urls.get(0));  // Truy cập phần tử đầu tiên nếu danh sách không rỗng
            }
            nhanVienRepository.save(nhanVien);
            return "redirect:/admin/nhan-vien/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageeror", "Ngày sinh không hợp lệ");
            return "admin/nhan-vien/updateNhanVien";
        }

    }
}
