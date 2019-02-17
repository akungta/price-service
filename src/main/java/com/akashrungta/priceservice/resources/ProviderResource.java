package com.akashrungta.priceservice.resources;

import com.akashrungta.priceservice.core.RecordsManager;
import com.akashrungta.priceservice.models.Upload;
import com.akashrungta.priceservice.models.enums.Indicator;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Api
@Path("/provider/{provider}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProviderResource {

    private final ConcurrentMap<String, Indicator> providerIndicator = new ConcurrentHashMap<>();

    private final RecordsManager recordsManager;

    public ProviderResource(RecordsManager recordsManager) {
        this.recordsManager = recordsManager;
    }

    @POST
    @Timed
    @Path("/{indicator}")
    public Response update(@PathParam("provider") @Valid String provider, @Valid @NotNull @PathParam("indicator") Indicator indicator) {
        providerIndicator.merge(provider, indicator, (oldIndicator,newIndicator) -> {
            if(oldIndicator == Indicator.START){
                if(newIndicator == Indicator.START){
                    throw new WebApplicationException("Already Started for " + provider, Response.Status.NOT_ACCEPTABLE);
                } else if(newIndicator == Indicator.COMPLETE){
                    recordsManager.commit(provider);
                } else if(newIndicator == Indicator.CANCEL){
                    recordsManager.rollback(provider);
                }
                return newIndicator;
            } else {
                if(newIndicator == Indicator.START){
                    return newIndicator;
                } else {
                    throw new WebApplicationException("Can't do anything for " + provider, Response.Status.NOT_ACCEPTABLE);
                }
            }
        });
        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/upload")
    public Response upload(@PathParam("provider") @Valid String provider, @NotNull @Valid Upload upload) {
        if(providerIndicator.get(provider) == Indicator.START){
            recordsManager.prepare(provider, upload.getRecords());
            return Response.ok().build();
        } else {
            throw new WebApplicationException("Provider has not indicated batch START", Response.Status.NOT_ACCEPTABLE);
        }
    }

}
