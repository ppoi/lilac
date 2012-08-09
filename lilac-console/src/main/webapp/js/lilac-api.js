/**
 * Lilac APIs
 */
lilac.api = {};


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

	validate: function(sessionId) {
		return $.ajax('/api/session/validate', {
			cache: false,
			data: {
				sessionId: sessionId
			},
			dataType: 'json',
			type: 'POST'
		});
	}
};