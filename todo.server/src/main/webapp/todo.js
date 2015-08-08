var todoApp = angular.module('todoApp', []);

todoApp.service('dataService', function($http) {
	this.connect = function(onMessageCallback) {
		var host = window.location.host;
		var path = window.location.pathname;
		var webCtx = path.substring(0, path.indexOf('/', 1));
		var endPointURL = "ws://" + window.location.host + webCtx + "/chat";
		this.chatClient = new WebSocket(endPointURL);
		this.chatClient.onmessage = function(event) {
			onMessageCallback(event.data);
		};
	}

	delete $http.defaults.headers.common['X-Requested-With'];
	this.getData = function(uuid, callbackFunc) {
		$http({
			method : 'GET',
			url : 'notes/' + uuid,
		// params : 'limit=10, sort_by=created:desc',
		// headers : {
		// 'cache-control' : 'private, max-age=0, no-cache'
		// 'Authorization' : 'Token token=xxxxYYYYZzzz'
		// }
		}).success(function(data, status, headers, config) {
			// With the data successfully returned, call our callback
			callbackFunc(data, headers()['notes-id']);
		}).error(function() {
			alert("get error");
		});
	}

	this.postData = function(uuid, data) {
		$http({
			method : 'POST',
			url : 'notes/' + uuid,
			data : data,
		// params : 'limit=10, sort_by=created:desc',
		// headers : {
		// 'Authorization' : 'Token token=xxxxYYYYZzzz'
		// }
		}).success(function(data) {
			// With the data successfully returned, call our callback
			// callbackFunc(data);
		}).error(function() {
			alert("post error");
		});
	}
});

todoApp.controller('TodoListController', function($scope, dataService) {
	var todoList = this;
	dataService.getData(todoList.uuid, function(data, uuid) {
		todoList.todos = data;
		todoList.uuid = uuid;
	});
	dataService.connect(function(json) {
		var incoming = JSON.parse(json);
		dataService.getData(todoList.uuid, function(data, uuid) {
			todoList.todos = data;
			todoList.uuid = uuid;
		});
		$scope.$apply();
	});

	todoList.addTodo = function() {
		todoList.todos.push({
			title : todoList.todoText,
			done : false
		});
		todoList.todoText = '';
		dataService.postData(todoList.uuid, todoList.todos);
	};

	todoList.remaining = function() {
		var count = 0;
		angular.forEach(todoList.todos, function(todo) {
			count += todo.done ? 0 : 1;
		});
		return count;
	};

	todoList.archive = function() {
		var oldTodos = todoList.todos;
		todoList.todos = [];
		angular.forEach(oldTodos, function(todo) {
			if (!todo.done)
				todoList.todos.push(todo);
		});
	};
});