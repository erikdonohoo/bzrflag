angular.module("BZRFlag").controller("TankCtrl", 
	["$scope", "Game", "$timeout", "$routeParams", "Tank",
	function($scope, Game, $timeout, $routeParams, Tank){

	// Models
	var data = {};
	data.gameId = $routeParams.gameid;
	data.tankId = $routeParams.tankid;
	data.visualMultiplier = 13;
	data.numSteps = 20;
	data.type = "all";
	data.rejectors = {};
	data.tangentials = {};
	data.tank = Tank.get({gameId: data.gameId, id: data.tankId}, function(){
		data.tank.gameId = data.gameId;
	});

	$scope.data = data;

	function updateField(type, value, field) {
		for (var i = data.tank.potentialFields.length - 1; i >= 0; i--) {
			 var pf = data.tank.potentialFields[i];
			 if (pf.type == type) {
			 	for (key in pf) {
			 		if (key == field)
			 			pf[key] = value;
			 	}
			 }
		};
	}

	// Functions
	$scope.updateTank = function() {

		// Update rejctors
		if (data.rejectors.strength) {
			updateField("rejector", data.rejectors.strength, "strength");
		}
		if (data.rejectors.spread) {
			updateField("rejector", data.rejectors.spread, "spread");
		}
		if (data.rejectors.radius) {
			updateField("rejector", data.rejectors.radius, "radius");
		}

		data.tank.$update();
	}
}])