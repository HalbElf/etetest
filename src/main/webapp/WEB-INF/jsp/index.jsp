<%@ page import="org.db1.etetest.*" %>
<!DOCTYPE HTML>
<html>
<head>
<meta content="text/html;charset=utf-8" http-equiv="Content-Type">
<meta content="utf-8" http-equiv="encoding">
<title>End-to-end Test server</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/bootstrap.css" rel="stylesheet" type="text/css">
<link href="css/app.css" rel="stylesheet" type="text/css">
</head>

<body ng-app="EtetestApplication">
	<script type="text/javascript">
		var url = <%="\"" + Support.getProperty("hostname") + ":" + Support.getProperty("server.port") + "\";"%>
		var contextRoot = <%="\"" + Support.getProperty("server.contextPath") + "\";"%>
		
	</script>
	<div id="content">
		<div id="main">
			<div ng-include src="'views/main-menu.html'"></div>
			<script src="js/angularjs/angular.js" type="text/javascript"></script>
			<script src="js/angularjs/angular-route.js" type="text/javascript"></script>
			<script src="js/smarttable/smart-table.js" type="text/javascript"></script>
			<script src="js/jquery/jquery-1.12.0.min.js" type="text/javascript"></script>
			<script src="js/app.js" type="text/javascript"></script>
			<script src="js/menu.js" type="text/javascript"></script>
			<script src="js/http.js" type="text/javascript"></script>
			<script src="js/server.js" type="text/javascript"></script>
		</div>
	</div>
</body>
</html>
