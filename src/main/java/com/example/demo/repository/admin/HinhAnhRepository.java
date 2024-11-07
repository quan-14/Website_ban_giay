package com.example.demo.repository.admin;

import com.example.demo.entity.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {
    @Query("select ha.duongDan from HinhAnh ha where ha.sanPhamChiTiet.id = :id order by ha.ngayTao desc ")
    public List<String> hinhAnhDuongDan(Integer id);
}
