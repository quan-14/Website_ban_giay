package com.example.demo.service.admin;

import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SanPhamChiTietService {

    Page<SanPhamChiTiet> getPageSPCT(Integer idSanPham, Pageable pageable);

    List<SanPhamChiTiet> getListSPCT(Integer idSanPham);

    public Page<SanPhamChiTiet> findProductsByCriteria(Integer chatLieuId,Integer coGiayId,
                                                       Integer deGiayId,Integer thuongHieuId,
                                                       String trangThai,Integer mauSacId,
                                                       Integer kichCoId, Integer sanPhamId, String khoangGia,Pageable pageable) ;
}
