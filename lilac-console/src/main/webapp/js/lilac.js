
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
		lilac.loadTemplate(this.templateURL)
			.done($.proxy(function(templates){
				var page = $('#page-template', templates);
				if(page.length) {
					this.page = page.attr('id', this.id).appendTo($.mobile.pageContainer);
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
Page.prototype.setEntityProperty = function(entity, key, defaultValue) {
	var value = entity[key];
	if(value == null) {
		value = defaultValue || '-';
	}
	$(this.prefixedId(key)).text(value);
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
	 * バージョン
	 */
	version: null,

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
		lilac.version = $('meta[name="console-version"]').attr('content');
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

	/**
	 * りら初期化処理
	 * @param eventData jQM初期化イベント
	 */
	initialize: function(eventData) {
		lilac.api.session.get()
			.done(function(session) {
				lilac.session = session;
				$.mobile.changePage(eventData.toPage, eventData.options);
			});
	},

	/**
	 * テンプレートをロードします
	 */
	loadTemplate: function(templateUrl) {
		var deferred = $.Deferred();
		var templateHolder = $('<div>');
		templateHolder.load(templateUrl + " #templates > div", function(contents, textStatus){
			if(textStatus == 'success' || textStatus == 'notmodified') {
				deferred.resolve(templateHolder);
			}
			else {
				deferred.reject();
			}
		});
		return deferred.promise();
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
/**
 * ページキャッシュのインスタンスを作成します。
 * @param capacity キャッシュのキャパシティ(最大ページ数)
 */
function PageCache(capacity) {
	this.capacity = capacity;
	this.cache = [];
};
/**
 * キャッシュにページを追加します。
 * @param page 追加するPageオブジェクト
 */
PageCache.prototype.add = function(page) {
	if(this.cache.length >= this.capacity) {
		//一番アクセスタイムスタンプが古いページ(キャッシュリストの最後尾)を削除
		this.cache.pop().remove();
	}
	//キャッシュリストの先頭に追加
	page.timestamp = new Date().getTime();
	this.cache.unshift(page);
};
/**
 * キャッシュからidで識別されるページを取得します。
 * @param id Page ID
 * @returns idで識別されるキャッシュされたPageオブジェクト。存在しない場合 null
 */
PageCache.prototype.get = function(id) {
	for(var i = 0; i < this.cache.length; ++i) {
		var page = this.cache[i];
		if(page.id == id) {
			//ページのアクセスタイムスタンプを更新し、キャッシュリストをタイムスタンプの逆順でソート
			page.timestamp = new Date().getTime();
			this.cache.sort(function(a, b) {
				return b.timestamp - a.timestamp;
			});
			return page;
		}
	}
	return null;
};
lilac.cache = new PageCache(10);


///////////////////////////////////////////////////////////
// 初期フック
(function() {
	$(document).bind('mobileinit', lilac.mobileinitHandler);
})();