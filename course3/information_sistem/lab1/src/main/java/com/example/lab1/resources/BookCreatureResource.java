package com.example.lab1.resources;

import com.example.lab1.dto.BookCreatureDto;
import com.example.lab1.entities.BookCreature;
import com.example.lab1.mappers.BookCreatureMapper;
import com.example.lab1.services.BookCreatureService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Map;

@Path("/creatures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookCreatureResource {

    @Inject
    private BookCreatureService bookCreatureService;

    @GET
    public Response list() {
        List<BookCreature> cities = bookCreatureService.findAllBookCreatures();
        if (cities.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(cities).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        BookCreature bc = bookCreatureService.findBookCreatures(id);
        if (bc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(bc).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = bookCreatureService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, BookCreatureDto dto) {
        BookCreature bookCreature = BookCreatureMapper.toEntity(dto);
        BookCreature updated = bookCreatureService.update(id, bookCreature);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @POST
    @Transactional
    public Response create(BookCreatureDto dto, @Context UriInfo uriInfo) {
        try {

            BookCreature bookCreature = BookCreatureMapper.toEntity(dto);
            bookCreature = bookCreatureService.create(bookCreature);

            BookCreatureDto outDto = BookCreatureMapper.toDTO(bookCreature);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(bookCreature.getId().toString());
            return Response.created(builder.build()).entity(outDto).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // --- special operations ---
    @DELETE
    @Path("/attack-level/{value}")
    @Transactional
    public Response deleteByAttackLevel(@PathParam("value") Double value) {
        int deleted = bookCreatureService.deleteByAttackLevel(value);
        return Response.ok(Map.of("deleted", deleted)).build();
    }

    @GET
    @Path("/search")
    public Response findByNameContains(@QueryParam("substring") String substring) {
        if (substring == null || substring.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "substring is required")).build();
        }
        List<BookCreature> list = bookCreatureService.findByNameContains(substring);
        return Response.ok(list).build();
    }

    @GET
    @Path("/attack-levels/distinct")
    public Response distinctAttackLevels() {
        List<Double> values = bookCreatureService.findDistinctAttackLevels();
        return Response.ok(values).build();
    }

    @POST
    @Path("/swap-rings")
    @Transactional
    public Response swapRings(Map<String, Integer> body) {
        Integer a = body.get("firstId");
        Integer b = body.get("secondId");
        if (a == null || b == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "firstId and secondId are required")).build();
        }
        boolean ok = bookCreatureService.swapRings(a, b);
        return ok ? Response.ok(Map.of("swapped", true)).build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
