package com.akashrungta.priceservice;

import com.akashrungta.priceservice.resources.ProviderResource;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PriceServiceApplication extends Application<PriceServiceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new PriceServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "PriceService";
    }

    @Override
    public void initialize(final Bootstrap<PriceServiceConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final PriceServiceConfiguration configuration,
                    final Environment environment) {
        final ProviderResource providerResource = new ProviderResource();
        environment.jersey().register(providerResource);
        environment.healthChecks().register("healthcheck", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }

}
