package com.computershop.main.repositories;

import com.computershop.main.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findTopByOrderOrderIdAndProviderOrderByCreatedAtDesc(Integer orderId, String provider);

    List<PaymentTransaction> findByProviderOrderByCreatedAtDesc(String provider);

    @Query("select count(pt) from PaymentTransaction pt where pt.provider = :provider and pt.status = :status")
    long countByProviderAndStatus(@Param("provider") String provider, @Param("status") String status);

    @Query("select coalesce(sum(pt.amount), 0) from PaymentTransaction pt where pt.provider = :provider and pt.status = :status")
    BigDecimal sumAmountByProviderAndStatus(@Param("provider") String provider, @Param("status") String status);
}

