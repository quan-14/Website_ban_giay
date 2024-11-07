package com.example.demo.repository.admin;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    @Query("SELECT ms FROM MauSac ms WHERE ms.deleted = 0 ORDER BY ms.ngayTao DESC")
    Page<MauSac> getAllActiveMauSac(Pageable pageable);

    @Query("SELECT ms FROM MauSac ms WHERE (ms.ma LIKE %:ma% OR ms.ten LIKE %:ma%) AND (ms.trangThai = :trangThai1 OR ms.trangThai = :trangThai2) AND ms.deleted = 0 ORDER BY ms.ngayTao DESC")
    Page<MauSac> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("SELECT ms FROM MauSac ms WHERE ms.deleted = 0 ORDER BY ms.ngayTao DESC")
    List<MauSac> getListActiveMauSac();

    @Query("select sp from MauSac sp where sp.ma like?1 or sp.ten like ?1")
    Page<MauSac> search(String nameSearch, Pageable pageable);
}
