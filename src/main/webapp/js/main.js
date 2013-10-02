var app = angular.module("BZRFlag", ["ngResource", "ngRoute"]);

app.config(function($routeProvider){
	$routeProvider
		.when('/', {templateUrl: 'partials/home.html', controller: 'MainCtrl'})
		.when('/games/:id', {templateUrl: 'partials/game.html', controller: 'GameCtrl'})
		.when('/games/:gameid/teams/me/tanks/:tankid', {templateUrl: 'partials/tank.html', controller: 'TankCtrl'})
		.otherwise({redirectTo: '/'});
})

app.run(function($rootScope){
	
})