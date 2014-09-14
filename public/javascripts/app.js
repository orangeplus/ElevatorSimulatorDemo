(function () {

    var App = Ember.Application.create({
        LOG_TRANSITIONS: true
    });

    const GROUND = 'ground', VISITED = 'visited', NOTVISITED = 'not-visited';


    // Routes
    App.Router.map(function() {
        this.route("about");
        this.route("buildings", function() {
            this.resource('building', { path: ':building_id' }, function() {
                this.resource('elevator', { path: ':elevator_id' });
            });
        });
    });

    App.BuildingsRoute = Ember.Route.extend({
        model: function() {
            return this.store.find("building");
        }
    });

    App.BuildingRoute = Ember.Route.extend({
        setupController: function (controller, model) {
            var building;
            $.getJSON('/buildings/' + model.get('id'),
                function (data) {
                    building = Ember.Object.create({
                        id: data.id,
                        name: data.name,
                        people: data.people,
                        maxFloor: data.maxFloor,
                        elevators: []
                    });

                    for (var i = 0; i < data.elevators.length; i++) {
                        var floors = $.map(data.elevators[i].floors.split(','), function(value){
                            return parseInt(value, 10);
                        });

                        var type, number = i + 1;
                        if (data.elevators[i].floors === "0") {
                            type = "standard";
                        } else {
                            type = "express";
                        }

                        var elevator = Ember.Object.create({
                            id: data.elevators[i].id,
                            number: number,
                            building_id: data.elevators[i].building_id,
                            floors: data.elevators[i].floors,
                            elevatorType: type,
                            displayFloors: []
                        });



                        for (var j = 0; j < building.get('maxFloor'); j++) {
                            var floorNumber = j + 1, floorType;

                            if (j === 0) {
                                floorType = GROUND;
                            } else if (elevator.get('floors') === '0') {
                                floorType = VISITED;
                            } else if ($.inArray(floorNumber, floors) > - 1) {
                                floorType = VISITED;
                            } else {
                                floorType = NOTVISITED;
                            }

                            var floor = Ember.Object.create({
                                floorClass: 'floor',
                                floor: floorNumber,
                                floorType: floorType
                            });

                            elevator.displayFloors.pushObject(floor);
                        }

                        building.elevators.pushObject(elevator);

                    }
                    controller.set("model", building);
                });
        }
    });

    App.ElevatorView = Ember.View.extend({

    });

    App.ElevatorController = Ember.Controller.extend({
        clickFloor: function() {

        },
        addFloor: function() {
        },
        removeFloor: function() {
        },
        addElevator: function() {

        },
        updateElevator: function() {

        },
        deleteElevator: function() {

        }
    });

    // Models
    App.Building = DS.Model.extend({
        name: DS.attr('string'),
        maxFloor: DS.attr('number'),
        people: DS.attr('number'),
        createDate: DS.attr('date')
    });

    App.Elevator = DS.Model.extend({
        maxPerson: DS.attr('number'),
        building: DS.belongsTo('building'),
        floors: DS.attr('string')
    });




})();