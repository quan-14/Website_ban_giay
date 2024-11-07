package com.example.demo.repository.admin;

import com.example.demo.entity.HoaDonChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet,Integer> {

    Page<HoaDonChiTiet> findAllByHoaDon_Id(Integer idhoadon, Pageable pageable);
    @Query("select hdct from HoaDonChiTiet hdct where hdct.hoaDon.id =?1")
    List<HoaDonChiTiet> findallbyhoadon(Integer idhoadon);
    @Query("select hdct.ma from HoaDonChiTiet hdct order by hdct.ma desc")
    List<String> listma();

    //Thống kê
    @Query("SELECT hdt.sanPhamChiTiet.sanPham.ten, SUM(hdt.soLuong) AS totalQuantity " +
            "FROM HoaDonChiTiet hdt " +
            "GROUP BY hdt.sanPhamChiTiet.sanPham.ten " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.deleted = 0")
    Integer findTotalQuantity();

    @Query("SELECT SUM(hdct.soLuong * hdct.donGia) FROM HoaDonChiTiet hdct WHERE hdct.deleted = 0")
    Double findTotalRevenue();

    @Query("SELECT SUM(hdct.soLuong * hdct.donGia) FROM HoaDonChiTiet hdct WHERE hdct.ngayTao BETWEEN :startDate AND :endDate AND hdct.deleted = 0")
    Double findRevenueToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.ngayTao BETWEEN :startOfYear AND :endOfYear")
    List<HoaDonChiTiet> findAllInYear(@Param("startOfYear") Date startOfYear, @Param("endOfYear") Date endOfYear);

    @Query("SELECT FUNCTION('MONTH', hdct.ngayTao) AS thang, " +
            "COUNT(DISTINCT hd.id) AS soHoaDon, " +
            "SUM(hdct.soLuong) AS tongSoLuongSanPham " +
            "FROM HoaDonChiTiet hdct " +
            "LEFT JOIN hdct.hoaDon hd " +
            "WHERE FUNCTION('YEAR', hdct.ngayTao) = FUNCTION('YEAR', CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', hdct.ngayTao) " +
            "ORDER BY FUNCTION('MONTH', hdct.ngayTao)")
    List<Object[]> thongKeTheoThang();


    //so sánh
    @Query("SELECT SUM(hdct.soLuong * hdct.donGia) FROM HoaDonChiTiet hdct WHERE CAST(hdct.ngayTao AS date) = CAST(:currentDate AS date)")
    Double getTotalRevenueByDate(Date currentDate);

    @Query("SELECT SUM(hdct.soLuong * hdct.donGia) FROM HoaDonChiTiet hdct WHERE CAST(hdct.ngayTao AS date) = CAST(:previousDate AS date)")
    Double getTotalRevenueByPreviousDate(Date previousDate);
}
