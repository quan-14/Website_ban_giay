package com.example.demo.repository.admin;

import com.example.demo.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    @Query("select hd from HoaDon hd where " +
            "(hd.ma like %:search1% " +
            "or hd.nhanVien.ten like %:search1% " +
            "or hd.khachHang.ten like %:search1% " +
            "or hd.soDienThoai like %:search1%) " +
            "and (:loai1 is null or hd.loaiHoaDon = :loai1) " +
            "and (hd.trangThai like %:trangthaihd%) " +
            "and hd.ngayTao between :date1 and :date2")
    Page<HoaDon> searchHoaDon(String search1,
                                Integer loai1,
                                String trangthaihd,
                                Date date1,
                                Date date2,Pageable pageable);
    @Query(value = "select * from hoa_don where loai_hoa_don = 1 and trang_thai like N'%Tạo mới%' order by ngay_tao\n",nativeQuery = true)
    List<HoaDon> listhoadonbh();

    @Query(value = "select * from hoa_don where trang_thai not like N'%Tạo mới%' order by ngay_tao\n",nativeQuery = true)
    Page<HoaDon> listkhongtaomoi(Pageable page);

    @Query("select hd.ma from HoaDon hd order by hd.ma desc")
    List<String> listma();

    //Thống kê topkh
    @Query("SELECT hd.khachHang.ten, COUNT(hd.id) AS purchaseCount, SUM(hd.tongTien) AS totalAmount " +
            "FROM HoaDon hd " +
            "GROUP BY hd.khachHang.ten " +
            "ORDER BY totalAmount DESC")
    List<Object[]> findTopCustomersByPurchases();

}
