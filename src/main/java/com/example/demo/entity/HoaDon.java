package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hoa_don")
@ToString
@Entity
public class HoaDon {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    NhanVien nhanVien;
    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia")
    PhieuGiamGia phieuGiamGia;
    @Column(name = "ma")
    private String ma;
    @Column(name = "tong_tien")
    private Double tongTien;
    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;
    @Column(name = "quan_huyen")
    private String quanHuyen;
    @Column(name = "phuong_xa")
    private String phuongXa;
    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;
    @Column(name = "phi_ship")
    private Integer phiShip;
    @Column(name = "thoi_gian_nhan_du_kien")
    private Date thoiGianNhanDuKien;
    @Column(name = "loai_hoa_don")
    private Integer loaiHoaDon;
    @Column(name = "ghi_chu")
    private String ghiChu;
    @Column(name = "ngay_tao")
    private Date ngayTao;
    @Column(name = "nguoi_tao")
    private String nguoiTao;
    @Column(name = "ngay_sua")
    private Date ngaySua;
    @Column(name = "nguoi_sua")
    private String nguoiSua;
    @Column(name = "trang_thai")
    private String trangThai;
    @Column(name = "deleted")
    private Integer deleted;
}
