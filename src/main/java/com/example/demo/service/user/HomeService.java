package com.example.demo.service.user;

import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.repository.admin.SanPhamRepository;
import com.example.demo.repository.user.HomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {
    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    public List<SanPhamChiTiet> getUniqueSanPhamChiTiets() {
        List<SanPhamChiTiet> result = new ArrayList<>();
        List<SanPham> sanPhams = sanPhamRepository.findAll();
        for (SanPham sp : sanPhams) {
            List<SanPhamChiTiet> spctList = homeRepository.findBySanPham(sp.getId());
            if (!spctList.isEmpty()) {
                result.add(spctList.get(0));
            }
        }
        return result;
    }

    public List<SanPhamChiTiet> getTop5UniqueSanPhamChiTiets() {
        List<SanPhamChiTiet> result = new ArrayList<>();
        List<SanPham> sanPhams = sanPhamRepository.findAll();

        for (SanPham sp : sanPhams) {
            List<SanPhamChiTiet> spctList = homeRepository.findBySanPham(sp.getId());
            if (!spctList.isEmpty()) {
                result.add(spctList.get(0));
            }
        }

        result.sort(Comparator.comparing(SanPhamChiTiet::getNgayTao).reversed());

        return result.stream().limit(5).collect(Collectors.toList());
    }

    public List<SanPhamChiTiet> getTop5ProductSanPhamChiTiets() {
        List<SanPhamChiTiet> result = new ArrayList<>();
        List<SanPham> sanPhams = sanPhamRepository.findAll();

        for (SanPham sp : sanPhams) {
            List<SanPhamChiTiet> spctList = homeRepository.findBySanPham(sp.getId());
            if (!spctList.isEmpty()) {
                result.add(spctList.get(0));
            }
        }

        result.sort(Comparator.comparing(SanPhamChiTiet::getSoLuong).reversed());

        return result.stream().limit(5).collect(Collectors.toList());
    }

}
