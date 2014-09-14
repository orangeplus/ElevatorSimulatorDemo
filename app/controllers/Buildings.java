package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Building;
import models.Elevator;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keith on 9/13/2014.
 */
public class Buildings extends Controller {

    public static Result create() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("No data sent");
        } else {
            String name = json.findPath("name").textValue();
            Long people = json.findPath("people").longValue();
            Long maxFloor = json.findPath("maxFloor").longValue();

            List<String> errors = getBuldingSubmitErrors(name, people, maxFloor);
            if (errors.size() > 0) {
                return badRequest(StringUtils.join(errors, ','));
            }

            Building building = Building.create(name, people, maxFloor);
            return get(building.getId());
        }
    }

    public static Result getBuildingList() {
        ObjectNode result = Json.newObject();
        ArrayNode buildingNodes = result.putArray("buildings");
        for (Building building: Building.find.findList()) {
            buildingNodes.add(buildingToJson(building));
        }
        return ok(result);
    }

    public static ObjectNode buildingToJson(Building building) {
        ObjectNode result = Json.newObject();
        result.put("id", building.getId());
        result.put("name", building.getName());
        result.put("maxFloor", building.getMaxFloor());
        result.put("people", building.getPeople());
        result.put("createDate", building.getCreateDate().toString());
        return result;
    }

    public static Result get(long buildingId) {
        Building building = Building.find.byId(buildingId);
        if (building == null) {
            return notFound("Building not found");
        }

        ObjectNode result = buildingToJson(building);
        ArrayNode elevatorsNodes = result.putArray("elevators");

        for (Elevator elevator: building.getElevators()) {
            elevatorsNodes.add(Elevators.elevatorToJson(elevator));
        }

        return ok(result);
    }

    public static Result edit(Long buildingId) {
        Building building = Building.find.byId(buildingId);
        if (building == null) {
            return notFound("Building not found");
        }

        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest("No data sent");
        } else {
            String name = json.findPath("name").textValue();
            Long people = json.findPath("people").longValue();
            Long maxFloor = json.findPath("maxFloor").longValue();

            List<String> errors = getBuldingSubmitErrors(name, people, maxFloor);

            if (errors.size() > 0) {
                return badRequest(StringUtils.join(errors, ','));
            }

            building.setName(name);
            building.setMaxFloor(maxFloor);
            building.setPeople(people);
            building.update();

            return get(building.getId());
        }
    }


    public static Result delete(long buildingId) {
        Building building = Building.find.byId(buildingId);
        if (building == null) {
            return notFound("Building not found");
        }
        building.delete();
        return ok();
    }

    public static List<String> getBuldingSubmitErrors(String name, Long people, Long maxFloor) {
        List<String> result = new ArrayList<>();
        if (name == null) {
            result.add("Missing parameter");
        }

        if (people < 1) {
            result.add("People must be greater than zero");
        }

        if (maxFloor < 2) {
            result.add("Max floor must be greater than one");
        }
        return result;
    }
}
