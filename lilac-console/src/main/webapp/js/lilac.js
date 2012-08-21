
///////////////////////////////////////////////////////////
// Action
function Action(path, pageClass, secure) {
	this.path = path;
	this.pageClass = pageClass;
	this.secure = secure;
}
Action.prototype = {
	execute: function(path, options) {
		var id = path.path;
		if(id.charAt(0) == '#') {
			id = id.substring(1);
		}
		var page = lilac.cache.get(id);
		if(page == undefined) {
			var pageClass = this.pageClass;
			page = new pageClass(id);
			var deferred = $.Deferred();
			page.initialize().done(function(){
				page.prepare(path, options).done(function(p){
					deferred.resolve(p);
				})
				.fail(function(){
					lilac.showErrorMsg('ページの表示に失敗しました');
				});
			})
			.fail(function(){
				lilac.showErrorMsg('ページの初期化に失敗しました');
			});
			lilac.cache.add(page);
			return deferred.promise();
		}
		else {
			return page.prepare(path, options);
		}
	}
};

///////////////////////////////////////////////////////////
// Page
function Page(self, id, template) {
	self.id = id;
	self.templateURL = template;
	self.page = null;
};
Page.prototype = {};
Page.prototype.initialize = function () {
	var deferred = $.Deferred();
	if(this.page) {
		deferred.resolve();
	}
	else {
		lilac.templates.get(this.templateURL)
			.done($.proxy(function(templates){
				var page = $('#page-template', templates);
				if(page.length) {
					this.page = page.clone().attr('id', this.id).appendTo($.mobile.pageContainer);
					this.customizePage(this.page);
					this.page.page();
					deferred.resolve();
				}
				else {
					deferred.reject();
				}
			}, this))
			.fail(function(){
				deferred.reject();
			});
	}
	return deferred.promise();	
};
Page.prototype.customizePage = $.noop;
Page.prototype.prepare = function (path, options) {
	return $.Deferred().resolve(this.page).promise();
};
Page.prototype.remove= function() {
	this.finalize();
	this.page.remove();
	this.page = null;
};
Page.prototype.finalize = $.noop;
Page.prototype.prefixedId = function(attrName) {
	return '#' + this.id + "-" + attrName;		
};



///////////////////////////////////////////////////////////
// Controller
var lilac = {
	/**
	 * アクションリスト
	 */
	actions: [],

	/**
	 * セッション
	 */
	session: null,


	/**
	 * jQM初期化イベントハンドラ
	 *  - pagebeforechangeハンドラの登録
	 *  - #initが表示されたら(pageshow)、#mainに遷移する処理を登録
	 */
	mobileinitHandler: function() {
		$(document).bind("pagebeforechange", lilac.pagebeforechangeHandler);
		$('#init').bind("pageshow", function(event, data) {
			var url = $.mobile.path.parseUrl(location.href);
			$.mobile.changePage(url.hash ? url.hash : '#main');
		});
	},

	/**
	 * jQMページ遷移イベントハンドラ
	 *  - 遷移先ページが文字列以外の場合、処理しない
	 *  - ハッシュが登録アクションパスにマッチする場合、当該アクションを実行
	 *    1. ページロードダイアログ表示
	 *    2. アクション実行
	 *      - 正常終了したら、ページロードダイアログを消し、アクション実行結果に遷移
	 *      - 失敗したら、ページロードダイアログを消し、エラーダイアログを表示
	 *    3. イベントの無効化
	 * @param event pagebeforechangeイベントオブジェクト
	 * @param options jQMイベントデータ
	 */
	pagebeforechangeHandler: function(event, data) {
		if(typeof data.toPage != 'string') {
			return;
		}

		if(!lilac.session) {
			event.preventDefault();
			lilac.initialize(data);
			return;
		}

		var url = $.mobile.path.parseUrl(data.toPage);
		for(var i = 0; i < lilac.actions.length; ++i) {
			var action = lilac.actions[i];
			var m = XRegExp('(?<path>' + action.path + ')(?:\\?(?<params>.*))?').exec(url.hash);
			if(m) {
				if(action.secure && !lilac.session.id) {
					var loginData = {
						transition: 'pop',
						changeHash: false,
						originalToPage: data.toPage,
						originalOptions: data.options,
						previousPage: $.mobile.activePage
					};
					$.mobile.changePage('#login', loginData);
				}
				else {
					$.mobile.loading('show');
					action.execute(m, data.options).done(function(page){
						page.jqmData("url", url.hash);
						$.mobile.loading('hide');
						$.mobile.changePage(page, data.options);
					}).fail(function(message){
						$.mobile.loading('hide');
						lilac.showErrorMsg(message, data.options);
					});
				}
				event.preventDefault();
				break;
			}
		}
	},

	initialize: function(eventData) {
		lilac.api.session.get()
			.done(function(session) {
				lilac.session = session;
				$.mobile.changePage(eventData.toPage, eventData.options);
			});
	},

	/**
	 * エラーメッセージを表示します。
	 * @param message　エラーメッセージ。デフォルトメッセージを表示する場合はnull
	 * @param options jqmページ遷移オプション
	 */
	showErrorMsg: function(message) {
		message = message || $.mobile.pageLoadErrorMessage; 
		$.mobile.loading('show', {
			theme: $.mobile.pageLoadErrorMessageTheme,
			text: message,
			textVisible: true,
			textonly: true});
		setTimeout(function() {
			$.mobile.loading('hide');
		}, 1500 );
	},

	extend: function(base, sub) {
		function f(){};
		f.prototype = base.prototype;
		sub.prototype = new f();
		sub.prototype.__super__ = base.prototype;
		sub.prototype.__super__.constructor = base;
		sub.prototype.constructor = sub;
		return sub;
	}
};


///////////////////////////////////////////////////////////
// ページキャッシュ
function PageCache(capacity) {
	this.capacity = capacity;
	this.cache = new Array(this.capacity);
	this.index = 0;
};
PageCache.prototype = {
	add: function(page) {
		var old = this.cache[this.index];
		if(old != undefined) {
			if(old.page == $.mobile.activePage) {
				this.index++;
				if(this.index == this.capacity) {
					this.index = 0;
				}
			}
			else {
				old.remove();
			}
		}
		this.cache[this.index] = page;
		this.index++;
		if(this.index == this.capacity) {
			this.index = 0;
		}
	},
	
	get: function(id) {
		for(var i = 0; i < this.capacity; ++i) {
			var page = this.cache[i];
			if(page && page.id == id) {
				return page;
			}
		}
	}
};
lilac.cache = new PageCache(10);

///////////////////////////////////////////////////////////
// Templates
function TemplateManager(){
	this.templates = {};
};
TemplateManager.prototype = {
	get: function(templateUrl) {
		var deferred = $.Deferred();
		if(templateUrl in this.templates) {
			deferred.resolve(this.templates[templateUrl]);
		}
		else {
			var templateHolder = $('<div>');
			templateHolder.load(templateUrl + " #templates > div", $.proxy(function(contents, textStatus){
				if(textStatus == 'success' || textStatus == 'notmodified') {
					this.templates[templateUrl] = templateHolder;
					deferred.resolve(templateHolder);
				}
				else {
					deferred.reject();
				}
			}, this));
		}
		return deferred.promise();
	}
};
lilac.templates = new TemplateManager();


///////////////////////////////////////////////////////////
// 初期フック
(function() {
	$(document).bind('mobileinit', lilac.mobileinitHandler);
})();