package com.example.demo.infrastructure.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GhnConfig {
    @Value("aef361b5-f26a-11ed-bc91-ba0234fcde32")
    private String apiToken;
    @Value("124173")
    private int ShopID;
    @Value("https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee")
    private String shipUrl;

    @Value("https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province")
    private String provinceUrl;

    @Value("https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district")
    private String districtUrl;

    @Value("https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward")
    private String wardUrl;


    public String getApiToken() {
        return apiToken;
    }


    public String getProvinceUrl() {
        return provinceUrl;
    }

    public String getDistrictUrl() {
        return districtUrl;
    }

    public String getWardUrl() {
        return wardUrl;
    }
    public String getShipUrl() {
        return shipUrl;
    }
    public int getShopID() {
        return ShopID;
    }


}

