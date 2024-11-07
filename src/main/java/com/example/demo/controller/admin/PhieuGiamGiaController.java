package com.example.demo.controller.admin;
import com.example.demo.entity.DataMailDTO;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.KhachHangPhieuGiamGia;
import com.example.demo.entity.PhieuGiamGia;
import com.example.demo.repository.admin.KhachHangPhieuGiamGiaRepository;
import com.example.demo.repository.admin.KhachHangRepository;
import com.example.demo.repository.admin.PhieuGiamGiaRepository;
import com.example.demo.service.admin.EmailService;
import com.example.demo.service.admin.KhachHangService;
import com.example.demo.service.admin.PhieuGiamGiaService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/phieu-giam-gia")
public class PhieuGiamGiaController {

    @Autowired
    PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    KhachHangRepository khachHangRepository;

    @Autowired
    KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

//    private final EntityManager entityManager;

//    public PhieuGiamGiaController(PhieuGiamGiaRepository phieuGiamGiaRepository,
//                                  EntityManager entityManager) {
//        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
//        this.entityManager = entityManager;


    @GetMapping("/hien-thi")
    public String hienthi(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 5;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<PhieuGiamGia> giamGiaPageable = phieuGiamGiaRepository.findAll(pageable);
        model.addAttribute("listsPGG", giamGiaPageable);
//        LocalDateTime data = LocalDateTime.now();
//
//        if(data.isAfter(giamp)){
//
//        }
        model.addAttribute("phieuGiamGia", new PhieuGiamGia());

        return "admin/phieu-giam-gia/phieu-giam-gia";
    }

    @ModelAttribute("khpgg")
    List<KhachHangPhieuGiamGia> getkhPGG() {
        return khachHangPhieuGiamGiaRepository.findAll();
    }
//    @ModelAttribute("maPGG")
//    String getkhPGG() {
//        return "";
//    }

    @GetMapping("/tao-san-pham")
    public String hienThiPGG(Model model, @RequestParam("page") Optional<Integer> page) {
        int checkpage = page.orElse(0);
        int pagesize = 3;
        checkpage = Math.max(checkpage, 0);
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<KhachHang> khachHangPage = khachHangRepository.findAll(pageable);

        model.addAttribute("listsKH", khachHangPage);
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        phieuGiamGia.setMa(phieuGiamGiaService.generateRandomCode());
        model.addAttribute("phieuGiamGia", phieuGiamGia);
        return "admin/phieu-giam-gia/create-pgg";
    }


    @PostMapping("/add")
    public String addPGG(@Valid PhieuGiamGia phieuGiamGia, BindingResult bindingResult,
                         @RequestParam(name = "khachHang", required = true, defaultValue = "-1") List<KhachHang> selectedItemsIds,
                         Model model, @RequestParam("page") Optional<Integer> page) throws MessagingException, MessagingException {


        if (bindingResult.hasErrors()) {
            if (phieuGiamGia.getHinhThucGiam() != null && phieuGiamGia.getSoTienGiam() != null) {
                if (phieuGiamGia.getHinhThucGiam() == 0) {
                    phieuGiamGia.setPhanTramGiam(null);
                    if (phieuGiamGia.getSoTienGiam() < 0 && phieuGiamGia.getHinhThucGiam() == 0) {
                        model.addAttribute("errorSoTienGiam", "Số tiền phải lớn hơn 0");
                    }
                }
                if (phieuGiamGia.getHinhThucGiam() == 1) {
                    phieuGiamGia.setPhanTramGiam(phieuGiamGia.getSoTienGiam());
                    if (phieuGiamGia.getPhanTramGiam() < 1 || phieuGiamGia.getPhanTramGiam() > 99 ) {
                        model.addAttribute("errorPhanTramGiam", "Phần trăm giảm chỉ khoảng 1% - 99%");
                    }else{
                        phieuGiamGia.setSoTienGiam(null);
                    }
                }

            } else {
                model.addAttribute("errorHinhThucGiam", "Bạn chưa chọn hình thưc giảm giá");
            }

            if (phieuGiamGia.getNgayBatDau() != null && phieuGiamGia.getNgayKetThuc() != null) {
                LocalDateTime now = LocalDateTime.now();
                if (phieuGiamGia.getNgayBatDau().isAfter(phieuGiamGia.getNgayKetThuc())) {
                    model.addAttribute("errorKetThuc", "Không được để ngày bắt đầu hơn ngày kết thúc");
                }else if(phieuGiamGia.getNgayKetThuc().isBefore(now)){
                    model.addAttribute("errorKetThucMinNow", "Không được để ngày kết thúc bé hơn hôm hiện tại");
                }
            }

            model.addAttribute("phieuGiamGia", phieuGiamGia);
            int checkpage = page.orElse(0);
            int pagesize = 3;
            checkpage = Math.max(checkpage, 0);
            Pageable pageable = PageRequest.of(checkpage, pagesize);
            Page<KhachHang> khachHangPage = khachHangRepository.findAll(pageable);

            model.addAttribute("listsKH", khachHangPage);
            return "admin/phieu-giam-gia/create-pgg";
        }
        phieuGiamGia.setNgaySua(new Date());
        phieuGiamGia.setNgayTao(new Date());
        phieuGiamGia.setNguoiTao("Admin");

        LocalDateTime now = LocalDateTime.now();
        if (phieuGiamGia.getNgayBatDau() == null || phieuGiamGia.getNgayKetThuc() == null) {
            now = null;
        } else {
            if (now.isBefore(phieuGiamGia.getNgayBatDau())) {
                phieuGiamGia.setTrangThai("Sắp diễn ra");} else if (now.isAfter(phieuGiamGia.getNgayBatDau()) && now.isBefore(phieuGiamGia.getNgayKetThuc())) {
                phieuGiamGia.setTrangThai("Đang diễn ra");
            }

        }


//        if(phieuGiamGia.getMa().trim().isEmpty()){
//            phieuGiamGia.setMa(phieuGiamGiaService.generateRandomCode());
//        }
        phieuGiamGiaRepository.save(phieuGiamGia);
        KhachHangPhieuGiamGia khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();

        if (phieuGiamGia.getHinhThucSuDung() == 0) {
            selectedItemsIds.remove(0);
        }

//        if(selectedItemsIds.size()>0){
        for (int i = 0; i < selectedItemsIds.size(); i++) {
            khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();
            khachHangPhieuGiamGia.setKhachHang(selectedItemsIds.get(i));
            khachHangPhieuGiamGia.setGiamGia(phieuGiamGia);
            khachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
//            String to = "baontph39509@fpt.edu.vn";
//            String subject = "Chúc mừng bạn đã nhận được một phiếu giảm giá từ ChinShoes!";
//            String body = "Mã '" + phieuGiamGia.getMa() + "' sẽ được áp dụng từ ngày :" + phieuGiamGia.getNgayBatDau() + " đến hết ngày:" + phieuGiamGia.getNgayKetThuc() + "\n" +
//                    "Với mã này bạn có thế áp dụng cho các sản phẩm với múc ưu đãi " + phieuGiamGia.getTen() + "\n" +
//                    "Xin cám ơn!";
//            emailService.sendEmail(to, subject, body);
            try {
                DataMailDTO dataMail = new DataMailDTO();
                dataMail.setTo(selectedItemsIds.get(i).getEmail());
                dataMail.setSubject("[ChinShoes] Bạn đã nhận được một phiếu giảm giá ✅");
                Map<String, Object> props = new HashMap<>();
                props.put("pgg", phieuGiamGia);
                props.put("kh", selectedItemsIds.get(i));
                dataMail.setProps(props);
                emailService.sendHtmlMail(dataMail, "admin/phieu-giam-gia/email-template-pgg");

            }catch (Exception e){
                System.out.println(selectedItemsIds.get(i).getEmail()+" của khách hàng có sdt "+selectedItemsIds.get(i).getSdt() +" bị lỗi");
            }

        }
//        }

        return "redirect:/admin/phieu-giam-gia/hien-thi";

    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam(required = false) Integer kieu,
                         @RequestParam(required = false) Integer loai,
                         @RequestParam(required = false) String trangThai,
                         @RequestParam("page") Optional<Integer> page,
                         Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<PhieuGiamGia> results = phieuGiamGiaRepository.searchPGG(startDate, endDate, kieu, loai, trangThai, pageable);
        model.addAttribute("listsPGG", results);
        model.addAttribute("phieuGiamGia", results);

        return "admin/phieu-giam-gia/phieu-giam-gia";

    }

    @PostMapping("/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> toggleStatus(@PathVariable int id) {
        String newStatus = phieuGiamGiaService.toggleStatus(id);
        Map<String, String> response = new HashMap<>();
        response.put("newStatus", newStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/update/{id}")
    public String viewUpdate(@PathVariable("id") Integer id, Model model, @RequestParam("page") Optional<Integer> page) {
        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.getReferenceById(id);
        model.addAttribute("phieuGiamGia", phieuGiamGia);
        int checkpage = page.orElse(0);
        int pagesize = 3;
        Pageable pageable = PageRequest.of(checkpage, pagesize);
        Page<KhachHang> khachHangPage = khachHangRepository.findAll(pageable);
        model.addAttribute("listsKH", khachHangPage);
        return "admin/phieu-giam-gia/update-pgg";
    }


    @PostMapping("/update/")
    public String update(PhieuGiamGia phieuGiamGia,
                         @RequestParam(name = "khachHang", required = true, defaultValue = "-1") List<KhachHang> selectedItemsIds
    ) {

        phieuGiamGia.setNgaySua(new Date());
        phieuGiamGia.setNgayTao(phieuGiamGia.getNgayTao());
        phieuGiamGia.setNguoiTao("Admin");

        LocalDateTime data = LocalDateTime.now();
        if (phieuGiamGia.getNgayBatDau() == null || phieuGiamGia.getNgayKetThuc() == null) {
            data = null;
        } else {
            if (data.isBefore(phieuGiamGia.getNgayBatDau())) {
                phieuGiamGia.setTrangThai("Sắp diễn ra");
            } else if (data.isAfter(phieuGiamGia.getNgayBatDau()) && data.isBefore(phieuGiamGia.getNgayKetThuc())) {
                phieuGiamGia.setTrangThai("Đang diễn ra");
            }

        }

        if (phieuGiamGia.getHinhThucGiam() == 0) {
            phieuGiamGia.setPhanTramGiam(null);
        }
        if (phieuGiamGia.getHinhThucGiam() == 1) {
            phieuGiamGia.setPhanTramGiam(phieuGiamGia.getSoTienGiam());
            phieuGiamGia.setSoTienGiam(null);
        }
//        if(phieuGiamGia.getMa().trim().isEmpty()){
//            phieuGiamGia.setMa(phieuGiamGiaService.generateRandomCode());
//        }
        phieuGiamGiaRepository.save(phieuGiamGia);
        KhachHangPhieuGiamGia khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();

        if (phieuGiamGia.getHinhThucSuDung() == 1) {
            selectedItemsIds.remove(0);
        }

        for (int i = 0; i < selectedItemsIds.size(); i++) {
            khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();
            khachHangPhieuGiamGia.setKhachHang(selectedItemsIds.get(i));
            khachHangPhieuGiamGia.setGiamGia(phieuGiamGia);
            khachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
            String to = "baontph39509@fpt.edu.vn";
            String subject = "Chúc mừng bạn đã nhận được một phiếu giảm giá bên ChinShoes";
            String body = "Mã '" + phieuGiamGia.getMa() + "' sẽ được áp dụng từ ngày :" + phieuGiamGia.getNgayBatDau() + " đến hết ngày:" + phieuGiamGia.getNgayKetThuc() + "\n" +
                    "Với mã này bạn có thế áp dụng cho các sản phẩm với múc ưu đãi " + phieuGiamGia.getTen() + "\n" +
                    "Xin cám ơn!";
            emailService.sendEmail(to, subject, body);

        }
        return "";
    }
}
