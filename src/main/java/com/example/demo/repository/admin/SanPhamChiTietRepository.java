package com.example.demo.repository.admin;

import com.example.demo.dto.ProductSpecification;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer>, JpaSpecificationExecutor<SanPhamChiTiet> {

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham " +
            "AND spct.deleted = 0 ORDER BY spct.ngayTao DESC")
    Page<SanPhamChiTiet> getPageSPCT(Integer idSanPham, Pageable pageable);

    @Query("select spct from SanPhamChiTiet spct where spct.sanPham.id = ?1")
    List<SanPhamChiTiet> getListSPCT(Integer idSanPham);

    @Query("select s from SanPhamChiTiet s where s.sanPham.id = ?1")
    List<SanPhamChiTiet> findBySanPham(Integer idSp);
    @Query("select s from SanPhamChiTiet s where s.sanPham.id = ?1 and s.mauSac.id = ?2")
    List<SanPhamChiTiet> findspctSizeBySanPhamandmausac(Integer idSp,Integer idMs);
    @Query(value = "select * from san_pham_chi_tiet\n" +
            "where don_gia = 0 and so_luong = 0 and id_san_pham = ?1",nativeQuery = true)
    List<SanPhamChiTiet> renSPCT(Integer idSp);

    @Query("select sum(sp.soLuong) from SanPhamChiTiet sp where sp.sanPham.id = ?1")
    Long totalByProduct(Integer sanPhamId);
    @Query(value = "SELECT san_pham_chi_tiet.id, san_pham_chi_tiet.id_san_pham, san_pham_chi_tiet.id_xuat_xu, san_pham_chi_tiet.id_nha_san_xuat, san_pham_chi_tiet.id_chat_lieu, san_pham_chi_tiet.id_size, san_pham_chi_tiet.id_mau_sac, \n" +
            "                  san_pham_chi_tiet.ma, san_pham_chi_tiet.trang_thai, san_pham_chi_tiet.don_gia, san_pham_chi_tiet.mo_ta, san_pham_chi_tiet.so_luong, san_pham_chi_tiet.khoi_luong, san_pham_chi_tiet.don_vi_tinh, san_pham_chi_tiet.ghi_chu, \n" +
            "                  san_pham_chi_tiet.ngay_tao, san_pham_chi_tiet.nguoi_tao, san_pham_chi_tiet.ngay_sua, san_pham_chi_tiet.nguoi_sua, san_pham_chi_tiet.deleted, san_pham_chi_tiet.qr_image\n" +
            "FROM     san_pham INNER JOIN\n" +
            "                  san_pham_chi_tiet ON san_pham.id = san_pham_chi_tiet.id_san_pham where san_pham.trang_thai like N'%Đang hoạt động%' and san_pham_chi_tiet.trang_thai like N'%Đang hoạt động%' and san_pham_chi_tiet.so_luong > 0 ",nativeQuery = true)
    Page<SanPhamChiTiet> pagespct (Pageable pageable);

}
