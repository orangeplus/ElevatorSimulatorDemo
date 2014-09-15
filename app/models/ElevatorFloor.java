package models;
/**
 * Created by keith on 9/13/2014.
 */

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;


@Entity
public class ElevatorFloor extends Model {
    @Id
    private long id;
    public long getId() {
        return id;
    }

    @Constraints.Required
    private long floor;
    public long getFloor() { return floor;}
    public void setFloor(long floor) { this.floor = floor; }

    @Constraints.Required
    @ManyToOne
    private Elevator elevator;
    public Elevator getElevator() { return elevator;}
    public void setElevator(Elevator elevator) { this.elevator = elevator;}

    public static final Finder<Long, ElevatorFloor> find = new Finder<>(
            Long.class, ElevatorFloor.class
    );

    public static ElevatorFloor findByElevatorAndFloor(Elevator elevator, long floor) {
        return find.where().eq("elevator_id", elevator.getId()).eq("floor", floor).findUnique();
    }

    public static ElevatorFloor create(Elevator elevator, long floor) {
        ElevatorFloor elevatorFloor = new ElevatorFloor();
        elevatorFloor.setElevator(elevator);
        elevatorFloor.setFloor(floor);
        elevatorFloor.save();
        return elevatorFloor;
    }

}
