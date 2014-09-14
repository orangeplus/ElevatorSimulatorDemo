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
public class Elevators extends Controller {
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

    public static Result getElevator(Long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        if (elevator == null) {
            return notFound();
        }
        return ok(Elevators.elevatorToJson(elevator));
    }


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
            if (maxPeople < 1) {
                return badRequest("Max people must be greater than zero");
            }

            Elevator.create(building, maxPeople);
            return Buildings.get(buildingId);
        }
    }

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
            if (maxPeople < 1) {
                return badRequest("Max people must be greater than zero");
            }

            elevator.setMaxPeople(maxPeople);
            elevator.update();
            return Buildings.get(buildingId);
        }
    }

    public static Result delete(Long buildingId, Long elevatorId) {
        Elevator elevator = Elevator.find.byId(elevatorId);
        Result validate = validateElevator(elevator, buildingId);
        if (validate != null){
            return validate;
        }

        elevator.delete();
        return Buildings.get(buildingId);
    }

    public static ObjectNode elevatorToJson(Elevator elevator) {
        ObjectNode elevatorNode = Json.newObject();
        elevatorNode.put("id", elevator.getId());
        elevatorNode.put("maxPeople", elevator.getMaxPeople());
        elevatorNode.put("building_id", elevator.getBuilding().getId());

        if (elevator.getFloors().size() == 0) {
            elevatorNode.put("floors", "0");
        } else {
            elevatorNode.put("floors", StringUtils.join(elevator.getListOfFloors(), ','));
        }
        return elevatorNode;
    }

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
