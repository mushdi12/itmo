package com.example.lab1.resources;

import com.example.lab1.config.CacheStatisticsConfig;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/cache-statistics")
@Produces(MediaType.APPLICATION_JSON)
public class CacheStatisticsResource {

    @GET
    @Path("/logging/status")
    public Response getLoggingStatus() {
        boolean enabled = CacheStatisticsConfig.isLoggingEnabled();
        return Response.ok(Map.of(
                "enabled", enabled,
                "message", enabled ? "Логирование статистики кэша включено" : "Логирование статистики кэша выключено"
        )).build();
    }

    @POST
    @Path("/logging/enable")
    public Response enableLogging() {
        CacheStatisticsConfig.enableLogging();
        return Response.ok(Map.of(
                "success", true,
                "message", "Логирование статистики кэша включено"
        )).build();
    }

    @POST
    @Path("/logging/disable")
    public Response disableLogging() {
        CacheStatisticsConfig.disableLogging();
        return Response.ok(Map.of(
                "success", true,
                "message", "Логирование статистики кэша выключено"
        )).build();
    }
}

