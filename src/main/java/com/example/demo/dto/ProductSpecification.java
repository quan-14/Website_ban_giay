package com.example.demo.dto;

import com.example.demo.entity.SanPhamChiTiet;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification implements Specification<SanPhamChiTiet> {

    private Integer chatLieuId;

    private Integer coGiayId;

    private Integer deGiayId;

    private Integer thuongHieuId;

    private String trangThai;

    private Integer mauSacId;

    private Integer kichCoId;

    private Integer sanPhamId;

    private String khoangGia;

    private Double minPrice;

    private Double maxPrice;

    public ProductSpecification(Integer chatLieuId, Integer coGiayId, Integer deGiayId,
                                Integer thuongHieuId, String trangThai, Integer mauSacId, Integer kichCoId, Integer sanPhamId, String khoangGia) {
        this.chatLieuId = chatLieuId;
        this.coGiayId = coGiayId;
        this.deGiayId = deGiayId;
        this.thuongHieuId = thuongHieuId;
        this.trangThai = trangThai;
        this.mauSacId = mauSacId;
        this.kichCoId = kichCoId;
        this.sanPhamId = sanPhamId;
        this.khoangGia = khoangGia;
        if(khoangGia != null){
            this.minPrice = Double.valueOf(khoangGia.split("-")[0]);
            this.maxPrice = Double.valueOf(khoangGia.split("-")[1]);
        }
    }

    @Override
    public Predicate toPredicate(Root<SanPhamChiTiet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        if(chatLieuId != null){
            predicate = cb.and(predicate, cb.equal(root.get("chatLieu").get("id"), chatLieuId));
        }
        if(coGiayId != null){
            predicate = cb.and(predicate, cb.equal(root.get("sanPham").get("coGiay").get("id"), coGiayId));
        }
        if(thuongHieuId != null){
            predicate = cb.and(predicate, cb.equal(root.get("sanPham").get("thuongHieu").get("id"), thuongHieuId));
        }
        if(deGiayId != null){
            predicate = cb.and(predicate, cb.equal(root.get("sanPham").get("deGiay").get("id"), deGiayId));
        }
        if(trangThai != null){
            if(trangThai != ""){
                predicate = cb.and(predicate, cb.equal(root.get("trangThai"), trangThai));
            }
        }
        if(mauSacId != null){
            predicate = cb.and(predicate, cb.equal(root.get("mauSac").get("id"), mauSacId));
        }
        if(kichCoId != null){
            predicate = cb.and(predicate, cb.equal(root.get("size").get("id"), kichCoId));
        }
        if (minPrice != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("donGia"), minPrice));
        }

        if (maxPrice != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("donGia"), maxPrice));
        }
        predicate = cb.and(predicate, cb.equal(root.get("sanPham").get("id"), sanPhamId));
        return predicate;
    }
}