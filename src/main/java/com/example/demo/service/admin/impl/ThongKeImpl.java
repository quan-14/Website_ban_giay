package com.example.demo.service.admin.impl;


import com.example.demo.entity.HoaDonChiTiet;
import com.example.demo.repository.admin.HoaDonChiTietRepository;
import com.example.demo.repository.admin.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeImpl {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    public List<Map<String, Object>> getTopCustomersByPurchases() {
        // Lấy danh sách top khách hàng đã mua hàng
        List<Object[]> allCustomers = hoaDonRepository.findTopCustomersByPurchases();

        Date currentDate = new Date();

        // Tạo một lịch để lấy tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        // Lọc và tính tổng số lượng cho tháng hiện tại
        return allCustomers.stream()
                .filter(customer -> {
                    // Lấy tên khách hàng
                    String customerName = (String) customer[0];

                    // Kiểm tra xem hóa đơn nào có ngày tạo trong tháng hiện tại
                    return hoaDonRepository.findAll().stream()
                            .filter(hd -> hd.getKhachHang().getTen().equals(customerName))
                            .anyMatch(hd -> {
                                // Lấy ngày tạo hóa đơn
                                Calendar saleCalendar = Calendar.getInstance();
                                saleCalendar.setTime(hd.getNgayTao());
                                return saleCalendar.get(Calendar.YEAR) == currentYear &&
                                        saleCalendar.get(Calendar.MONTH) + 1 == currentMonth;
                            });
                })
                .limit(5) // Giới hạn kết quả chỉ lấy 5 khách hàng
                .map(customer -> Map.of(
                        "name", customer[0],
                        "purchaseCount", customer[1],
                        "totalAmount", customer[2]
                ))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopSellingProducts() {
        // Lấy danh sách top sản phẩm bán chạy
        List<Object[]> allProducts = hoaDonChiTietRepository.findTopSellingProducts();

        // Lấy ngày hiện tại
        Date currentDate = new Date();

        // Tạo một lịch để lấy tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0

        // Lọc và tính tổng số lượng cho tháng hiện tại
        return allProducts.stream()
                .filter(product -> {
                    // Lấy tên sản phẩm
                    String productName = (String) product[0];

                    // Lấy danh sách hóa đơn chi tiết cho sản phẩm này
                    // Kiểm tra xem hóa đơn nào có ngày tạo trong tháng hiện tại
                    return hoaDonChiTietRepository.findAll().stream()
                            .filter(hdt -> hdt.getSanPhamChiTiet().getSanPham().getTen().equals(productName))
                            .anyMatch(hdt -> {
                                Calendar saleCalendar = Calendar.getInstance();
                                saleCalendar.setTime(hdt.getNgayTao());
                                return saleCalendar.get(Calendar.YEAR) == currentYear &&
                                        saleCalendar.get(Calendar.MONTH) + 1 == currentMonth;
                            });
                })
                .limit(5) // Giới hạn kết quả chỉ lấy 5 sản phẩm
                .map(product -> Map.of(
                        "name", product[0],
                        "totalQuantity", product[1]
                ))
                .collect(Collectors.toList());
    }

    private String formatIntegerNumber(Integer value) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(new Locale("vi", "VN"));
        return value != null ? numberFormat.format(value) : "0";
    }

    public Integer getTotalQuantity() {
        return hoaDonChiTietRepository.findTotalQuantity();
    }

    public String getTotalRevenue() {
        Double totalRevenue = hoaDonChiTietRepository.findTotalRevenue();
        Integer revenueAsInteger = totalRevenue != null ? (int) Math.round(totalRevenue) : 0;
        return formatIntegerNumber(revenueAsInteger);
    }

    public String getRevenueToday(Date startDate, Date endDate) {
        Double revenueToday = hoaDonChiTietRepository.findRevenueToday(startDate, endDate);
        Integer revenueAsInteger = revenueToday != null ? (int) Math.round(revenueToday) : 0;
        return formatIntegerNumber(revenueAsInteger);
    }

    public Map<String, Object> getRevenueStatsForView() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuantity", formatIntegerNumber(getTotalQuantity()));
        stats.put("totalRevenue", getTotalRevenue());
        stats.put("revenueToday", getRevenueToday(new Date(), new Date()));
        return stats;
    }

    public Map<Integer, Double> getMonthlyRevenue() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Lấy ngày đầu tiên và ngày cuối cùng của năm hiện tại
        calendar.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);
        Date startOfYear = calendar.getTime();

        calendar.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endOfYear = calendar.getTime();

        // Lấy tất cả các bản ghi trong năm hiện tại
        List<HoaDonChiTiet> allInCurrentYear = hoaDonChiTietRepository.findAllInYear(startOfYear, endOfYear);

        // Tính tổng doanh thu theo tháng
        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put(i, 0.0);
        }
        for (HoaDonChiTiet hdct : allInCurrentYear) {
            calendar.setTime(hdct.getNgayTao());
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH trả về 0-11
            double revenue = hdct.getSoLuong() * hdct.getDonGia();
            monthlyRevenue.put(month, monthlyRevenue.get(month) + revenue);
        }

        return monthlyRevenue;
    }

    public List<Object[]> thongKeTheoThang() {
        List<Object[]> results = hoaDonChiTietRepository.thongKeTheoThang();
        List<Object[]> thongKeList = new ArrayList<>();

        // Khởi tạo tất cả 12 tháng với giá trị mặc định
        for (int i = 1; i <= 12; i++) {
            thongKeList.add(new Object[]{i, 0, 0L}); // Tháng, Số hóa đơn, Tổng số lượng sản phẩm
        }

        // Gắn kết quả truy vấn vào danh sách
        for (Object[] row : results) {
            Integer thang = (Integer) row[0];
            Long soHoaDon = (Long) row[1];
            Long tongSoLuongSanPham = (Long) row[2];
            thongKeList.set(thang - 1, new Object[]{thang, soHoaDon.intValue(), tongSoLuongSanPham});
        }

        // Trả về danh sách đã sắp xếp theo tháng
        return thongKeList;
    }


    public Double calculateRevenueGrowthPercentage() {
        // Lấy ngày hiện tại
        Date currentDate = new Date();

        // Lấy ngày hôm trước
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -1);
        Date previousDate = calendar.getTime();

        // Lấy doanh thu của cả hai ngày
        Double todayRevenue = hoaDonChiTietRepository.getTotalRevenueByDate(currentDate);
        Double yesterdayRevenue = hoaDonChiTietRepository.getTotalRevenueByPreviousDate(previousDate);

        // Nếu giá trị null, đặt là 0 để tránh lỗi NullPointerException
        todayRevenue = todayRevenue != null ? todayRevenue : 0.0;
        yesterdayRevenue = yesterdayRevenue != null ? yesterdayRevenue : 0.0;

        // Tính toán phần trăm tăng trưởng
        if (yesterdayRevenue == 0) {
            return todayRevenue > 0 ? 100.0 : 0.0; // Nếu không có doanh thu hôm qua, tăng trưởng là 100% nếu hôm nay có doanh thu
        } else {
            return ((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100;
        }
    }

}
