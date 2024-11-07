package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hoa_don_chi_tiet")
@Entity
public class HoaDonChiTiet {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    HoaDon hoaDon;
    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    SanPhamChiTiet sanPhamChiTiet;
    @Column(name = "ma")
    private String ma;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "don_gia")
    private Double donGia;
    @Column(name = "ngay_tao")
    private Date ngayTao;
    @Column(name = "nguoi_tao")
    private String nguoiTao;
    @Column(name = "ngay_sua")
    private Date ngaySua;
    @Column(name = "nguoi_sua")
    private String nguoiSua;
    @Column(name = "deleted")
    private Integer deleted;
}
