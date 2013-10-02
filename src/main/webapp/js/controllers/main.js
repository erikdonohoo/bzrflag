angular.module("BZRFlag").controller("MainCtrl", 
	["$scope", "Game", "$timeout", "$location", 
	function($scope, Game, $timeout, $location){

	// Models
	var data = {};

	function refreshGames() {
		data.loading = true;
		data.games = Game.query(function(){
			data.loading = false;
		});
	}

	data.loading = false;

	refreshGames();

	$scope.data = data;

	// Functions
	$scope.createGame = function() {
		Game.save($scope.data.newgame, function(){
			data.loading = true;
			$timeout(refreshGames, 1000);
		});
	}
	$scope.viewGame = function(game) {
		$location.path('games/' + game.id);
	}
	$scope.deleteGame = function(game) {
		Game.delete({id: game.id}, function(){
			refreshGames();
		});
	}
}])