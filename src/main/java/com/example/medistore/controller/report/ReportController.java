package com.example.medistore.controller.report;

import com.example.medistore.dto.report.*;
import com.example.medistore.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // =========================
    // REVENUE
    // =========================

    @GetMapping("/revenue/day")
    public List<RevenueReportDTO> getRevenueByDay(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return reportService.getRevenueByDay(
                startDate,
                endDate);
    }

    @GetMapping("/revenue/month")
    public List<RevenueReportDTO> getRevenueByMonth(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return reportService.getRevenueByMonth(
                startDate,
                endDate);
    }

    @GetMapping("/revenue/product")
    public List<ProductRevenueDTO> getRevenueByProduct(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return reportService.getRevenueByProduct(
                startDate,
                endDate);
    }

    // =========================
    // BEST SELLING
    // =========================

    @GetMapping("/best-selling")
    public List<BestSellingProductDTO> getBestSellingProducts(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return reportService.getBestSellingProducts(
                startDate,
                endDate);
    }

    // =========================
    // INVENTORY
    // =========================

    @GetMapping("/inventory")
    public List<InventoryReportDTO> getInventoryReport() {
        return reportService.getInventoryReport();
    }

    @GetMapping("/inventory/low-stock")
    public List<LowStockDTO> getLowStock(
            @RequestParam(defaultValue = "20") Long threshold) {

        return reportService.getLowStockProducts(
                threshold);
    }

    @GetMapping("/inventory/sales-ratio")
    public List<InventorySalesRatioDTO> getInventorySalesRatio() {
        return reportService.getInventorySalesRatio();
    }
}