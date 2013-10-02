angular.module("BZRFlag").factory("Tank", ["$resource", "Backend", function($resource, Backend){
	
	return $resource(Backend.getAPIPrefix() + '/games/:gameId/teams/me/tanks/:id',
	{
		id: '@id',
		gameId: '@gameId'
	})
}]);