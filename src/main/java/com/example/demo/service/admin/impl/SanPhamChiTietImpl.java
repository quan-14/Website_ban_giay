package com.example.demo.service.admin.impl;

import com.example.demo.dto.ProductSpecification;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.repository.admin.SanPhamChiTietRepository;
import com.example.demo.service.admin.SanPhamChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanPhamChiTietImpl implements SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Override
    public Page<SanPhamChiTiet> getPageSPCT(Integer idSanPham, Pageable pageable) {
        return sanPhamChiTietRepository.getPageSPCT(idSanPham, pageable);
    }

    public List<SanPhamChiTiet> getListSPCT(Integer idSanPham) {
        return null;
    }

    @Override
    public Page<SanPhamChiTiet> findProductsByCriteria(Integer chatLieuId, Integer coGiayId, Integer deGiayId, Integer thuongHieuId, String trangThai, Integer mauSacId, Integer kichCoId,Integer sanPhamId, String khoangGia, Pageable pageable) {
        ProductSpecification spec = new ProductSpecification(chatLieuId, coGiayId, deGiayId, thuongHieuId, trangThai, mauSacId, kichCoId, sanPhamId, khoangGia);
        return sanPhamChiTietRepository.findAll(spec,pageable);
    }
}
