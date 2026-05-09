package com.computershop.dto;

/**
 * DTO dành cho báo cáo doanh thu và đơn hàng.
 */
public class ReportDTO {

    private double totalOrderValue; 
    private double totalPaid;       
    private double totalPending;    
    private double totalCancelled;  

    // ==================== Constructors ====================

    public ReportDTO() {
    }

    public ReportDTO(double totalOrderValue, double totalPaid, double totalPending, double totalCancelled) {
        this.totalOrderValue = totalOrderValue;
        this.totalPaid = totalPaid;
        this.totalPending = totalPending;
        this.totalCancelled = totalCancelled;
    }

    // ==================== Getters and Setters ====================

    public double getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(double totalPending) {
        this.totalPending = totalPending;
    }

    public double getTotalCancelled() {
        return totalCancelled;
    }

    public void setTotalCancelled(double totalCancelled) {
        this.totalCancelled = totalCancelled;
    }
}