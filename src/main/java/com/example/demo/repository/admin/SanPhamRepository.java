package com.example.demo.repository.admin;

import com.example.demo.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query("SELECT sp FROM SanPham sp WHERE sp.deleted = 0 ORDER BY sp.ngayTao DESC")
    Page<SanPham> findAllActiveSanPham(Pageable pageable);

    @Query("SELECT cg FROM SanPham cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<SanPham> getListActiveSanPham();


    @Query(value = "select s from SanPham s where (s.ma like ?1 or s.ten like ?1)")
    Page<SanPham> findByParam(String search, Pageable pageable);


    @Query(value = "select s from SanPham s where (s.ma like ?1 or s.ten like ?1) and s.trangThai = ?2")
    Page<SanPham> findByParamAndTrangThai(String search, String trangThai, Pageable pageable);

}
