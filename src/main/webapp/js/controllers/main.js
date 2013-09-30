angular.module("BZRFlag").controller("MainCtrl", 
	["$scope", "Game", "$timeout", 
	function($scope, Game, $timeout){

	// Models
	var data = {};

	function refreshGames() {
		data.games = Game.query();
	}

	refreshGames();

	$scope.data = data;

	// Functions
	$scope.createGame = function() {
		Game.save($scope.data.newgame, function(){
			refreshGames();
		});
	}

	// Refresh games frequently
	$timeout(refreshGames, 10000);
}])