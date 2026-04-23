package com.volter.shop.modules.alert.application;

import com.volter.shop.modules.alert.domain.model.RiskAlert;
import com.volter.shop.modules.alert.domain.repository.RiskAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskAlertService {

    private final RiskAlertRepository riskAlertRepository;

    public RiskAlert save(RiskAlert riskAlert) {
        return riskAlertRepository.save(riskAlert);
    }
}
