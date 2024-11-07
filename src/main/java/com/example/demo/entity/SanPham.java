package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "san_pham")
@Entity
@ToString
public class SanPham {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "ma")
    private String ma;

    @Size(min = 6, max = 50, message="Trường 'Tên' phải có độ dài từ 6 đến 50 kí tự")
//    @NotBlank(message = "Trường 'Tên' không được để trống!")
    @Column(name = "ten")
    private String ten;

    @Column(name = "trang_thai")
    private String trangThai;

    @NotNull(message = "Trường 'Cổ giày' không được để trống!")
    @ManyToOne
    @JoinColumn(name = "id_co_giay")
    private CoGiay coGiay;

    @NotNull(message = "Trường 'Đế giày' không được để trống!")
    @ManyToOne
    @JoinColumn(name = "id_de_giay")
    private DeGiay deGiay;

    @NotNull(message = "Trường 'Danh mục' không được để trống!")
    @ManyToOne
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @NotNull(message = "Trường 'Thương hiệu' không được để trống!")
    @ManyToOne
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @Column(name = "ghi_chu")
    private String ghiChu;

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

    @Transient
    private Long soLuongTon;
}
