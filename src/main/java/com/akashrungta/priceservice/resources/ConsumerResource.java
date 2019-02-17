package com.akashrungta.priceservice.resources;

import com.akashrungta.priceservice.core.IncompleteRecordsManager;
import com.akashrungta.priceservice.core.RecordsManager;
import com.akashrungta.priceservice.models.LatestPrice;
import com.akashrungta.priceservice.models.Upload;
import com.akashrungta.priceservice.models.enums.Indicator;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Api
@Path("/consumer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsumerResource {

    private final RecordsManager recordsManager;

    public ConsumerResource(RecordsManager recordsManager) {
        this.recordsManager = recordsManager;
    }

    @POST
    @Timed
    @Path("/{instrument_id}")
    public Response lastestPrice(@PathParam("instrument_id") @Valid Integer instrumentId) {
        Optional<BigDecimal> price = recordsManager.getPrice(instrumentId);
        if(price.isPresent()){
            return Response.ok().entity(new LatestPrice(instrumentId, price.get())).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("No price found.").build();
    }

}
