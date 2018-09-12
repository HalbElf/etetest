etetestServerApp.controller('MenuController', function(EVENTS, $scope, $log, $location, $route) {
	var self = this;
	self.loggedIn = false;
	$scope.buttons = [ {
		name : 'servers',
		caption : 'Servers',
		link : '/servers-list',
		isEnabled : function() {
			return true;
		}
	}, {
		name : 'log',
		caption : 'Log View',
		link : '/log-view',
		isEnabled : function() {
			return true;
		}
	}, {
		name : 'addServer',
		caption : 'Add Server',
		link : '/server-add',
		isEnabled : function() {
			return true;
		}
	}, {
		name : 'serverConfig',
		caption : 'Server Configuration',
		link : '/server-config',
		isEnabled : function() {
			return true;
		}
	},];
	
	(self.init = function() {
	})();
	
	$scope.select = function(name) {
		$scope.selected = name;
		var selectedButton = null;
		for (var i = 0; i < $scope.buttons.length; ++i) {
			if (name == $scope.buttons[i].name) {
				selectedButton = $scope.buttons[i];
				break;
			}
		}
		$location.path(selectedButton.link);
		$route.reload();
	}
	
	$scope.isButtonEnabled = function(button) {
		return button.isEnabled();
	}
});