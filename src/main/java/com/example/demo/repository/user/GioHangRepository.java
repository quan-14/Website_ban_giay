package com.example.demo.repository.user;

import com.example.demo.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    @Query("select g from GioHang g where g.khachHang.id = ?1")
    Optional<GioHang> findByKhachHang(Integer idKhachHang);

}
