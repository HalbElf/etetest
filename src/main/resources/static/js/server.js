etetestServerApp.controller('ServerController', function($http, $scope, $log, $q) {
    var self = this;
    $scope.message = {};
    $scope.showMessage = false;
    $scope.servers = [];
    selected = new Set();
    
    $scope.initList = function() {
        $http.get(url + contextRoot + '/get-servers').then(function(responseData) {
            $scope.servers = responseData.data;
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
        var monitors = Array.from(selected);
        $http.put(url + contextRoot + '/delete-servers', monitors).then(function() {
        }, function() {
        });
        $scope.initList();
    }

    $scope.serversEmpty = function() {
        return $scope.servers.length == 0;
    }
    
    $scope.updateSelection = function(server) {
        if (server.selected) {
            selected.add(server);
        } else {
            selected.delete(server);
        }
    }

    $scope.selectedServersEmpty = function() {
        return selected.size == 0;
    }
    
    $scope.selectedServersSingle = function() {
        return selected.size == 1;
    }
    
    $scope.clearDatabase = function() {
        $http.put(url + contextRoot + '/clear-data').then(function() {
        }, function() {
        });
        $scope.initList();
    }
});