package com.api.xpress.airtime.data.repository;

import com.api.xpress.airtime.data.models.AirtimePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirtimePurchaseRepository extends JpaRepository<AirtimePurchase, String> {
}
