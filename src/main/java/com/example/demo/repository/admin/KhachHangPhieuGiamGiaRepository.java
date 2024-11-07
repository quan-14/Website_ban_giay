package com.example.demo.repository.admin;

import com.example.demo.entity.KhachHangPhieuGiamGia;
import com.example.demo.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangPhieuGiamGiaRepository extends JpaRepository<KhachHangPhieuGiamGia,Integer> {
    @Query("select khpgg.giamGia from KhachHangPhieuGiamGia khpgg where khpgg.khachHang.id = :id")
    List<PhieuGiamGia> lstpggkh(Integer id);
}
