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
                this.resource('newElevator', {path: 'new'});
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
            // Populate model from Ajax
            $.getJSON('/buildings/' + model.get('id'),
                function (data) {
                    building = Ember.Object.create({
                        id: data.id,
                        name: data.name,
                        people: data.people,
                        maxFloor: data.maxFloor,
                        elevators: []
                    });

                    // populate Elevators
                    for (var i = 0; i < data.elevators.length; i++) {
                        var number = i + 1;

                        var elevator = Ember.Object.create({
                            id: data.elevators[i].id,
                            number: number,
                            floors: data.elevators[i].floors,
                            isExpress: data.elevators[i].isExpress,
                            maxFloor: data.maxFloor
                        });


                        building.elevators.pushObject(elevator);

                    }
                    controller.set("model", building);
                }
            );
        }
    });

    App.NewElevatorRoute = Ember.Route.extend({
        setupController: function (controller, model) {
            controller.set("model", model);
        }
    });

    App.ElevatorRoute = Ember.Route.extend({
        setupController: function (controller, model) {
            var elevator;
            $.getJSON('/elevators/' + model.id, function (data) {
                console.log(data);
                var floors = $.map(data.floors.split(','), function (value) {
                    return parseInt(value, 10);
                });

                var elevatorType = (data.isExpress) ? "Express" : "Standard";
                elevator = Ember.Object.create({
                    id: data.id,
                    building_id: data.building_id,
                    floors: data.floors,
                    isExpress: data.isExpress,
                    maxPeople: data.maxPeople,
                    elevatorType: elevatorType,
                    displayFloors: []
                });

                // Create floor models for entire building
                for (var j = 0; j < model.maxFloor; j++) {
                    var floorNumber = j + 1, floorType;

                    if (j === 0) {
                        floorType = GROUND;
                    } else if (! data.isExpress) {
                        floorType = VISITED;
                    } else if ($.inArray(floorNumber, floors) > -1) {
                        floorType = VISITED;
                    } else {
                        floorType = NOTVISITED;
                    }

                    var floor = Ember.Object.create({
                        floorClass: 'floor',
                        floor: floorNumber,
                        floorType: floorType,
                        elevatorId: elevator.get('id'),
                        isExpress: data.isExpress
                    });
                    elevator.displayFloors.pushObject(floor);
                }
                controller.set("model", elevator);

            });
        }
    });

    App.ElevatorView = Ember.View.extend({
        // add and remove floors
        click: function() {
            var self = this;
            if (this.get('content').get('floorType') == GROUND) {
                alert("Ground floor cannot be changed");
            }  else if (! this.get('content').get('isExpress')) {
                alert("This is a standard elevator, floors may not be removed.");
            }  else if (this.get('content').get('floorType') === VISITED) {
                $.ajax({
                    type: "POST",
                    url: '/elevators/' + this.get('content').get('elevatorId') + '/floors',
                    data: JSON.stringify({floor: this.get('content').get('floor')}),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function() {
                        self.get('content').set('floorType', NOTVISITED);
                    }
                });

            } else if (this.get('content').get('floorType') === NOTVISITED) {
                $.ajax({
                    type: "DELETE",
                    url: '/elevators/' + this.get('content').get('elevatorId') + '/floors/' + this.get('content').get('floor'),
                    success: function() {
                        self.get('content').set('floorType', VISITED);
                    }
                });
            }
            return false;
        }

    });
/*
    App.ElevatorController = Ember.Controller.extend({
        init: function() {

        },

        actions: {
            changeFloor: function() {
                var something;
            }
        }
    });

 */
    // Models
    App.Building = DS.Model.extend({
        name: DS.attr('string'),
        maxFloor: DS.attr('number'),
        people: DS.attr('number'),
        createDate: DS.attr('date')
    });
})();