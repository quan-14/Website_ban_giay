package com.example.demo.repository.user;

import com.example.demo.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    @Query("select g from GioHangChiTiet g where g.gioHang.id = ?1")
    List<GioHangChiTiet> findByGioHang(Integer idGioHang);

    @Query("select count(g.id) from GioHangChiTiet g where g.gioHang.id = ?1")
    Long countByGioHang(Integer gioHangId);

    @Query("select g from GioHangChiTiet g where g.gioHang.id = ?1 and g.sanPhamChiTiet.id = ?2")
    GioHangChiTiet findByGioHangAndCtsp(Integer id, Integer idSpct);
}
