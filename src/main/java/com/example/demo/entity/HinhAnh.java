package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hinh_anh")
@Entity
public class HinhAnh {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    @JsonBackReference
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "loai")
    private String loai;

    @Column(name = "duong_dan")
    private String duongDan;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "ngay_tao")
    private Date ngayTao;

}
