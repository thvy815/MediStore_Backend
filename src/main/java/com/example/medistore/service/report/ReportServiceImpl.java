package com.example.medistore.service.report;

import com.example.medistore.dto.report.*;
import com.example.medistore.repository.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl
                implements ReportService {

        private final ReportRepository reportRepository;

        @Override
        public List<RevenueReportDTO> getRevenueByDay(
                        LocalDate startDate,
                        LocalDate endDate) {

                return reportRepository
                                .getRevenueByDay(startDate, endDate)
                                .stream()
                                .map(o -> new RevenueReportDTO(
                                                o[0].toString(),
                                                (BigDecimal) o[1]))
                                .toList();
        }

        @Override
        public List<RevenueReportDTO> getRevenueByMonth(
                        LocalDate startDate,
                        LocalDate endDate) {

                return reportRepository
                                .getRevenueByMonth(
                                                startDate,
                                                endDate)
                                .stream()
                                .map(o -> new RevenueReportDTO(
                                                o[0].toString(),
                                                (BigDecimal) o[1]))
                                .toList();
        }

        @Override
        public List<ProductRevenueDTO> getRevenueByProduct(
                        LocalDate startDate,
                        LocalDate endDate) {

                return reportRepository
                                .getRevenueByProduct(
                                                startDate,
                                                endDate)
                                .stream()
                                .map(o -> new ProductRevenueDTO(
                                                o[0].toString(),
                                                ((Number) o[1]).longValue(),
                                                (BigDecimal) o[2]))
                                .toList();
        }

        @Override
        public List<BestSellingProductDTO> getBestSellingProducts(
                        LocalDate startDate,
                        LocalDate endDate) {

                return reportRepository
                                .getBestSellingProducts(
                                                startDate,
                                                endDate)
                                .stream()
                                .map(o -> new BestSellingProductDTO(
                                                o[0].toString(),
                                                ((Number) o[1]).longValue()))
                                .toList();
        }

        @Override
        public List<InventoryReportDTO> getInventoryReport() {

                return reportRepository
                                .getInventoryReport()
                                .stream()
                                .map(o -> new InventoryReportDTO(
                                                o[0].toString(),
                                                ((Number) o[1]).longValue()))
                                .toList();
        }

        @Override
        public List<LowStockDTO> getLowStockProducts(
                        Long threshold) {

                return reportRepository
                                .getLowStockProducts(threshold)
                                .stream()
                                .map(o -> new LowStockDTO(
                                                o[0].toString(),
                                                ((Number) o[1]).longValue()))
                                .toList();
        }

        @Override
        public List<InventorySalesRatioDTO> getInventorySalesRatio() {

                return reportRepository
                                .getInventorySalesRatio()
                                .stream()
                                .map(o -> new InventorySalesRatioDTO(
                                                o[0].toString(),
                                                o[1] != null
                                                                ? ((Number) o[1]).doubleValue()
                                                                : 0))
                                .toList();
        }
}