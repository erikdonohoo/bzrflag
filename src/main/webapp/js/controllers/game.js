angular.module("BZRFlag").controller("GameCtrl", 
	["$scope", "Game", "$timeout", "$routeParams",
	function($scope, Game, $timeout, $routeParams){

	// Models
	var data = {};

	$scope.data = data;

	// Functions
	$scope.refreshGame = function() {
		data.game = Game.get({id: $routeParams.id});
	}

	// Get game
	data.game = Game.get({id: $routeParams.id});
}])