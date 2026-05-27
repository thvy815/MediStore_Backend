package com.example.medistore.repository.report;

import com.example.medistore.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Order, UUID> {

    // =====================================================
    // REVENUE BY DAY
    // =====================================================

    @Query(value = """
                SELECT
                    CAST(o.created_at AS DATE) as period,
                    COALESCE(SUM(p.amount), 0) as revenue
                FROM payments p
                JOIN orders o ON p.order_id = o.id
                WHERE p.status = 'success'
                AND CAST(o.created_at AS DATE)
                    BETWEEN :startDate AND :endDate
                GROUP BY CAST(o.created_at AS DATE)
                ORDER BY CAST(o.created_at AS DATE)
            """, nativeQuery = true)
    List<Object[]> getRevenueByDay(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // =====================================================
    // REVENUE BY MONTH
    // =====================================================

    @Query(value = """
                SELECT
                    TO_CHAR(o.created_at, 'YYYY-MM') as period,
                    COALESCE(SUM(p.amount), 0) as revenue
                FROM payments p
                JOIN orders o ON p.order_id = o.id
                WHERE p.status = 'success'
                GROUP BY TO_CHAR(o.created_at, 'YYYY-MM')
                ORDER BY period
            """, nativeQuery = true)
    List<Object[]> getRevenueByMonth();

    // =====================================================
    // REVENUE BY YEAR
    // =====================================================

    @Query(value = """
                SELECT
                    EXTRACT(YEAR FROM o.created_at) as period,
                    COALESCE(SUM(p.amount), 0) as revenue
                FROM payments p
                JOIN orders o ON p.order_id = o.id
                WHERE p.status = 'success'
                GROUP BY EXTRACT(YEAR FROM o.created_at)
                ORDER BY period
            """, nativeQuery = true)
    List<Object[]> getRevenueByYear();

    // =====================================================
    // REVENUE BY PRODUCT
    // =====================================================

    @Query(value = """
                SELECT
                    pr.name as product_name,
                    COALESCE(SUM(oi.quantity), 0) as total_sold,
                    COALESCE(SUM(oi.quantity * oi.unit_price), 0) as revenue
                FROM order_items oi
                JOIN products pr ON oi.product_id = pr.id
                JOIN orders o ON oi.order_id = o.id
                WHERE o.status IN ('completed', 'delivered')
                GROUP BY pr.name
                ORDER BY revenue DESC
            """, nativeQuery = true)
    List<Object[]> getRevenueByProduct();

    // =====================================================
    // BEST SELLING PRODUCTS
    // =====================================================

    @Query(value = """
                SELECT
                    pr.name as product_name,
                    COALESCE(SUM(oi.quantity), 0) as quantity_sold
                FROM order_items oi
                JOIN products pr ON oi.product_id = pr.id
                GROUP BY pr.name
                ORDER BY quantity_sold DESC
                LIMIT 10
            """, nativeQuery = true)
    List<Object[]> getBestSellingProducts();

    // =====================================================
    // PRODUCT RANKING BY REVENUE
    // =====================================================

    @Query(value = """
                SELECT
                    pr.name as product_name,
                    COALESCE(SUM(oi.quantity * oi.unit_price), 0) as revenue
                FROM order_items oi
                JOIN products pr ON oi.product_id = pr.id
                GROUP BY pr.name
                ORDER BY revenue DESC
            """, nativeQuery = true)
    List<Object[]> getProductRankingByRevenue();

    // =====================================================
    // PRODUCT RANKING BY QUANTITY
    // =====================================================

    @Query(value = """
                SELECT
                    pr.name as product_name,
                    COALESCE(SUM(oi.quantity), 0) as quantity_sold
                FROM order_items oi
                JOIN products pr ON oi.product_id = pr.id
                GROUP BY pr.name
                ORDER BY quantity_sold DESC
            """, nativeQuery = true)
    List<Object[]> getProductRankingByQuantity();

    // =====================================================

    // =====================================================
    // INVENTORY REPORT
    // =====================================================

    @Query(value = """
                SELECT
                    p.name,
                    COALESCE(SUM(pb.quantity_remaining), 0)
                FROM product_batches pb
                JOIN products p ON pb.product_id = p.id
                GROUP BY p.name
                ORDER BY p.name
            """, nativeQuery = true)
    List<Object[]> getInventoryReport();

    // =====================================================
    // LOW STOCK PRODUCTS
    // =====================================================

    @Query(value = """
                SELECT
                    p.name,
                    COALESCE(SUM(pb.quantity_remaining), 0) as remain
                FROM product_batches pb
                JOIN products p ON pb.product_id = p.id
                GROUP BY p.name
                HAVING COALESCE(SUM(pb.quantity_remaining), 0) < :threshold
                ORDER BY remain ASC
            """, nativeQuery = true)
    List<Object[]> getLowStockProducts(
            @Param("threshold") Long threshold);

    // =====================================================
    // INVENTORY SALES RATIO
    // =====================================================

    @Query(value = """
                SELECT
                    p.name,
                    COALESCE(
                        SUM(pb.quantity_remaining)::decimal /
                        NULLIF(SUM(oi.quantity), 0),
                        0
                    ) as ratio
                FROM products p
                LEFT JOIN product_batches pb
                    ON pb.product_id = p.id
                LEFT JOIN order_items oi
                    ON oi.product_id = p.id
                GROUP BY p.name
                ORDER BY ratio DESC
            """, nativeQuery = true)
    List<Object[]> getInventorySalesRatio();

    // =====================================================
    // TOTAL REVENUE TODAY
    // =====================================================

    @Query(value = """
                SELECT COALESCE(SUM(amount), 0)
                FROM payments
                WHERE status = 'success'
                AND CAST(created_at AS DATE) = CURRENT_DATE
            """, nativeQuery = true)
    Double getTodayRevenue();

    // =====================================================
    // TOTAL ORDERS TODAY
    // =====================================================

    @Query(value = """
                SELECT COUNT(*)
                FROM orders
                WHERE CAST(created_at AS DATE) = CURRENT_DATE
            """, nativeQuery = true)
    Long getTodayOrders();

    // =====================================================
    // TOTAL PRODUCTS
    // =====================================================

    @Query(value = """
                SELECT COUNT(*)
                FROM products
                WHERE is_active = true
            """, nativeQuery = true)
    Long getTotalProducts();

    // =====================================================
    // EXPIRED PRODUCTS
    // =====================================================

    @Query(value = """
                SELECT
                    p.name,
                    pb.batch_number,
                    pb.expiry_date
                FROM product_batches pb
                JOIN products p ON pb.product_id = p.id
                WHERE pb.expiry_date < CURRENT_DATE
                ORDER BY pb.expiry_date ASC
            """, nativeQuery = true)
    List<Object[]> getExpiredProducts();

    // =====================================================
    // MONTHLY TREND
    // =====================================================

    @Query(value = """
                SELECT
                    TO_CHAR(o.created_at, 'YYYY-MM') as period,
                    COUNT(o.id) as total_orders,
                    COALESCE(SUM(p.amount), 0) as revenue
                FROM orders o
                LEFT JOIN payments p ON p.order_id = o.id
                WHERE p.status = 'success'
                GROUP BY TO_CHAR(o.created_at, 'YYYY-MM')
                ORDER BY period
            """, nativeQuery = true)
    List<Object[]> getMonthlyTrend();
}