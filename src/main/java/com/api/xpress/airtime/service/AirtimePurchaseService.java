package com.api.xpress.airtime.service;

import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;

import java.io.IOException;

public interface AirtimePurchaseService {
  AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO);
}
