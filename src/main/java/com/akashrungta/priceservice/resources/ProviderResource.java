package com.akashrungta.priceservice.resources;

import com.akashrungta.priceservice.models.Upload;
import com.akashrungta.priceservice.models.enums.Indicator;
import com.codahale.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Path("/provider/{provider}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProviderResource {

    private ConcurrentMap<String, Indicator> providerIndicator = new ConcurrentHashMap<>();

    @POST
    @Timed
    @Path("/{indicator}")
    public Response update(@PathParam("provider") @Valid String provider, @PathParam("Indicator") Optional<Indicator> indicator) {
        if(indicator.isPresent()){
            providerIndicator.merge(provider, indicator.get(), (oldIndicator,newIndicator) -> {
                if(oldIndicator == Indicator.START){
                    if(newIndicator == Indicator.START){
                        throw new WebApplicationException("Already Started for " + provider, Response.Status.NOT_ACCEPTABLE);
                    } else if(newIndicator == Indicator.COMPLETE){
                        //complete copy
                    } else if(newIndicator == Indicator.CANCEL){
                        //cleanup
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
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    @POST
    @Timed
    @Path("/upload")
    public Response upload(@PathParam("provider") @Valid String provider, @NotNull @Valid Upload upload) {
        if(providerIndicator.get(provider) == Indicator.START){

        } else {
            throw new WebApplicationException("Provider has not indicated batch START", Response.Status.NOT_ACCEPTABLE);
        }
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

}
