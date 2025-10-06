package com.example.lab1.resources;

import com.example.lab1.dto.MagicCityDto;
import com.example.lab1.entities.MagicCity;
import com.example.lab1.mappers.MagicCityMapper;
import com.example.lab1.services.MagicCityService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Map;

@Path("/cities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MagicCityResource {

    @Inject
    private MagicCityService magicCityservice;

    @GET
    public Response list() {
        List<MagicCity> cities = magicCityservice.findAllMagicCities();
        if (cities.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(cities).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        MagicCity city = magicCityservice.findMagicCity(id);
        if (city == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(city).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = magicCityservice.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, MagicCity city) {
        MagicCity updated = magicCityservice.update(id, city);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @POST
    @Transactional
    public Response create(MagicCityDto dto, @Context UriInfo uriInfo) {

        try {
            MagicCity magicCity = MagicCityMapper.toEntity(dto);
            magicCity = magicCityservice.create(magicCity);
            MagicCityDto magicCityDto = MagicCityMapper.toDTO(magicCity);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(magicCity.getId()));
            return Response.created(builder.build()).entity(magicCityDto).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // special operation: destroy elf cities
    @DELETE
    @Path("/elves")
    @Transactional
    public Response deleteElfCities() {
        int deleted = magicCityservice.deleteElfCities();
        return Response.ok(Map.of("deleted", deleted)).build();
    }
}
