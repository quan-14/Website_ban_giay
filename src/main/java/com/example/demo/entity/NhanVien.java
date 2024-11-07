package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nhan_vien")
@Entity
public class NhanVien {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "phuong_xa")
    private String phuongXa;

    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;

    private String ma;

    @NotBlank(message = "Bạn chưa điền tên")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    @Column(name = "ten")
    private String ten;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @NotBlank(message = "Bạn chưa điền email")
    @Size(max = 100, message = "email không được vượt quá 100 ký tự")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Bạn chưa điền số điện thoại")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có từ 10 đến 11 chữ số")
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "ngay_sinh")
    private java.sql.Date ngaySinh;

    @Column(name = "gioi_tinh")
    private int gioiTinh;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "vai_tro")
    private String vaiTro;

    @Column(name = "ngay_tao")
    private java.sql.Date ngayTao;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_sua")
    private String nguoiSua;

    private int deleted;

}
