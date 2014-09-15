package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Building;
import models.Elevator;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by keith on 9/13/2014.
 */
public class Elevators extends ApiController {
    /**
     * Returns JSON of all elevators for a building
     * @param buildingId
     * @return JSON result
     */
    public static Result get(Long buildingId) {
        Building building = Building.find.byId(buildingId);
        if (building == null) {
            return notFound("Building not found");
        }

        ObjectNode node = Json.newObject();
        ArrayNode elevatorsNodes = node.putArray("elevators");

        for (Elevator elevator: building.getElevators()) {
            elevatorsNodes.add(Elevators.elevatorToJson(elevator));
        }

        return ok(node);
    }

    /**
     * Returns JSON of a single elevator
     * @param elevatorId
     * @return
     */
    public static Result getElevator(Long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        if (elevator == null) {
            return notFound();
        }
        return ok(Elevators.elevatorToJson(elevator));
    }

    /**
     * Creates an elevator from a building takes in maxPeople and isExpress from the body of the request
     * returns JSON from get
     * @param buildingId
     * @return JSON result
     */
    public static Result create(Long buildingId) {
        Building building = Building.find.byId(buildingId);
        if (building == null) {
            return notFound("Building not found");
        }

        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("No data sent");
        } else {
            Long maxPeople = json.findPath("maxPeople").longValue();
            boolean isExpress = json.findPath("isExpress").booleanValue();
            if (maxPeople < 1) {
                return badRequest("Max people must be greater than zero");
            }

            Elevator.create(building, maxPeople, isExpress);
            return get(buildingId);
        }
    }

    /**
     * Edits an elevator record, returns get
     * @param buildingId
     * @param elevatorId
     * @return JSON result
     */
    public static Result edit(Long buildingId, Long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        Result validate = validateElevator(elevator, buildingId);
        if (validate != null){
            return validate;
        }

        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("No data sent");
        } else {
            Long maxPeople = json.findPath("maxPeople").longValue();
            boolean isExpress = json.findPath("isExpress").booleanValue();

            if (maxPeople < 1) {
                return badRequest("Max people must be greater than zero");
            }

            elevator.setisExpress(isExpress);
            elevator.setMaxPeople(maxPeople);
            elevator.update();
            return get(buildingId);
        }
    }

    /**
     * Deletes an elevator record. Returns get.
     * @param buildingId
     * @param elevatorId
     * @return JSON result
     */
    public static Result delete(Long buildingId, Long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        Result validate = validateElevator(elevator, buildingId);
        if (validate != null){
            return validate;
        }

        elevator.delete();
        return get(buildingId);
    }

    /**
     * Returns a JSON object for an elevator record
     * @param elevator
     * @return JSON ObjectNode
     */
    public static ObjectNode elevatorToJson(Elevator elevator) {
        ObjectNode elevatorNode = Json.newObject();
        elevatorNode.put("id", elevator.getId());
        elevatorNode.put("maxPeople", elevator.getMaxPeople());
        elevatorNode.put("isExpress", elevator.isExpress());
        elevatorNode.put("building_id", elevator.getBuilding().getId());

        // if elevator is not express, it has no specific floors
        if (elevator.isExpress()) {
            elevatorNode.put("floors", StringUtils.join(elevator.getListOfFloors(), ','));
        } else {
            elevatorNode.put("floors", "0");
        }
        return elevatorNode;
    }

    /**
     * Tests to ensure that an elevator exists and that it belongs to the specified building
     * @param elevator
     * @param buildingId
     * @return null or an error result
     */
    public static Result validateElevator(Elevator elevator, long buildingId) {
        if (elevator == null) {
            return notFound("Elevator not found");
        }

        if (elevator.getBuilding().getId() != buildingId) {
            return badRequest("Elevator does not belong to this building");
        }

        return null;
    }

}
