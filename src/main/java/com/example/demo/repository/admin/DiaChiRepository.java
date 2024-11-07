package com.example.demo.repository.admin;

import com.example.demo.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
    @Query("select dc from DiaChi dc where dc.khachHang.id = :idkh and dc.macDinh = 0")
    DiaChi findDiaChiDefaultByKhachHangId(Integer idkh);
    @Query("select dc from DiaChi dc where dc.khachHang.id = :idkh")
    List<DiaChi> findDiaChibyKH(Integer idkh);
}
