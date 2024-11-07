package com.example.demo.repository.admin;

import com.example.demo.entity.CoGiay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoGiayRepository extends JpaRepository<CoGiay, Integer> {


    @Query("SELECT cg FROM CoGiay cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    Page<CoGiay> findAllActiveCoGiay(Pageable pageable);

    @Query("SELECT cg FROM CoGiay cg WHERE cg.deleted = 0 ORDER BY cg.ngayTao DESC")
    List<CoGiay> getListActiveCoGiays();

    @Query("SELECT sz FROM CoGiay sz WHERE (sz.ma LIKE %:ma% OR sz.ten LIKE %:ma%) AND (sz.trangThai = :trangThai1 OR sz.trangThai = :trangThai2) AND sz.deleted = 0 ORDER BY sz.ngayTao DESC")
    Page<CoGiay> listFilter(String ma, String trangThai1, String trangThai2, Pageable pageable);

    @Query("select sp from CoGiay sp where sp.ma like?1 or sp.ten like ?1" )
    Page<CoGiay> search(String nameSearch, Pageable pageable);

}
