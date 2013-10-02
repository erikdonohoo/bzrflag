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
	data.tank = Tank.get({gameId: data.gameId, id: data.tankId});
	$scope.data = data;
}])