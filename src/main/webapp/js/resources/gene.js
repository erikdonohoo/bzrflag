angular.module("BZRFlag").factory("Gene", ["$resource", "Backend", function($resource, Backend){
	
	return $resource(Backend.getAPIPrefix() + '/pfgenes/:id',
	{
		id: '@gene'
	})
}]);