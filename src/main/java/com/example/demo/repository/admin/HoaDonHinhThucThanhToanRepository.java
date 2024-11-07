package com.example.demo.repository.admin;

import com.example.demo.entity.HinhThucThanhToan;
import com.example.demo.entity.HinhThucThanhToanHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonHinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToanHoaDon,Integer> {
    List<HinhThucThanhToanHoaDon> findAllByHoaDon_Id(Integer idhoadon);
    @Query("select httt.hinhThucThanhToan from HinhThucThanhToanHoaDon httt where httt.hoaDon.id=:id")
    List<HinhThucThanhToan> listfindhttt(Integer id);
}
