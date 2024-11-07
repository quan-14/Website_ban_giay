package com.example.demo.service.admin.impl;

import com.example.demo.entity.KhachHang;
import com.example.demo.repository.admin.KhachHangRepository;
import com.example.demo.service.admin.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    KhachHangRepository khachHangRepository;

    @Override
    public List<KhachHang> searchByTen(String sdt) {
        return khachHangRepository.findByTenContaining(sdt);
    }
}

