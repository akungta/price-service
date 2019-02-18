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
@Path("/provider/{session_id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProviderResource {

    /**
     * Provider Indicator stores current state of the given indicator
     */
    private final ConcurrentMap<String, Indicator> providerIndicator = new ConcurrentHashMap<>();

    private final RecordsManager recordsManager;

    public ProviderResource(RecordsManager recordsManager) {
        this.recordsManager = recordsManager;
    }

    @POST
    @Timed
    @Path("/{indicator}")
    public Response update(@PathParam("session_id") @Valid String session_id, @Valid @NotNull @PathParam("indicator") Indicator indicator) {
        providerIndicator.merge(session_id, indicator, (oldIndicator,newIndicator) -> {
            /**
             * Below is the state change check, allowed changes are START -> COMPLETE -> START
             * or START -> CANCEL -> START. All other changes are suppose to throw Exceptions
             */
            if(oldIndicator == Indicator.START){
                if(newIndicator == Indicator.START){
                    throw new WebApplicationException("Already Started for " + session_id, Response.Status.NOT_ACCEPTABLE);
                } else if(newIndicator == Indicator.COMPLETE){
                    recordsManager.commit(session_id);
                } else if(newIndicator == Indicator.CANCEL){
                    recordsManager.rollback(session_id);
                }
                return newIndicator;
            } else {
                if(newIndicator == Indicator.START){
                    return newIndicator;
                } else {
                    throw new WebApplicationException("Can't do anything for " + session_id, Response.Status.NOT_ACCEPTABLE);
                }
            }
        });
        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/upload")
    public Response upload(@PathParam("session_id") @Valid String session_id, @NotNull @Valid Upload upload) {
        // Only allowed to upload if START indication is provided as per requirements
        if(providerIndicator.get(session_id) == Indicator.START){
            recordsManager.prepare(session_id, upload.getRecords());
            return Response.ok().build();
        } else {
            throw new WebApplicationException("Provider has not indicated batch START", Response.Status.NOT_ACCEPTABLE);
        }
    }

}
