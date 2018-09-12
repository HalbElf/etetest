'use strict';

var etetestServerApp = angular.module('EtetestApplication', [ 'ngRoute', 'smart-table' ]);

etetestServerApp.constant("EVENTS", {
	errorEvent : 'error'
});

etetestServerApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/server-add', {
		templateUrl : 'views/server-add.html',
	}).when('/servers-list', {
        templateUrl : 'views/servers-list.html',
    }).otherwise({
		redirectTo : '/'
	});

}]).config(['$httpProvider', function($httpProvider) {
	var interceptor = [
	    '$q',
	    '$rootScope',
	    'EVENTS',
	    function($q, $rootScope, EVENTS) {
	      var service = {
	         'responseError': function(rejection) {
	              $rootScope.$broadcast(EVENTS.errorEvent, rejection);
	              return $q.reject(rejection);
	          }
	      };
	      return service;
	    }
	  ];
	  $httpProvider.interceptors.push(interceptor);
	}
]).directive('messageRow', function($compile) {
	return {
		restrict : 'E',
		replace : 'true',
		templateUrl : 'views/message-row-fragment.html',
		link : function(scope, elem, attrs) {
		}
	};
});