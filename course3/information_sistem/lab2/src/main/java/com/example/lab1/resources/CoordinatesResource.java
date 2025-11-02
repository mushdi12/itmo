package com.example.lab1.resources;

import com.example.lab1.dto.CoordinatesDto;
import com.example.lab1.entities.Coordinates;
import com.example.lab1.mappers.CoordMapper;
import com.example.lab1.services.CoordinatesService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Map;

@Path("/coords")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesResource {

    @Inject
    private CoordinatesService coordinatesService;

    @GET
    public Response list() {
        List<Coordinates> coords = coordinatesService.findAllCoords();
         if (coords.isEmpty()) {
             return Response.status(Response.Status.NOT_FOUND).build();
         }
        return Response.ok(coords).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Coordinates coords = coordinatesService.findCoords(id);
        if (coords == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(coords).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = coordinatesService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, CoordinatesDto dto) {
        Coordinates coordinates = CoordMapper.toEntity(dto);
        Coordinates updated = coordinatesService.update(id,coordinates);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @POST
    public Response create(CoordinatesDto dto, @Context UriInfo uriInfo) {

        try {
            Coordinates coordinates = CoordMapper.toEntity(dto);
            coordinates  = coordinatesService.create(coordinates);
            CoordinatesDto outDto = CoordMapper.toDTO(coordinates);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(coordinates.getId()));
            return Response.created(builder.build()).entity(outDto).build();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Ошибка при создании координат: " + e.getMessage()))
                    .build();
        }
    }

}
