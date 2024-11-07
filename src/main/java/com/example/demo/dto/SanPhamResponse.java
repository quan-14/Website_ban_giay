package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;


public interface SanPhamResponse {

    public Integer getId();

    public String getMa();

    public String getTen();

    public String getTrangThai();

    public Long getSoLuongTon();
}
