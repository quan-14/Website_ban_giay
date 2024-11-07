package com.example.demo.repository.admin;

import com.example.demo.entity.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.deleted = 0 ORDER BY dm.ngayTao DESC")
    Page<DanhMuc> getAllActiveDanhMuc(Pageable pageable);

    @Query("SELECT cg FROM DanhMuc cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<DanhMuc> getListActiveDanhMucs();

    @Query("SELECT sz FROM DanhMuc sz WHERE (sz.ma LIKE %:ma% OR sz.ten LIKE %:ma%) AND (sz.trangThai = :trangThai1 OR sz.trangThai = :trangThai2) AND sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    Page<DanhMuc> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("select sp from DanhMuc sp where sp.ma like?1 or sp.ten like ?1" )
    Page<DanhMuc> search(String nameSearch, Pageable pageable);

}
