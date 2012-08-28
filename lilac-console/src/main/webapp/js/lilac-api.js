/**
 * Lilac APIs
 */

///////////////////////////////////////////////////////////
// ServiceInfo API
lilac.api = {
	version: function() {
		return $.ajax('/api', {
			dataType: 'json',
			type: 'GET'
		});
	}
};

///////////////////////////////////////////////////////////
// Book API
lilac.api.book = {
	get: function(id) {
		return $.ajax('/api/book/' + id, {
			dataType: 'json',
		});
	},

	list: function(params, page) {
		return $.ajax('/api/book/list/' + page, {
			data: params,
			dataType: 'json',
		});
	}
};

///////////////////////////////////////////////////////////
// Author API
lilac.api.author = {
	get: function(id) {
		return $.ajax('/api/author/' + id, {
			dataType: 'json'
		});
	}
};

///////////////////////////////////////////////////////////
// Label API
lilac.api.label = {
	list: function() {
		return $.ajax('/api/label', {
			dataType: 'json'
		});
	}
};

///////////////////////////////////////////////////////////
// Account API
lilac.api.account = {
	get: function(username) {
		return $.ajax('/api/account/' + username, {
			cache: false,
			dataType: 'json',
			type: 'GET'
		});
	},

	setCredential: function(username, credential) {
		return $.ajax('/api/account/' + username + '/credential', {
			contentType: 'application/json',
			dataType: 'json',
			data: JSON.stringify(credential),
			processData: false,
			type: 'POST'
		});
	}
};

///////////////////////////////////////////////////////////
// Session API
lilac.api.session = {
	login: function(userId, password) {
		return $.ajax('/api/session/login', {
			cache: false,
			data: {
				userId: userId,
				password: password
			},
			dataType: 'json',
			type: 'POST'
		});
	},

	get: function() {
		return $.ajax('/api/session', {
			cache: false,
			dataType: 'json',
			type: 'GET'
		});
	},

	logout: function() {
		return $.ajax('/api/session/logout', {
			cache: false,
			dataType: 'json',
			type: 'GET'
		});
	}
};

///////////////////////////////////////////////////////////
// Import API
lilac.api.importData = {
	list: function() {
		return $.ajax('/api/import', {
			cache: false,
			dataType: 'json',
			type: 'GET'
		});
	},

	importData: function(fileId) {
		return $.ajax('/api/import/' + fileId, {
			cache: false,
			dataType: 'json',
			type: 'PUT'
		});
	},

	cancel: function(fileId) {
		return $.ajax('/api/import/' + fileId, {
			cache: false,
			dataType: 'json',
			type: 'DELETE'
		});
	}
};