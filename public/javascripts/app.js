(function () {

    var App = Ember.Application.create({
        LOG_TRANSITIONS: true
    });

    const GROUND = 'ground', VISITED = 'visited', NOTVISITED = 'not-visited';


    /****************************************************
     ROUTER
     ****************************************************/

    App.Router.map(function() {
        this.route("about");
        this.route("buildings", function() {
            this.resource('newBuilding', {path: 'new'});
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


    /****************************************************
     ROUTEES
     ****************************************************/

    App.BuildingsRoute = Ember.Route.extend({
        model: function() {
            return this.store.find("building");
        }
    });


    App.NewBuildingRoute = Ember.Route.extend({
        model: function() {
            return this.store.createRecord('building');
        },

        setupController: function (controller, model) {
            console.log(controller);
            controller.set("model", model);
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
            var elevator = App.ElevatorModel.create({});
            elevator.set('id', 0);
            elevator.set('maxFloor', model.maxFloor);
            elevator.set('buildingId', model.id);
            elevator.set('floors', "0");
            elevator.set('isExpress',false);
            elevator.set('maxPeople', 10);
            controller.set("model", elevator);
        }
    });

    App.ElevatorRoute = Ember.Route.extend({
        setupController: function (controller, model) {
            var elevator = App.ElevatorModel.create({});
            var con = controller;
            elevator.fetch(model.get('id'), model.get('maxFloor'), function(){
                controller.set("model", elevator);
            });
        }
    });

    /****************************************************
     Views
     ****************************************************/

    App.ElevatorFloorView = Ember.View.extend({
        // add and remove floors
        click: function() {
            var self = this;
            if (this.get('content').get('floorType') == GROUND) {
                alert("Ground floor cannot be changed");
            }  else if (! this.get('content').get('isExpress')) {
                alert("This is a standard elevator, floors may not be removed.");
            }  else if (this.get('content').get('floorType') === VISITED) {
                self.get('content').set('floorType', NOTVISITED);
                $.ajax({
                    type: "DELETE",
                    url: '/elevators/' + this.get('content').get('elevatorId') + '/floors/' + this.get('content').get('floor'),
                    error: function () {
                        self.get('content').set('floorType', VISITED);
                    }
                });

            } else if (this.get('content').get('floorType') === NOTVISITED) {
                self.get('content').set('floorType', VISITED);
                    $.ajax({
                        type: "POST",
                        url: '/elevators/' + this.get('content').get('elevatorId') + '/floors',
                        data: JSON.stringify({floor: this.get('content').get('floor')}),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        error: function() {
                            console.log(xhr.status);
                            console.log(thrownError);
                           self.get('content').set('floorType', NOTVISITED);
                        }
                });
            }
            return false;
        }

    });

    /****************************************************
     Controllers
     ****************************************************/

    App.NewBuildingController = Ember.Controller.extend({
        actions: {
            createBuilding: function() {
                var record = this.store.createRecord('building',{
                    name: name,
                    maxFloor: maxFloor,
                    people: people
                });

                record.save();
                return true;
            }
        }
    })

    App.NewElevatorController = Ember.Controller.extend({
        isExpressValues: [
            {label: "Express", id: 1},
            {label: "Standard",id: 0}
        ],
        actions: {
            createElevator: function() {
                var self = this;
                self.model.createElevator(function(){
                    self.transitionTo('buildings', self.get('buildingId'));
                });
            }
        }
    });

    App.ElevatorController = Ember.ObjectController.extend({

        isEdit: false,
        isExpressValues: [
            {label: "Express", id: true},
            {label: "Standard",id: false}
        ],
        actions: {
            setEdit: function() {
                this.set('isEdit', true);
            },

            unSetEdit: function() {
                this.set('isEdit', false);
            },
            updateElevator: function() {
                var self = this;
                self.model.save(function(){
                    self.set('isEdit', false);
                });
            }
        }

    });

    /****************************************************
     Models
     ****************************************************/

    App.Building = DS.Model.extend({
        name: DS.attr('string'),
        maxFloor: DS.attr('number'),
        people: DS.attr('number'),
        createDate: DS.attr('date')
    });


    App.ElevatorModel = Ember.Object.extend({
        id: null,
        buildingId: null,
        floors: null,
        isExpress: null,
        maxPeople: null,
        maxFloor: null,
        displayFloors: [],
        test: "test",

        fetch: function(elevatorId, maxFloor, callback) {
            var self = this;
            $.getJSON('/elevators/' + elevatorId, function (data) {
                data.maxFloor = maxFloor;
                self.populate(data);
                if (callback !== undefined) {
                    callback();
                }
            });
        },

        populate: function(data) {
            this.set('id', data.id);
            this.set('maxFloor', data.maxFloor)
            this.set('buildingId', data.building_id);
            this.set('floors', data.floors);
            this.set('isExpress', data.isExpress);
            this.set('maxPeople', data.maxPeople);
            var displayFloors = this.populateFloors();
            this.set('displayFloors', displayFloors);
        },

        // Create floor models for entire building
        populateFloors: function() {
            var self = this;
            var displayFloors = [];
            var visitedFloors = $.map(this.get('floors').split(','), function (value) {
                return parseInt(value, 10);
            });

            for (var j = 0; j < self.get('maxFloor'); j++) {
                var floorNumber = j + 1, floorType;

                if (j === 0) {
                    floorType = GROUND;
                } else if (! self.get('isExpress')) {
                    floorType = VISITED;
                } else if ($.inArray(floorNumber, visitedFloors) > -1) {
                    floorType = VISITED;
                } else {
                    floorType = NOTVISITED;
                }

                var elevatorFloor = App.ElevatorFloorModel.create({});
                elevatorFloor.set('floor', floorNumber);
                elevatorFloor.set('floorType', floorType);
                elevatorFloor.set('elevatorId', self.get('id'));
                elevatorFloor.set('isExpress', self.get('isExpress'));

                displayFloors.pushObject(elevatorFloor);
            }
            return displayFloors;
        },

        save: function(callback) {
            var self = this;
            var data = JSON.stringify({
                maxPeople: self.get('maxPeople'),
                isExpress: self.get('isExpress')
            });

            $.ajax({
                type: "PUT",
                url: '/buildings/' + self.get('buildingId') + '/elevators/' + self.get('id'),
                data: data,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                error: function (xhr, ajaxOptions, thrownError) {
                    alert('Update Failed \n\n ' +thrownError);
                },
                success: function() {
                    var displayFloors = self.populateFloors();
                    self.set('displayFloors', displayFloors);
                    console.log('updated');
                    if (callback !== undefined) {
                        callback();
                    }
                }
            });
        },

        createNew: function() {
            var self = this;
            var data = JSON.stringify({
                maxPeople: self.get('maxPeople'),
                isExpress: self.get('isExpress')
            });

            $.ajax({
                type: "POST",
                url: '/buildings/' + self.get('buildingId') + '/elevators/',
                data: data,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                error: function () {
                    alert("update failed!")
                },
                success: function(data) {
                    console.log('updated');
                    this.populateFloors(); // in case isExpress has changed
                    if (callback !== undefined) {
                        callback();
                    }
                }
            });
        },

        elevatorType: function() {
            return (this.get('isExpress') ? "Express" : "Standard");
        }.property('isExpress')
    });

    App.ElevatorFloorModel = Ember.Object.extend({
        floorClass: 'floor',
        floor: null,
        floorType: null,
        elevatorId: null,
        isExpress: null
    });

    /****************************************************
     Helpers
     ****************************************************/
    Ember.Handlebars.helper('format-date', function(date) {
        return moment(date).format('dddd, MMM Do');
    });

})();