package com.example.demo.controller.admin;

import com.example.demo.service.admin.impl.ThongKeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/thong-ke")
public class ThongKeController {
    @Autowired
    private ThongKeImpl statisticsService;


    @GetMapping("/view")
    public String getStatistics( Model model) {
        // Lấy danh sách khách hàng và sản phẩm bán chạy
        List<Map<String, Object>> topCustomers = statisticsService.getTopCustomersByPurchases();
        List<Map<String, Object>> topProducts = statisticsService.getTopSellingProducts();

        model.addAttribute("topCustomers", topCustomers);
        model.addAttribute("topProducts", topProducts);

        // Thêm thống kê doanh thu vào model
        Map<String, Object> stats = statisticsService.getRevenueStatsForView();
        model.addAttribute("totalQuantity", stats.get("totalQuantity"));
        model.addAttribute("totalRevenue", stats.get("totalRevenue"));
        model.addAttribute("revenueToday", stats.get("revenueToday"));

        // Thống kê doanh thu
        Map<Integer, Double> monthlyRevenue = statisticsService.getMonthlyRevenue();
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        // Thống kê số lượng hóa đơn và sản phẩm theo tháng
        List<Object[]> thongKeTheoThang = statisticsService.thongKeTheoThang();
        model.addAttribute("thongKeTheoThang", thongKeTheoThang); // Thêm vào model để hiển thị

        //Biểu đồ so sánh mức độ tăng trưởng doanh thu
        Double revenueGrowth = statisticsService.calculateRevenueGrowthPercentage();
        model.addAttribute("revenueGrowth", revenueGrowth);

        return "admin/dashboard";
    }
}
