var todoApp = angular.module('todoApp', []);

todoApp.service('dataService', function($http) {
	delete $http.defaults.headers.common['X-Requested-With'];
	this.getData = function(callbackFunc) {
		$http({
			method : 'GET',
			url : 'notes/blah',
		// params : 'limit=10, sort_by=created:desc',
		// headers : {
		// 'Authorization' : 'Token token=xxxxYYYYZzzz'
		// }
		}).success(function(data) {
			// With the data successfully returned, call our callback
			callbackFunc(data);
		}).error(function() {
			alert("error");
		});
	}
});

todoApp.controller('TodoListController', function(dataService) {
	var todoList = this;
	dataService.getData(function(data) {
		todoList.todos = data;
	});

	todoList.addTodo = function() {
		todoList.todos.push({
			text : todoList.todoText,
			done : false
		});
		todoList.todoText = '';
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