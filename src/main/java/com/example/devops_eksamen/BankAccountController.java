package com.example.devops_eksamen;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class BankAccountController {


    private final MeterRegistry meterRegistry;

    @Autowired
    public BankAccountController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostMapping(path = "/tx", consumes = "application/json", produces = "application/json")
    public void addMember(@RequestBody Transaction tx) {
        meterRegistry.counter("txcount2", "currency", tx.getCurrency()).increment();
        meterRegistry.gauge("CurrencyGauge", tx.getIntCurrency());

    }
}
