package com.example.demo.configuration;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.admin.KhachHangRepository;
import com.example.demo.repository.admin.NhanVienRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();

        Optional<NhanVien> nhanVien = nhanVienRepository.findNhanVienByEmail(email);
        Optional<KhachHang> khachHang = khachHangRepository.findByEmail(email);
        if (nhanVien.isPresent()) {
            NhanVien nv = nhanVien.get();
            if (nv.getVaiTro().equals("ROLE_ADMIN")) {
                request.getSession().setAttribute("username", nv.getTen());
                request.getSession().setAttribute("role", "ADMIN");
                response.sendRedirect("/admin/dashboard");
            } else if (nv.getVaiTro().equals("ROLE_EMPLOYEE")) {
                request.getSession().setAttribute("username", nv.getTen());
                request.getSession().setAttribute("role", "EMPLOYEE");
                response.sendRedirect("/admin/dashboard");
            }
        } else if (khachHang.isPresent()) {
            request.getSession().setAttribute("username", khachHang.get().getTen());
            request.getSession().setAttribute("role", "USER");
            response.sendRedirect("user/sanpham/home");
        }
    }
}
