etetestServerApp.controller('ServerController', function($http, $scope, $log, $q) {
    var self = this;
    $scope.message = {};
    $scope.showMessage = false;
    $scope.servers = [];
    $scope.selected = new Set();
    
    $scope.initList = function() {
        $http.get(url + contextRoot + '/get-servers').then(function(responseData) {
            $scope.servers = responseData.data;
            $scope.selected.clear();
        }, function(createStatus) {
        });
    };

   function handleAddServerResponse(createStatus) {
        if (createStatus.data.status == 'Success') {
            $scope.message.type = 'info';
        } else {
            $scope.message.type = 'error';
            $q.reject(createStatus);
        }
        $scope.message.text = createStatus.data.text;
        $scope.showMessage = true;
        $scope.selected.clear();
    }

    $scope.addServer = function() {
        $scope.showMessage = false;
        serverConfig = {
            "alias": $scope.alias,
            "portNumber": $scope.portNumber,
            "remotePortNumber": $scope.remotePortNumber,
            "remoteHost": $scope.remoteHost
        };
        $http.post(url + contextRoot + '/add-server', serverConfig).then(function(createStatus) {
            handleAddServerResponse(createStatus);
        }, function(createStatus) {
            handleAddServerResponse(createStatus);
        });
        $scope.initList();
    }
    
    $scope.deleteServers = function() {
        var monitors = Array.from($scope.selected);
        $http.put(url + contextRoot + '/delete-servers', monitors).then(function() {
            $scope.initList();
        }, function() {
        });
    }

    $scope.serversEmpty = function() {
        return $scope.servers.length == 0;
    }
    
    $scope.updateSelection = function(server) {
        if (server.selected) {
            $scope.selected.add(server);
        } else {
            $scope.selected.delete(server);
        }
    }

    $scope.selectedServersEmpty = function() {
        return $scope.selected.size == 0;
    }
    
    $scope.selectedServersSingle = function() {
        return $scope.selected.size == 1;
    }
    
    $scope.clearDatabase = function() {
        $http.put(url + contextRoot + '/clear-data').then(function() {
            $scope.selected.clear();
        }, function() {
        });
        $scope.initList();
    }
});