package com.example.demo.service.admin;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    void upload(MultipartFile file);

}
