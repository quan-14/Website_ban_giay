package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lich_su_hoa_don")
@Entity
public class LichSuHoaDon {
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
    @JoinColumn(name = "id_hoa_don")
    HoaDon hoaDon;
    @Column(name = "hanh_dong")
    private String hanhDong;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
