package com.example.demo.repository.admin;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.DeGiay;
import com.example.demo.entity.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeGiayRepository extends JpaRepository<DeGiay, Integer> {
    @Query("SELECT dg FROM DeGiay dg WHERE dg.deleted = 0 ORDER BY dg.ngayTao DESC")
    Page<DeGiay> getAllActiveDeGiay(Pageable pageable);

    @Query("SELECT cg FROM DeGiay cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<DeGiay> getListActiceDeGiays();

    @Query("SELECT sz FROM DeGiay sz WHERE (sz.ma LIKE %:ma% OR sz.ten LIKE %:ma%) AND (sz.trangThai = :trangThai1 OR sz.trangThai = :trangThai2) AND sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    Page<DeGiay> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("select sp from DeGiay sp where sp.ma like?1 or sp.ten like ?1" )
    Page<DeGiay> search(String nameSearch, Pageable pageable);

}
