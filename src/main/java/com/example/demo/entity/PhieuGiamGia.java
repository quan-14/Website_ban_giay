package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "trang_thai")
    private String trangThai;

    @NotBlank(message = "Bạn chưa điền tên phiếu!")
    @Column(name = "ten")
    private String ten;

//    @NotEmpty(message = "Không được để trống và lý do sửa phiếu!")
    @Column(name = "mo_ta")
    private String moTa;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "Bạn chưa nhập ngày bắt đầu!")
    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

//    @Future(message = "Ngày kết thúc phải là ngày tương lai")
    @NotNull(message = "Bạn chưa nhập ngày kết thúc!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "hinh_thuc_giam")
    private Integer hinhThucGiam;

    @Min(value = 0, message = "Giảm tối thiểu phải lớn hơn 0")
    @NotNull(message = "Không được để trống")
    @Column(name = "gia_tri_don_toi_thieu")
    private Double giaTriDonToiThieu;

//    @NotNull(message = "Không được để trống")
    @Column(name = "so_tien_giam")
    private Double soTienGiam;

//    @Min(value = 0, message = "Giảm tối đa phải lớn hơn 0")
//    @NotNull(message = "Không được để trống")
    @Column(name = "giam_toi_da")
    private Double giamToiDa;

//    @NotNull(message = "Không được để trống")
    @Column(name = "phan_tram_giam")
    private Double phanTramGiam;

    @NotNull(message = "Không được để trống")
    @Column(name = "hinh_thuc_su_dung")
    private Integer hinhThucSuDung;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    private String nguoiSua;

    @Column(name = "deleted")
    private int deleted;

    @Min(value = 0,message = "Số lượng phải lớn hơn 0")
    @NotNull(message = "Không được để trống")
    @Column(name = "so_luong")
    private Integer soLuong;
}