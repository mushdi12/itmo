package com.example.lab1.resources;

import com.example.lab1.dto.RingDto;
import com.example.lab1.entities.Ring;
import com.example.lab1.mappers.RingsMapper;
import com.example.lab1.services.RingService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Map;

@Path("/rings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RingResource {

    @Inject
    private RingService ringService;

    @GET
    public Response list() {
        List<Ring> rings = ringService.findAllRings();
        if (rings.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(rings).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Ring ring = ringService.findRing(id);
        if (ring == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ring).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = ringService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Ring ring) {
        Ring updated = ringService.update(id, ring);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @POST
    @Transactional
    public Response create(RingDto dto, @Context UriInfo uriInfo) {
        try {
            Ring ring = RingsMapper.toEntity(dto);
            ring = ringService.create(ring);
            RingDto outRing = RingsMapper.toDTO(ring);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(ring.getId()));
            return Response.created(builder.build()).entity(outRing).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

}
