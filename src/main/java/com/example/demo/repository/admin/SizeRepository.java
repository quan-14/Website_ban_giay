package com.example.demo.repository.admin;

import com.example.demo.entity.ChatLieu;
import com.example.demo.entity.CoGiay;
import com.example.demo.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
    @Query("SELECT sz FROM Size sz WHERE sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    Page<Size> getAllActiveSize(Pageable pageable);

    @Query("SELECT sz FROM Size sz WHERE (sz.ma LIKE %:ma% OR sz.ten LIKE %:ma%) AND (sz.trangThai = :trangThai1 OR sz.trangThai = :trangThai2) AND sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    Page<Size> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("SELECT sz FROM Size sz WHERE sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    List<Size> getListActiveSize();

    @Query("select sp from Size sp where sp.ma like?1 or sp.ten like ?1" )
    Page<Size> search(String nameSearch, Pageable pageable);

}
