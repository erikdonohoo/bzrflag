angular.module("BZRFlag").controller("MainCtrl", ["$scope", "Game", function($scope, Game){

	var data = {};

	data.games = Game.query();

	$scope.data = data;
}])