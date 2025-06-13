package com.example.backend.points;

import com.example.backend.auth.jwt.Authenticating;
import com.example.backend.auth.jwt.JwtUtil;
import com.example.backend.utils.dtos.RespOneParameterDTO;
import com.example.backend.utils.dtos.ErrorDTO;
import com.example.backend.utils.exceptions.InvalidRadiusException;
import com.example.backend.utils.exceptions.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/points")
@Authenticating
public class PointsController {
    @EJB
    private PointsService pointsService;

    @GET
    public Response getPointsCount(@HeaderParam("Authorization") String accessToken) {
        String username = JwtUtil.getUsernameFromToken(accessToken);
        try{
            long count = this.pointsService.getCountPointsByUsername(username);
            RespOneParameterDTO<Long> answer = new RespOneParameterDTO<>(count);
            return Response.ok().header("Content-Type", "application/json").entity(answer).build();
        } catch (UserNotFoundException e){
            ErrorDTO err = new ErrorDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).header("Content-Type", "application/json").entity(err).build();
        } catch (Exception e){
            ErrorDTO err = new ErrorDTO("Failed to retrieve points count");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPointsSlice(JsonNode json, @HeaderParam("Authorization") String accessToken){
        String username = JwtUtil.getUsernameFromToken(accessToken);
        if (!json.get("start").isNumber() || !json.get("limit").isNumber()){
            ErrorDTO err = new ErrorDTO("Invalid parameters");
            return Response.status(Response.Status.BAD_REQUEST).header("Content-Type", "application/json").entity(err).build();
        }
        long start = json.get("start").asLong();
        long limit = json.get("limit").asLong();
        try{
            List<Point> pointList = this.pointsService.getSlicePoints(username, start, limit);
            return Response.ok().header("Content-Type", "application/json").entity(pointList).build();
        } catch (UserNotFoundException e){
            ErrorDTO err = new ErrorDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).header("Content-Type", "application/json").entity(err).build();
        } catch (Exception e){
            ErrorDTO err = new ErrorDTO("Failed to retrieve points slice");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPoint(@HeaderParam("Authorization") String accessToken, JsonNode pointForm){
        if (pointForm.get("x") == null || pointForm.get("y") == null || pointForm.get("r") == null ||
                !pointForm.get("x").isNumber() || !pointForm.get("y").isNumber() || !pointForm.get("r").isNumber()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        double x = pointForm.get("x").asDouble();
        double y = pointForm.get("y").asDouble();
        double r = pointForm.get("r").asDouble();
        String username = JwtUtil.getUsernameFromToken(accessToken);
        try{
            Point newPoint = this.pointsService.createPoint(x, y, r, username);
            return Response.ok().header("Content-Type", "application/json").entity(newPoint).build();
        } catch (UserNotFoundException e){
            ErrorDTO err = new ErrorDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).header("Content-Type", "application/json").entity(err).build();
        } catch (InvalidRadiusException e) {
            ErrorDTO err = new ErrorDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).header("Content-Type", "application/json").entity(err).build();
        }catch (Exception e){
            ErrorDTO err = new ErrorDTO("Failed to create point");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }
}
