package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
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
@Table(name = "khach_hang")
@Entity
public class KhachHang {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "ma")
    private String ma;

    @NotBlank(message = "Bạn chưa điền tên")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    @Column(name = "ten")
    private String ten;

    @Column(name = "mat_khau")
    private String matKhau;

    @NotBlank(message = "Bạn chưa điền số điện thoại")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có từ 10 đến 11 chữ số")
    @Column(name = "so_dien_thoai")
    private String sdt;

    @NotBlank(message = "Bạn chưa điền email")
    @Size(max = 100, message = "email không được vượt quá 100 ký tự")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email")
    private String email;
    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "gioi_tinh")
    private int gioiTinh;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "vai_tro")
    private String vaiTro;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    private String nguoiSua;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "deleted")
    private int deleted;

}
