package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.enums.Indicator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Provider {

    private Provider(){ }

    private static class Holder {
        private static final Provider INSTANCE = new Provider();
    }

    public static Provider getInstance() {
        return Holder.INSTANCE;
    }

    private ConcurrentMap<Integer, Indicator> providerIndicator = new ConcurrentHashMap<>();

    public void updateIndication(Integer providerId, Indicator indicator){
        if(providerIndicator.containsKey(providerId) && providerIndicator.get(providerId) == Indicator.START){

        }
        this.providerIndicator.put(providerId, indicator);
    }


}
