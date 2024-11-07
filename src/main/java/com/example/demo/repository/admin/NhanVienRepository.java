package com.example.demo.repository.admin;

import com.example.demo.entity.NhanVien;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO nhan_vien (ma, ten, mat_khau, email, so_dien_thoai, ngay_sinh, tinh_thanh_pho, quan_huyen, phuong_xa, dia_chi_cu_the, gioi_tinh, trang_thai, vai_tro, ngay_tao, nguoi_tao,hinh_anh, ngay_sua, nguoi_sua, deleted) " +
            "VALUES (:ma, :ten, :matKhau, :email, :soDienThoai, :ngaySinh, :tinhThanhPho, :quanHuyen, :phuongXa, :diaChiCuThe, :gioiTinh, :trangThai, :vaiTro, GETDATE(), :nguoiTao,:hinhAnh, NULL, NULL, 0)", nativeQuery = true)
    void insertNhanVien(@Param("ma") String ma,
                        @Param("ten") String ten,
                        @Param("matKhau") String matKhau,
                        @Param("email") String email,
                        @Param("soDienThoai") String soDienThoai,
                        @Param("ngaySinh") Date ngaySinh,
                        @Param("tinhThanhPho") String tinhThanhPho,
                        @Param("quanHuyen") String quanHuyen,
                        @Param("phuongXa") String phuongXa,
                        @Param("diaChiCuThe") String diaChiCuThe,
                        @Param("gioiTinh") int gioiTinh,
                        @Param("trangThai") String trangThai,
                        @Param("vaiTro") String vaiTro,
                        @Param("nguoiTao") String nguoiTao, String hinhAnh);


    NhanVien save(NhanVien nhanVien);

    @Query(value = "SELECT * FROM nhan_vien WHERE deleted = 0 ORDER BY ngay_tao DESC", nativeQuery = true)
    Page<NhanVien> getAllNVActive(Pageable pageable);

    Optional<NhanVien> findNhanVienByEmail(String email);

    @Query(value = "select * from nhan_vien where so_dien_thoai like ?1", nativeQuery = true)
    List<NhanVien> findByTenContaining(String sdt);

    @Query("select kh from KhachHang kh where kh.ten like %:search1% or kh.sdt like %:search1% or kh.email like %:search1%")
    Page<NhanVien> findnv(String search1, Pageable pageable);

    @Query("select nv from NhanVien nv where " +
            "(nv.ten like %:search1% " +
            "or nv.soDienThoai like %:search1%) " +
            "and (:loai1 is null or nv.gioiTinh = :loai1) " +
            "and nv.ngaySinh between :date1 and :date2 " +
            "order by nv.ngayTao desc") // Sắp xếp theo ngày tạo giảm dần
    Page<NhanVien> searchNhanVien(@Param("search1") String search1,
                                  @Param("loai1") Integer loai1,
                                  @Param("date1") java.util.Date date1,
                                  @Param("date2") java.util.Date date2, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NhanVien n WHERE n.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
