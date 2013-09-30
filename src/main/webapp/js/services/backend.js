angular.module("BZRFlag").factory("Backend", [function(){

	var prefix = "http://localhost:8080/bzrflag/api";

	return {

		getAPIPrefix: function() {
			return prefix;
		}
	}
}]);