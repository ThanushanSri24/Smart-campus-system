package com.smartcampus.resource;

import com.smartcampus.service.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.getRooms().values());
        return Response.ok(roomList).build();
    }


    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Room 'id' is required."))
                    .build();
        }
        if (room.getName() == null || room.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Room 'name' is required."))
                    .build();
        }
        if (DataStore.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody("A room with id '" + room.getId() + "' already exists."))
                    .build();
        }

        DataStore.putRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room '" + roomId + "' not found."))
                    .build();
        }
        return Response.ok(room).build();
    }

    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room '" + roomId + "' not found."))
                    .build();
        }

        // block deletion if sensors are still assigned
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Cannot delete room '" + roomId + "'. It still has " +
                            room.getSensorIds().size() + " sensor(s) assigned: " + room.getSensorIds()
            );
        }

        DataStore.deleteRoom(roomId);
        return Response.noContent().build(); 
    }



    private java.util.Map<String, String> errorBody(String message) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("error", message);
        return body;
    }
}
