package com.example.demo.repository.admin;

import com.example.demo.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {
    @Query("select lshd from LichSuHoaDon lshd where lshd.hoaDon.id = ?1 and lshd.deleted = 0 order by lshd.ngayTao asc")
    List<LichSuHoaDon> findAllByHoaDon_Id(Integer idhoadon);
    @Query("select lshd from LichSuHoaDon lshd where lshd.hoaDon.id = ?1 order by lshd.ngayTao asc")
    List<LichSuHoaDon> findAllByHoaDon_Id1(Integer idhoadon);
}
