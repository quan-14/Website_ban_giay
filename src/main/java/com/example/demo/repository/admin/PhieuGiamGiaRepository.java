package com.example.demo.repository.admin;

import com.example.demo.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia,Integer> {

    @Query(value = "select * from phieu_giam_gia where hinh_thuc_su_dung = ?1 or hinh_thuc_giam = ?2",nativeQuery = true)
    Page<PhieuGiamGia> findByKieu(int kieu, int loai,Pageable pageable);


    @Override
    @Query(value = "select * from phieu_giam_gia order by id desc",nativeQuery = true)
    Page<PhieuGiamGia> findAll(Pageable pageable);

    @Query(value = "select * from phieu_giam_gia\n" +
            "where ((?1 is null or ?1 like '' or ?2 is null or ?2 like '') \n" +
            "or((ngay_bat_dau >= ?1 and  ngay_bat_dau <= ?2) and (ngay_ket_thuc >= ?1 and ngay_ket_thuc <= ?2 )))\n" +
            "and (?5 IS NULL or ?5 LIKE '' or trang_thai = ?5)\n" +
            "and (?4 IS NULL or ?4 LIKE '' or hinh_thuc_giam = ?4)\n" +
            "and (?3 IS NULL or ?3 LIKE '' or hinh_thuc_su_dung = ?3)\n " +
            "  order by id desc"
            ,nativeQuery = true)
    Page<PhieuGiamGia> searchPGG(
            @Param("tuNgay") LocalDate tuNgay,
            @Param("denNgay") LocalDate denNgay,
            @Param("kieu") Integer kieu,
            @Param("loai") Integer loai,
            @Param("trangThai") String trangThai,
            Pageable pageable);
    @Query("select pgg from PhieuGiamGia pgg where pgg.deleted = 0 and pgg.hinhThucSuDung = 0 and pgg.soLuong > 0 order by pgg.giaTriDonToiThieu")
    List<PhieuGiamGia> lstpgg();

}
