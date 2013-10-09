angular.module("BZRFlag").controller("TankCtrl", 
	["$scope", "Game", "$timeout", "$routeParams", "Tank", "Backend",
	function($scope, Game, $timeout, $routeParams, Tank, Backend){

	// Models
	var data = {};
	data.gameId = $routeParams.gameid;
	data.tankId = $routeParams.tankid;
	data.prefix = Backend.getAPIPrefix();
	data.visualMultiplier = 13;
	data.numSteps = 20;
	data.type = "all";
	data.tank = Tank.get({gameId: data.gameId, id: data.tankId}, function(){
		processTank();
	});

	function processTank() {
		// Add gameId
		data.tank.gameId = data.gameId;

		// Organize fields
		data.fields = {};
		data.fields.rejectors = [];
		data.fields.tangentials = [];

		for (var i = data.tank.potentialFields.length - 1; i >= 0; i--) {
			var field = data.tank.potentialFields[i];
			switch (field.type) {
				case 'rejector':
					data.fields.rejectors.push(field);
					break;
				case 'tangential':
					data.fields.tangentials.push(field);
					break;
			}
		};
	}

	$scope.data = data;

	// Functions
	$scope.updateTank = function() {
		data.tank.$update();
	}
}])