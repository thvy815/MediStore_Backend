package com.example.medistore.controller.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.medistore.dto.order.ApplyVoucherResponse;
import com.example.medistore.dto.order.VoucherRequest;
import com.example.medistore.dto.order.VoucherResponse;
import com.example.medistore.service.order.VoucherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public VoucherResponse createVoucher(@RequestBody VoucherRequest request) {
        return voucherService.createVoucher(request);
    }

    @GetMapping
    public List<VoucherResponse> getAllVouchers() {
        return voucherService.getAllVouchers();
    }

    @GetMapping("/{id}")
    public VoucherResponse getVoucherById(@PathVariable UUID id) {
        return voucherService.getVoucherById(id);
    }

    @PutMapping("/{id}")
    public VoucherResponse updateVoucher(
            @PathVariable UUID id,
            @RequestBody VoucherRequest request
    ) {
        return voucherService.updateVoucher(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteVoucher(@PathVariable UUID id) {
        voucherService.deleteVoucher(id);
        return "Delete voucher successfully";
    }

    @GetMapping("/check")
    public ApplyVoucherResponse checkVoucher(
            @RequestParam String code,
            @RequestParam BigDecimal productAmount,
            @RequestParam BigDecimal shippingFee,
            @RequestParam UUID userId
    ) {
        return voucherService.checkVoucher(code, productAmount, shippingFee, userId);
    }
}