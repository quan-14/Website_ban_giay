package com.example.demo.service.admin.impl;

import com.example.demo.entity.HinhAnh;
import com.example.demo.repository.admin.HinhAnhRepository;
import com.example.demo.service.admin.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HinhAnhImpl implements HinhAnhService {

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Override
    public String create(HinhAnh hinhAnh) {
        hinhAnhRepository.save(hinhAnh);
        return null;
    }
}
