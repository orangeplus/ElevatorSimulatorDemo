package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Elevator;
import models.ElevatorFloor;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by keith on 9/13/2014.
 */
public class ElevatorFloors extends ApiController {
    public static Result create(long elevatorId) {

        Elevator elevator = Elevator.find.byId(elevatorId);
        if (elevator == null) {
            return notFound("Elevator not found");
        }


        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("No data sent");
        } else {
            Long floor = json.findPath("floor").longValue();
            if (floor < 2) {
                return badRequest("Floor above ground level is required");
            }

            ElevatorFloor elevatorFloor = ElevatorFloor.findByElevatorAndFloor(elevator, floor);
            if (elevatorFloor != null) {
                // return ok for this, the user's intent should have been met.
                return ok("Elevator already goes to this floor");
            }

            ElevatorFloor.create(elevator, floor);
            return ok(Elevators.elevatorToJson(elevator));
        }
    }

    public static Result delete(long elevatorId, long floor) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        if (elevator == null) {
            return notFound("Elevator not found");
        }

        ElevatorFloor elevatorFloor = ElevatorFloor.findByElevatorAndFloor(elevator, floor);
        if (elevatorFloor == null) {
            return notFound("Elevator floor not found");
        }

        elevatorFloor.delete();
        return ok();
    }

    public static Result deleteAll(long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        if (elevator == null) {
            return notFound("Elevator not found");
        }

        elevator.clearFloors();
        return ok();
    }


}
