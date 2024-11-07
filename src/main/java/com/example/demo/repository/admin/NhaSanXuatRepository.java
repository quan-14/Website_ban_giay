package com.example.demo.repository.admin;

import com.example.demo.entity.CoGiay;
import com.example.demo.entity.NhaSanXuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhaSanXuatRepository extends JpaRepository<NhaSanXuat, Integer> {

    @Query("SELECT cg FROM NhaSanXuat cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    Page<NhaSanXuat> findAllActiveNhaSanXuats(Pageable pageable);

    @Query("SELECT cg FROM NhaSanXuat cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<NhaSanXuat> getListActiveNhaSanXuats();

    @Query("SELECT ms FROM NhaSanXuat ms WHERE (ms.ma LIKE %:ma% OR ms.ten LIKE %:ma%) AND (ms.trangThai = :trangThai1 OR ms.trangThai = :trangThai2) AND ms.deleted = 0 ORDER BY ms.ngayTao DESC")
    Page<NhaSanXuat> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("select sp from NhaSanXuat sp where sp.ma like?1 or sp.ten like ?1" )
    Page<NhaSanXuat> search(String nameSearch, Pageable pageable);

}
