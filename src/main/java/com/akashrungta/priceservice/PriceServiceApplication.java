package com.akashrungta.priceservice;

import com.akashrungta.priceservice.core.RecordsManager;
import com.akashrungta.priceservice.resources.ConsumerResource;
import com.akashrungta.priceservice.resources.ProviderResource;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

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
        bootstrap.addBundle(new SwaggerBundle<PriceServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(PriceServiceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(final PriceServiceConfiguration configuration,
                    final Environment environment) {
        final ProviderResource providerResource = new ProviderResource(RecordsManager.getInstance());
        final ConsumerResource consumerResource = new ConsumerResource(RecordsManager.getInstance());
        environment.jersey().register(providerResource);
        environment.jersey().register(consumerResource);
        environment.healthChecks().register("healthcheck", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }

}
