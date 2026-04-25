package com.computershop.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Service tạo QR thanh toán theo 2 chuẩn:
 *
 * 1. VietQR (EMVCo) — quét được bằng TẤT CẢ app ngân hàng Việt Nam
 *    (Vietcombank, BIDV, Techcombank, MBBank, TPBank, ACB, ...) và MoMo, ZaloPay
 *    → Dùng cho QR code hiển thị trên trang web
 *
 * 2. MoMo deep link — dự phòng trên mobile, mở thẳng app MoMo
 *
 * Cấu hình trong application.properties:
 *   payment.vietqr.account-number  = số tài khoản
 *   payment.vietqr.account-name    = tên chủ tài khoản
 *   payment.vietqr.bank-bin        = mã BIN ngân hàng (tra tại https://api.vietqr.io/v2/banks)
 *   payment.vietqr.bank-name       = tên ngân hàng hiển thị
 */
@Service
public class MomoService {

    // ── VietQR config ────────────────────────────────────────────────────────
    @Value("${payment.vietqr.account-number}")
    private String accountNumber;

    @Value("${payment.vietqr.account-name}")
    private String accountName;

    @Value("${payment.vietqr.bank-bin}")
    private String bankBin;

    @Value("${payment.vietqr.bank-name}")
    private String bankName;

    // ── MoMo config (deep link dự phòng) ────────────────────────────────────
    @Value("${payment.momo.receiver-phone}")
    private String momoPhone;

    // ── VietQR EMVCo constants ────────────────────────────────────────────────
    private static final String NAPAS_GUID       = "A000000727";
    private static final String COUNTRY_CODE     = "VN";
    private static final String CURRENCY_CODE    = "704";      // VND

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sinh QR base64 theo chuẩn VietQR (EMVCo).
     * Quét được bằng TẤT CẢ app ngân hàng nội địa + MoMo + ZaloPay.
     *
     * @param amount    số tiền VND
     * @param addInfo   nội dung chuyển khoản (vd: "Thanh toan don hang #5")
     * @return chuỗi base64 PNG dùng trong <img src="data:image/png;base64,...">
     */
    public String generateQrBase64(long amount, String addInfo)
            throws WriterException, IOException {
        String payload = buildVietQRPayload(amount, addInfo);
        return encodeToBase64Png(payload, 300, 300);
    }

    /**
     * Trả về chuỗi VietQR payload (text) — dùng để debug hoặc API.
     *
     * Chuẩn NAPAS VietQR (EMVCo) — quét được bằng TẤT CẢ app ngân hàng VN.
     *
     * Cấu trúc Merchant Account Information (ID 38) — BẮT BUỘC đủ 4 sub-field:
     *   00 = NAPAS GUID      "A000000727"
     *   01 = bank BIN        vd "970422" (MB), "970436" (VCB)
     *   02 = service code    "QRIBFTTC"   ← fast inter-bank transfer
     *   03 = account number  vd "0983580550"
     *
     * Additional Data (ID 62):
     *   08 = Purpose of Transaction (nội dung CK — tất cả app ngân hàng + MoMo đọc trường này)
     */
    public String buildVietQRPayload(long amount, String addInfo) {
        String cleanNote = truncate(removeAccents(addInfo), 25);

        // ── Merchant Account Information (ID 38) — ĐÚNG CHUẨN NAPAS ─────────
        // sub-field 02 = service code "QRIBFTTC" là BẮT BUỘC
        // sub-field 03 = số tài khoản (không phải 02!)
        String inner = tlv("00", NAPAS_GUID)    // GUID
                     + tlv("01", bankBin)        // BIN ngân hàng
                     + tlv("02", "QRIBFTTC")     // service code (bắt buộc theo NAPAS)
                     + tlv("03", accountNumber); // số tài khoản
        String merchantAccount = tlv("38", inner);

        // ── Các trường EMVCo bắt buộc ────────────────────────────────────────
        String payloadFormatIndicator = tlv("00", "01");
        String pointOfInitiation      = tlv("01", "12"); // 12 = dynamic (có số tiền)
        String txCurrency             = tlv("53", CURRENCY_CODE);
        String txAmount               = tlv("54", String.valueOf(amount));
        String countryCode            = tlv("58", COUNTRY_CODE);
        String merchantName           = tlv("59", truncate(accountName, 25));
        String merchantCity           = tlv("60", "Ho Chi Minh");

        // ── Additional Data (ID 62) — sub-field 08 = nội dung CK ─────────────
        String additionalData = tlv("62", tlv("08", cleanNote));

        // ── Ghép payload chưa có CRC ──────────────────────────────────────────
        String payloadNoCRC = payloadFormatIndicator
                + pointOfInitiation
                + merchantAccount
                + txCurrency
                + txAmount
                + countryCode
                + merchantName
                + merchantCity
                + additionalData
                + "6304"; // ID=63, length=04, value sẽ tính bên dưới

        // ── CRC-16/CCITT-FALSE ────────────────────────────────────────────────
        String crc = crc16(payloadNoCRC);
        return payloadNoCRC + crc;
    }

    /**
     * VietQR Quick Link — trả về URL ảnh QR từ img.vietqr.io.
     * Ảnh PNG này đã nhúng sẵn số tài khoản, số tiền, nội dung CK,
     * đúng chuẩn NAPAS, quét được bằng tất cả app ngân hàng VN.
     *
     * Format: https://img.vietqr.io/image/<bankId>-<accountNo>-compact2.png
     *         ?amount=<amount>&addInfo=<note>&accountName=<name>
     */
    public String buildVietQRQuickLink(long amount, String note) {
        String encodedNote = URLEncoder.encode(note, StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        // bankBin field holds the bank short-code (e.g. "vcb" for Vietcombank)
        // We derive bank ID from BIN via a small lookup; if unknown, fall back to BIN
        String bankId = binToBankId(bankBin);
        return String.format(
            "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
            bankId, accountNumber, amount, encodedNote, encodedName
        );
    }

    /** Map BIN → VietQR bank short-code used by img.vietqr.io */
    private static String binToBankId(String bin) {
        switch (bin) {
            case "970436": return "vcb";        // Vietcombank
            case "970422": return "mbbank";     // MB Bank
            case "970418": return "bidv";       // BIDV
            case "970415": return "vietinbank"; // VietinBank
            case "970407": return "techcombank";// Techcombank
            case "970423": return "tpbank";     // TPBank
            case "970416": return "acb";        // ACB
            case "970432": return "vpbank";     // VPBank
            case "970426": return "msb";        // MSB
            case "970403": return "sacombank";  // Sacombank
            default:       return bin;          // fallback: dùng BIN trực tiếp
        }
    }

    /**
     * MoMo deep link — mở thẳng app MoMo trên điện thoại.
     */
    public String buildMomoDeepLink(long amount, String note) {
        String encodedNote = URLEncoder.encode(note, StandardCharsets.UTF_8);
        return String.format(
            "momo://app?action=payWithAppToken&isScanQR=false&phone=%s&amount=%d&note=%s",
            momoPhone, amount, encodedNote
        );
    }

    // Getters cho controller / template
    public String getAccountNumber() { return accountNumber; }
    public String getAccountName()   { return accountName; }
    public String getBankName()      { return bankName; }
    public String getMomoPhone()     { return momoPhone; }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** Tạo TLV (Tag-Length-Value) theo chuẩn EMVCo. */
    private static String tlv(String id, String value) {
        return String.format("%s%02d%s", id, value.length(), value);
    }

    /** Cắt chuỗi nếu vượt max (EMVCo giới hạn nhiều field). */
    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }

    /** Bỏ dấu tiếng Việt để tránh lỗi encoding trong QR text. */
    private static String removeAccents(String s) {
        if (s == null) return "";
        String normalized = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "")
                         .replaceAll("[^\\x00-\\x7F]", "");
    }

    /**
     * CRC-16/CCITT-FALSE (polynomial 0x1021, init 0xFFFF).
     * Đây là thuật toán CRC bắt buộc theo EMVCo QR spec.
     */
    private static String crc16(String payload) {
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= ((b & 0xFF) << 8);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }

    /** Mã hoá chuỗi QR payload thành ảnh PNG base64. */
    private static String encodeToBase64Png(String content, int width, int height)
            throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}
