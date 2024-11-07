package com.example.demo.repository.admin;

import com.example.demo.entity.HoaDon;
import com.example.demo.entity.KhachHang;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository

public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {
    @Query(value = "select * from khach_hang where so_dien_thoai like ?1",nativeQuery = true)
    List<KhachHang> findByTenContaining(String sdt);
    @Query("select kh from KhachHang kh where kh.ten like %:search1% or kh.sdt like %:search1% or kh.email like %:search1%")
    Page<KhachHang> findkh (String search1, Pageable pageable);
    @Query("select hd.ma from KhachHang hd order by hd.ma desc")
    List<String> listma();
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO khach_hang (ma, ten, mat_khau, email, so_dien_thoai, ngay_sinh, gioi_tinh, trang_thai, vai_tro, ngay_tao, nguoi_tao, ngay_sua, nguoi_sua, hinh_anh, deleted) " +
            "VALUES (:ma, :ten,:matkhau, :email, :sdt, :ngaysinh, :gioitinh, :trangthai, :vaitro, GETDATE(), :nguoitao, NULL, NULL, :hinhanh, 0)",
            nativeQuery = true)
    void insertKhachHang(String ma, String ten, String matkhau, String email, String sdt, Date ngaysinh, int gioitinh, String trangthai, String vaitro, String nguoitao, String hinhanh);

    @Query("SELECT k FROM KhachHang k WHERE k.ma = :ma")
    KhachHang findByMa(String ma);
    @Modifying
    @Transactional
    @Query(value = "UPDATE khach_hang SET " +
            "ma = :ma, " +
            "ten = :ten, " +
            "email = :email, " +
            "so_dien_thoai = :sdt, " +
            "ngay_sinh = :ngaysinh, " +
            "gioi_tinh = :gioitinh, " +
            "trang_thai = :trangthai, " +
            "vai_tro = :vaitro, " +
            "ngay_sua = GETDATE(), " +
            "hinh_anh = :hinhanh " +
            "WHERE id = :id",
            nativeQuery = true)
    void updatekhachhang(String ma, String ten, String email, String sdt, Date ngaysinh, int gioitinh, String trangthai, String vaitro, String hinhanh, Integer id);

    @Query("select hd from KhachHang hd where " +
            "(hd.ten like %:search1% " +
            "or hd.sdt like %:search1%) " +
            "and (:loai1 is null or hd.gioiTinh = :loai1) " +
            "and hd.ngaySinh between :date1 and :date2")
    Page<KhachHang> searchKhachHang(@Param("search1") String search1,
                                    @Param("loai1")Integer loai1,
                                    @Param("date1")Date date1,
                                    @Param("date2")Date date2, Pageable pageable);

    Optional<KhachHang> findByEmail(String email);

}
