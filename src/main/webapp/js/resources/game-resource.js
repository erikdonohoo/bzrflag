angular.module("BZRFlag").factory("Game", ["$resource", "Backend", function($resource, Backend){
	
	return $resource(Backend.getAPIPrefix() + '/games/:id',
	{
		id: '@id'
	})
}]);