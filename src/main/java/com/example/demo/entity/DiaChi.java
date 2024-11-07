package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dia_chi")
@Entity
public class DiaChi {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;
    @Column(name = "quan_huyen")
    private String quanHuyen;
    @Column(name = "phuong_xa")
    private String phuongXa;
    @NotBlank(message = "Bạn chưa điền địa chỉ cụ thể")
    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;
    @Column(name = "mac_dinh")
    private int macDinh;
}
