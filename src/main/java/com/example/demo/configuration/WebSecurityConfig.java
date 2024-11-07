package com.example.demo.configuration;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.admin.KhachHangRepository;
import com.example.demo.repository.admin.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions().disable() // Vô hiệu hóa X-Frame-Options
                )
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/home", "/forgot-password", "/register", "/user").permitAll()
//
//                        .requestMatchers("/admin/nhan-vien/**").hasAuthority("ROLE_EMPLOYEE")
//                        .requestMatchers("/admin/san-pham/view", "/admin/phieu-giam-gia/hien-thi","/admin/khachhang/**","/admin/ban-hang/**","admin/thong-ke/view").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                                .requestMatchers("/admin/dashboard", "/admin/san-pham/view",
                                        "/admin/phieu-giam-gia/hien-thi", "/admin/khachhang/**",
                                        "/admin/ban-hang/**", "/admin/thong-ke/view").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                                .anyRequest().permitAll()
                )
                .exceptionHandling(exception ->
                        exception
                                .accessDeniedPage("/error") // Đường dẫn đến trang lỗi tùy chỉnh
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL xử lý logout
                        .logoutSuccessUrl("/user/sanpham/home")  // Chuyển hướng sau khi logout thành công
                        .invalidateHttpSession(true)  // Xóa session
                        .deleteCookies("JSESSIONID")  // Xóa cookie nếu cần
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .build();

    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<NhanVien> nhanVien = nhanVienRepository.findNhanVienByEmail(username);
            Optional<KhachHang> khachHang = khachHangRepository.findByEmail(username);
            if (nhanVien.isEmpty() && khachHang.isEmpty()) {
                throw new UsernameNotFoundException("User " + username + " was not found in the database");
            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            System.out.println("role: " + authorities.toString());
            if (nhanVien.isPresent()) {
                NhanVien nv = nhanVien.get();
                if (nv.getVaiTro().equals("ROLE_ADMIN")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else if (nv.getVaiTro().equals("ROLE_EMPLOYEE")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
                }
                System.out.println("role: " + authorities.toString());
                return new User(nv.getEmail(), nv.getMatKhau(), authorities);
//                System.out.println("ROLE_ADMIN");
            } else if (khachHang.isPresent()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                return new User(khachHang.get().getEmail(), khachHang.get().getMatKhau(), authorities);
            }
            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
