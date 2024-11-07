package com.example.demo.repository.user;

import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    @Query("select sp from SanPhamChiTiet sp where sp.sanPham.ten like ?1")
    List<SanPhamChiTiet> search(String nameSearch);

    @Query("SELECT s FROM SanPhamChiTiet s WHERE s.sanPham.id = :sanPhamId ORDER BY s.soLuong DESC")
    List<SanPhamChiTiet> findBySanPham(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT s FROM SanPhamChiTiet s WHERE s.id = :id")
    SanPhamChiTiet findSanPhamChiTietById(@Param("id") Integer id);
}
