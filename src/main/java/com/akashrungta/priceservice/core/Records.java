package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.enums.Indicator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Records {

    private Records(){ }

    private static class Holder {
        private static final Records INSTANCE = new Records();
    }

    public static Records getInstance() {
        return Holder.INSTANCE;
    }



}
