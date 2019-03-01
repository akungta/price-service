package com.akashrungta.priceservice.resources;

import com.akashrungta.priceservice.core.RecordsManager;
import com.akashrungta.priceservice.models.Upload;
import com.akashrungta.priceservice.models.enums.Status;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Below is the state change check, allowed changes are START -> COMPLETE -> START
 * or START -> CANCEL -> START. All other changes are suppose to throw Exceptions
 */
@Api
@Path("/session/{session_id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionResource {

    /**
     * Status stores current state of the given session
     */
    private final ConcurrentMap<String, Status> sessionStatus = new ConcurrentHashMap<>();

    private final RecordsManager recordsManager;

    public SessionResource(RecordsManager recordsManager) {
        this.recordsManager = recordsManager;
    }

    @POST
    @Timed
    @Path("/start")
    public Response start(@PathParam("session_id") @Valid String sessionId) {
        sessionStatus.merge(sessionId, Status.START, (oldStatus, newStatus) -> {
            if(oldStatus == Status.START){
                throw new WebApplicationException("Already Started for " + sessionId, Response.Status.NOT_ACCEPTABLE);
            } else {
                recordsManager.prepare(sessionId);
                return newStatus;
            }
        });
        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/complete")
    public Response complete(@PathParam("session_id") @Valid String sessionId) {
        sessionStatus.compute(sessionId, (sId, oldStatus) -> {
            if(oldStatus == null){
                throw new WebApplicationException("Can't complete un-started session " + sessionId, Response.Status.NOT_ACCEPTABLE);
            } else if(oldStatus == Status.START){
                recordsManager.complete(sessionId);
                return Status.COMPLETE;
            } else {
                throw new WebApplicationException("Invalid operation for session " + sessionId, Response.Status.NOT_ACCEPTABLE);
            }
        });
        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/cancel")
    public Response cancel(@PathParam("session_id") @Valid String sessionId) {
        sessionStatus.compute(sessionId, (sId, oldStatus) -> {
            if(oldStatus == null){
                throw new WebApplicationException("Can't cancel un-started session " + sessionId, Response.Status.NOT_ACCEPTABLE);
            } else if(oldStatus == Status.START){
                recordsManager.cancel(sessionId);
                return Status.CANCEL;
            } else {
                throw new WebApplicationException("Invalid operation for session " + sessionId, Response.Status.NOT_ACCEPTABLE);
            }
        });
        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/upload")
    public Response upload(@PathParam("session_id") @Valid String sessionId, @NotNull @Valid Upload upload) {
        // Only allowed to upload if START indication
        if(sessionStatus.get(sessionId) == Status.START){
            recordsManager.upload(sessionId, upload.getRecords());
            return Response.ok().build();
        } else {
            throw new WebApplicationException("Provider has not indicated session start ", Response.Status.NOT_ACCEPTABLE);
        }
    }

}
