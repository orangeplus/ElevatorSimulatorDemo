package models;

/**
 * Created by keith on 9/13/2014.
 */
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Elevator extends Model {
    @Id
    private long id;
    public long getId() {
        return id;
    }

    @Constraints.Required
    @ManyToOne
    private Building building;
    public Building getBuilding() { return building;}
    public void setBuilding(Building building) { this.building = building;}

    @OneToMany(cascade = CascadeType.ALL)
    private List<ElevatorFloor> floors;
    public List<ElevatorFloor> getFloors() { return floors;}


    @Constraints.Required
    private long maxPeople;
    public long getMaxPeople() { return maxPeople;}
    public void setMaxPeople(long maxPeople) { this.maxPeople = maxPeople;}

    public static Elevator create(Building building, Long maxPeople) {
        Elevator elevator = new Elevator();
        elevator.setBuilding(building);
        elevator.setMaxPeople(maxPeople);
        elevator.save();
        return elevator;
    }

    public static final Finder<Long, Elevator> find = new Finder<>(
            Long.class, Elevator.class
    );

    public static List<Elevator> findByBuilding(Building building) {
        return find.where().eq("building", building).findList();
    }

    public void clearFloors() {
        // TODO: rawSQL would be more performant
        for (ElevatorFloor floor: getFloors()) {
            floor.delete();
        }
    }

    public void setFloors(long[] floors) {
        clearFloors();
        for (long floor: floors) {
            ElevatorFloor elevatorFloor = ElevatorFloor.create(this, floor);
        }
    }

    public List<Long> getListOfFloors() {
        List<Long> floors = new ArrayList<>();
        for (int i=0; i < getFloors().size(); i++ ) {
            floors.add(getFloors().get(i).getId());
        }
        return floors;
    }
}
