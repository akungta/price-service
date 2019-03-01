package com.akashrungta.priceservice;

import com.akashrungta.priceservice.core.JobExecutionService;
import com.akashrungta.priceservice.resources.InstrumentResource;
import com.akashrungta.priceservice.resources.SessionResource;
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
        environment.lifecycle().manage(new JobExecutionService());
        environment.jersey().register(new InstrumentResource());
        environment.jersey().register(new SessionResource());
        environment.healthChecks().register("healthcheck", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }

}
