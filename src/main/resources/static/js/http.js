etetestServerApp.factory('HttpService', function($http, $log) {
	var HttpService = {
		get : function(url, config) {
			var promise = $http.get(url, config).then(function(response) {
				$log.info("get response: " + response);
				return response.data;
			});
			return promise;
		},
		post : function(url, data, config) {
			var promise = $http.post(url, data, config).then(
					function(response) {
						$log.info("post response: " + response);
						return response.data;
					});
			return promise;
		},
		put : function(url, data, config) {
			var promise = $http.put(url, data, config).then(function(response) {
				$log.info("put response: " + response);
				return response.data;
			});
			return promise;
		}
	};
	return HttpService;
});

