package com.example.medistore.service.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.medistore.dto.order.ApplyVoucherResponse;
import com.example.medistore.dto.order.VoucherRequest;
import com.example.medistore.dto.order.VoucherResponse;
import com.example.medistore.entity.order.Voucher;
import com.example.medistore.repository.order.OrderVoucherRepository;
import com.example.medistore.repository.order.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final OrderVoucherRepository orderVoucherRepository;

    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new RuntimeException("Voucher code already exists");
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscount(request.getMaxDiscount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usagePerUser(request.getUsagePerUser())
                .status(request.getStatus())
                .build();

        return mapToResponse(voucherRepository.save(voucher));
    }

    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public VoucherResponse getVoucherById(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        return mapToResponse(voucher);
    }

    public VoucherResponse updateVoucher(UUID id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsagePerUser(request.getUsagePerUser());
        voucher.setStatus(request.getStatus());

        return mapToResponse(voucherRepository.save(voucher));
    }

    public void deleteVoucher(UUID id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Voucher not found");
        }

        voucherRepository.deleteById(id);
    }

    public ApplyVoucherResponse checkVoucher(
            String code,
            BigDecimal productAmount,
            BigDecimal shippingFee,
            UUID userId
    ) {
        if (productAmount == null) {
            productAmount = BigDecimal.ZERO;
        }

        if (shippingFee == null) {
            shippingFee = BigDecimal.ZERO;
        }

        BigDecimal orderAmountBeforeDiscount = productAmount.add(shippingFee);

        Voucher voucher = validateVoucher(code, orderAmountBeforeDiscount, userId);

        BigDecimal discountAmount = calculateDiscount(
                voucher,
                orderAmountBeforeDiscount,
                shippingFee
        );

        BigDecimal finalAmount = orderAmountBeforeDiscount.subtract(discountAmount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return ApplyVoucherResponse.builder()
                .voucherCode(voucher.getCode())
                .discountType(voucher.getDiscountType())
                .orderAmount(orderAmountBeforeDiscount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .message("Voucher applied successfully")
                .build();
    }

    public Voucher validateVoucher(String code, BigDecimal orderAmountBeforeDiscount, UUID userId) {
        Voucher voucher = voucherRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        LocalDate today = LocalDate.now();

        if (!"active".equalsIgnoreCase(voucher.getStatus())) {
            throw new RuntimeException("Voucher is not active");
        }

        if (voucher.getStartDate() != null && today.isBefore(voucher.getStartDate())) {
            throw new RuntimeException("Voucher is not started yet");
        }

        if (voucher.getEndDate() != null && today.isAfter(voucher.getEndDate())) {
            throw new RuntimeException("Voucher has expired");
        }

        if (voucher.getMinOrderValue() != null &&
                orderAmountBeforeDiscount.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new RuntimeException("Order amount does not meet minimum value");
        }

        if (voucher.getUsageLimit() != null) {
            int usedCount = orderVoucherRepository.countVoucherUsed(voucher.getId());

            if (usedCount >= voucher.getUsageLimit()) {
                throw new RuntimeException("Voucher usage limit reached");
            }
        }

        if (voucher.getUsagePerUser() != null) {
            int usedByUser = orderVoucherRepository.countVoucherUsedByUser(voucher.getId(), userId);

            if (usedByUser >= voucher.getUsagePerUser()) {
                throw new RuntimeException("You have already used this voucher");
            }
        }

        return voucher;
    }

    public BigDecimal calculateDiscount(
            Voucher voucher,
            BigDecimal orderAmountBeforeDiscount,
            BigDecimal shippingFee
    ) {
        if (orderAmountBeforeDiscount == null) {
            orderAmountBeforeDiscount = BigDecimal.ZERO;
        }

        if (shippingFee == null) {
            shippingFee = BigDecimal.ZERO;
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if ("fixed".equalsIgnoreCase(voucher.getDiscountType())) {
            discountAmount = voucher.getDiscountValue();
        }

        if ("percent".equalsIgnoreCase(voucher.getDiscountType())) {
            discountAmount = orderAmountBeforeDiscount
                    .multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (voucher.getMaxDiscount() != null &&
                    discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
                discountAmount = voucher.getMaxDiscount();
            }
        }

        if ("freeship".equalsIgnoreCase(voucher.getDiscountType())) {
            discountAmount = shippingFee;
        }

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        if (discountAmount.compareTo(orderAmountBeforeDiscount) > 0) {
            discountAmount = orderAmountBeforeDiscount;
        }

        return discountAmount;
    }

    private VoucherResponse mapToResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .usageLimit(voucher.getUsageLimit())
                .usagePerUser(voucher.getUsagePerUser())
                .status(voucher.getStatus())
                .createdAt(voucher.getCreatedAt())
                .build();
    }
}