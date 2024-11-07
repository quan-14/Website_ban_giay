package com.example.demo.service.admin.impl;

import com.example.demo.controller.admin.ApiResponse;
import com.example.demo.entity.Disctrict;
import com.example.demo.entity.Province;
import com.example.demo.entity.Ward;
import com.example.demo.infrastructure.mail.GhnConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GhnService {

    @Autowired
    private GhnConfig ghnConfig;

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnConfig.getApiToken());
        return headers;
    }

    public List<Province> getProvinces() {
        ResponseEntity<ApiResponse<List<Province>>> response = restTemplate.exchange(
                ghnConfig.getProvinceUrl(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                new ParameterizedTypeReference<ApiResponse<List<Province>>>() {}
        );

        return response.getBody().getData();
    }

    public List<Disctrict> getDistricts(int provinceID) {
        String url = String.format("%s?province_id=%d", ghnConfig.getDistrictUrl(), provinceID);
        ResponseEntity<ApiResponse<List<Disctrict>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                new ParameterizedTypeReference<ApiResponse<List<Disctrict>>>() {}
        );

        return response.getBody().getData();
    }

    public List<Ward> getWards(int districtID) {
        String url = String.format("%s?district_id=%d", ghnConfig.getWardUrl(), districtID);
        ResponseEntity<ApiResponse<List<Ward>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                new ParameterizedTypeReference<ApiResponse<List<Ward>>>() {}
        );

        return response.getBody().getData();
    }

    public int getShippingFee(int districtID,String wardCode,int khoiluong) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("service_type_id", 2);
        payload.put("from_district_id", 3440);
        payload.put("to_district_id", districtID);
        payload.put("to_ward_code", wardCode);
        payload.put("weight", khoiluong);
        payload.put("name", "Tổng của hóa đơn ");
        payload.put("quantity", 1);

        // Tạo đối tượng HttpEntity với payload và tiêu đề
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("ShopId", String.valueOf(ghnConfig.getShopID()));
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Gửi yêu cầu POST và nhận phản hồi
        ResponseEntity<String> response = restTemplate.exchange(
                ghnConfig.getShipUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Xử lý phản hồi
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();

            // Phân tích JSON và lấy giá trị service_fee
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                JsonNode data = jsonResponse.get("data");
                int Total = data.get("total").asInt();

                return Total;
            }  catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
        return 0;
    }


    public Province getProvinceByName(String provinceName) {
        List<Province> provinces = getProvinces();
        return provinces.stream()
                .filter(province -> province.getProvinceName().equalsIgnoreCase(provinceName))
                .findFirst()
                .orElse(null);
    }
    public Province getProvinceByID(int provinceID) {
        List<Province> provinces = getProvinces();
        return provinces.stream()
                .filter(province -> province.getProvinceID()==provinceID)
                .findFirst()
                .orElse(null);
    }

    // Tìm quận/huyện theo tên và ID tỉnh
    public Disctrict getDistrictByName(String districtName, int provinceID) {
        List<Disctrict> districts = getDistricts(provinceID);
        return districts.stream()
                .filter(district -> district.getDistrictName().equalsIgnoreCase(districtName))
                .findFirst()
                .orElse(null);
    }

    public Disctrict getDistrictByID(int districtID, int provinceID) {
        List<Disctrict> districts = getDistricts(provinceID);
        return districts.stream()
                .filter(district -> district.getDistrictID()==districtID)
                .findFirst()
                .orElse(null);
    }

    // Tìm phường/xã theo tên và ID quận/huyện
    public Ward getWardByName(String wardName, int districtID) {
        List<Ward> wards = getWards(districtID);
        return wards.stream()
                .filter(ward -> ward.getWardName().equalsIgnoreCase(wardName))
                .findFirst()
                .orElse(null);
    }
    public Ward getWardByID(String wardCode, int districtID) {
        List<Ward> wards = getWards(districtID);
        return wards.stream()
                .filter(ward -> ward.getWardCode().equalsIgnoreCase(wardCode))
                .findFirst()
                .orElse(null);
    }


}
