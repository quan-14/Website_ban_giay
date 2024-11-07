package com.example.demo.repository.admin;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.MauSac;
import com.example.demo.entity.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    @Query("SELECT th FROM ThuongHieu th WHERE th.deleted = 0 ORDER BY th.ngayTao DESC")
    Page<ThuongHieu> findAllActiveThuongHieu(Pageable pageable);

    @Query("SELECT th FROM MauSac th WHERE (th.ma LIKE %:ma% OR th.ten LIKE %:ma%) AND (th.trangThai = :trangThai1 OR th.trangThai = :trangThai2) AND th.deleted = 0 ORDER BY th.ngayTao DESC")
    Page<MauSac> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("SELECT cg FROM ThuongHieu cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<ThuongHieu> getListActiveThuongHieu();

    @Query("select sp from ThuongHieu sp where sp.ma like?1 or sp.ten like ?1" )
    Page<ThuongHieu> search(String nameSearch, Pageable pageable);
}
