angular.module("BZRFlag").directive("stopEvent",
	function() {
		return function(scope, elem, attrs) {
			elem.bind(attrs.stopEvent, function(e){
				e.stopPropagation();
			})
		}
});