package com.example.demo.api;

import com.example.demo.entity.HinhAnh;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.entity.SanPhamChiTietDTO;
import com.example.demo.repository.admin.HinhAnhRepository;
import com.example.demo.repository.admin.SanPhamChiTietRepository;
import com.example.demo.service.admin.impl.UploadImageFileImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/product")
public class ProductAPI {
    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private UploadImageFileImpl cloudinaryService;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @GetMapping("/public/get-detail")
    public ResponseEntity<?> getListDetail(@RequestParam Integer idSanPham){
        List<SanPhamChiTiet> list = sanPhamChiTietRepository.findBySanPham(idSanPham);
        HashMap<String, List<SanPhamChiTiet>> hashMap = new HashMap<>();

        for(SanPhamChiTiet s : list){
            String color = s.getMauSac().getTen();
            if(hashMap.containsKey(color) == false){
                List<SanPhamChiTiet> spcts = new ArrayList<>();
                spcts.add(s);
                hashMap.put(color, spcts);
            }
            else{
                List<SanPhamChiTiet> spcts = hashMap.get(color);
                spcts.add(s);
            }
        }
        return new ResponseEntity<>(hashMap, HttpStatus.OK);
    }
    @GetMapping("/public/san-pham-chi-tiet-by-id")
    public ResponseEntity<?> findBySanPhamChiTietId(@RequestParam Integer id){
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
        return new ResponseEntity<>(sanPhamChiTiet, HttpStatus.OK);
    }

    @PostMapping("/public/update-chi-tiet-san-pham")
    public void updateChiTietSanPham(@RequestBody List<SanPhamChiTietDTO> list){
        for(SanPhamChiTietDTO s : list){
            SanPhamChiTiet sp = sanPhamChiTietRepository.findById(s.getIdCtsp()).get();
            sp.setSoLuong(s.getSoLuong());
            sp.setDonGia(s.getDonGia());
            sanPhamChiTietRepository.save(sp);
        }
    }

    @DeleteMapping("/public/delete-chi-tiet-san-pham")
    public void delete(@RequestParam Integer id){
        sanPhamChiTietRepository.deleteById(id);
    }

//    @PostMapping("/public/upload-multiple-file")
//    public List<HinhAnh> uploadFile(@RequestParam("file") List<MultipartFile> file, @RequestParam Integer id){
//        List<String> list = new ArrayList<>();
//        ExecutorService es = Executors.newCachedThreadPool();
//        for(int i=0; i<file.size(); i++) {
//            Integer x=i;
//            es.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String image = cloudinaryService.uploadFile(file.get(x));
//                        list.add(image);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//        es.shutdown();
//        try {
//            boolean finished = es.awaitTermination(100000, TimeUnit.MINUTES);
//            if (finished) {
//
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
//        List<HinhAnh> result = new ArrayList<>();
//        for(String s : list){
//            HinhAnh hinhAnh = new HinhAnh();
//            hinhAnh.setDuongDan(s);
//            hinhAnh.setNgayTao(new Date(System.currentTimeMillis()));
//            hinhAnh.setSanPhamChiTiet(sanPhamChiTiet);
//            hinhAnhRepository.save(hinhAnh);
//            result.add(hinhAnh);
//        }
//        return result;
//    }
    @PostMapping("/public/upload-multiple-file")
    public List<HinhAnh> uploadFile(@RequestParam("file") List<MultipartFile> file, @RequestParam String ids){
        List<String> list = new ArrayList<>();
        ExecutorService es = Executors.newCachedThreadPool();
        for(int i=0; i<file.size(); i++) {
            Integer x=i;
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String image = cloudinaryService.uploadFile(file.get(x));
                        list.add(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
        try {
            boolean finished = es.awaitTermination(100000, TimeUnit.MINUTES);
            if (finished) {

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String str[] = ids.split(",");
        for(String sr : str){
            Integer id = Integer.valueOf(sr);
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(id).get();
            List<HinhAnh> result = new ArrayList<>();
            for(String s : list){
                HinhAnh hinhAnh = new HinhAnh();
                hinhAnh.setDuongDan(s);
                hinhAnh.setNgayTao(new Date(System.currentTimeMillis()));
                hinhAnh.setSanPhamChiTiet(sanPhamChiTiet);
                hinhAnhRepository.save(hinhAnh);
                result.add(hinhAnh);
            }
        }

        return null;
    }
}
