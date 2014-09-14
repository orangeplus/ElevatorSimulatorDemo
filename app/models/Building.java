package models;

/**
 * Created by keith on 9/13/2014.
 */

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Building extends Model {
    @Id
    private long id;
    public long getId() {
        return id;
    }

    @Constraints.Required
    private String name;
    public String getName() { return name;}
    public void setName(String name) { this.name = name;}

    @Constraints.Required
    private long maxFloor;
    public long getMaxFloor() { return maxFloor;}
    public void setMaxFloor(long maxFloor) { this.maxFloor = maxFloor;}

    @Constraints.Required
    private long people;
    public long getPeople() { return people;}
    public void setPeople(long people) { this.people = people;}

    @OneToMany(cascade = CascadeType.ALL)
    private List<Elevator> elevators;
    public List<Elevator> getElevators() { return elevators;}

    private Timestamp createDate;
    public Timestamp getCreateDate() { return createDate;}

    public static final Finder<Long, Building> find = new Finder<>(
            Long.class, Building.class
    );

    public static Building create(String name, long people, long maxFloor) {
        Building building = new Building();
        building.setName(name);
        building.setMaxFloor(maxFloor);
        building.setPeople(people);
        building.save();
        return building;
    }
}
