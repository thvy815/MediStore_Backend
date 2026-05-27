package com.example.medistore.service.report;

import com.example.medistore.dto.report.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<RevenueReportDTO> getRevenueByDay(
            LocalDate startDate,
            LocalDate endDate);

    List<RevenueReportDTO> getRevenueByMonth();

    List<ProductRevenueDTO> getRevenueByProduct();

    List<BestSellingProductDTO> getBestSellingProducts();

    List<InventoryReportDTO> getInventoryReport();

    List<LowStockDTO> getLowStockProducts(Long threshold);

    List<InventorySalesRatioDTO> getInventorySalesRatio();
}