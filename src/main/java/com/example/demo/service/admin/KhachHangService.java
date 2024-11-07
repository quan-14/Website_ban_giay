package com.example.demo.service.admin;

import com.example.demo.entity.KhachHang;

import java.util.List;

public interface KhachHangService {
    List<KhachHang> searchByTen(String sdt);

}
