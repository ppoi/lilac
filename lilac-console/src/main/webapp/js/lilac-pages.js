

/**
 * 書籍一覧ページ共通
 */
BookListPageBase = lilac.extend(Page, function(self, id, template) {
	this.__super__.constructor(self, id, template);
	self.search = {
		condition: null,
		page: 0,
		showLoadingMsg: false
	};
	self.count = null;
});
BookListPageBase.prototype.customizePage = function(page) {
	var booklistHolder = $('.booklist-holder', this.page);
	if(!booklistHolder.length) {
		return;
	}

	this.booklist = $('<ul>').attr({
		'data-role': 'listview',
		'data-theme': 'd',
		'data-inset': 'true'
		}).append($('<li>').attr('data-role', 'list-divider').append(
			document.createTextNode("一覧"),
			$('<span>').addClass('ui-li-count')
		)
	).appendTo(booklistHolder);
	this.morebuttonHolder = $('<div>').addClass('morebutton-holder').appendTo(booklistHolder);
	this.morebutton = $('<a>').attr({
		'href': '#',
		'data-role': 'button',
		'data-icon': 'forward',
		'data-iconpos': 'bottom',
		'data-theme': 'd'
	}).text('さらに表示...').button();
};
BookListPageBase.prototype.listBooks = function(options) {
	var deferred = $.Deferred();

	if(options.showLoadingMsg) {
		$.mobile.showPageLoadingMsg();
	}

	lilac.api.book.list(options.condition, options.page)
		.done($.proxy(function (data, textStatus, jqXHR) {
			this.makeResultList(data, (options.page == 0));
			this.search.condition = options.condition;
			this.search.page = options.page + 1;
			if(options.showLoadingMsg) {
				$.mobile.hidePageLoadingMsg();
			}
			deferred.resolve();
		}, this))
		.fail(function (jqXHR, textStatus, errorThrown) {
			window.alert(textStatus);
			deferred.reject();
		});

	return deferred.promise();			
};
BookListPageBase.prototype.makeResultList = function(result, clear) {
	var listview = this.booklist;
	var dividers = $('li[data-role="list-divider"]', listview);
	var items = $('li[data-role!="list-divider"]', listview);
	var previous;
	if(clear) {
		items.remove();
		previous = $(dividers[0]);
		this.count = null;
	}
	else {
		previous = $(items[items.length - 1]);
	}
	var count = this.count || 0;
	$.each(result.items, function(index, entity){
		entity.index = count + 1;
		var item = $('<li>').append(
			$('<span>').addClass("li-index").text(entity.index),
			document.createTextNode(" "),
			$('<a>').attr("href", "#bib" + entity.id).append(
					$('<p>').append($('<small>').text(entity.label)),
					$('<p>').append($('<strong>').text(entity.title)),
					$('<p>').text(entity.subtitle)
			)
		);
		previous.after(item);
		previous = item;
		count++;
	});
	this.count = count;
	$('span.ui-li-count', listview).text(count + "/" + result.count);
	if(count >= result.count) {
		this.morebutton.unbind().remove();
	}
	else if(!this.morebutton[0].parentNode) {
		this.morebuttonHolder.append(this.morebutton);
		this.morebutton.bind('vclick', $.proxy(function(event) {
			event.preventDefault();
			var options = {
				condition: this.search.condition,
				page: this.search.page,
				showLoadingMsg: true
			};
			this.listBooks(options);
		}, this));
	}
	listview.listview('refresh');
};


/**
 * メインメニュー
 */
MainPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/main.html');
});

/**
 * ログインダイアログ
 */
LoginPage = lilac.extend(Page, function(id){
	this.__super__.constructor(this, id, 'template/loginform.html');
});
LoginPage.prototype.customizePage = function(page){
	//ログイン実行
	$('#login-form').bind('submit', $.proxy(function(event){
		event.preventDefault();
		var userId = $('#login-userid');
		var password = $('#login-password');
		$.mobile.showPageLoadingMsg();
		lilac.api.session.login(userId.attr('value'), password.attr('value'))
		.done(function(data, status, jqXHR){
			$.mobile.hidePageLoadingMsg();
			$.mobile.showPageLoadingMsg("a", "ログインに成功しました", true );
			setTimeout(function() {
				$.mobile.hidePageLoadingMsg();
				lilac.session = data;
				var toPage = this.next.toPage;
				var options = this.next.options;
				options.transition = 'pop';
				options.reverse = true;
				options.fromPage = this.page;
				$.mobile.changePage(toPage, options);
			}, 1500 );
		}).fail(function(jqXHR, status){
			alert(status);
			lilac.showErrorMsg('ログインに失敗しました');
		});
		return false;
	}, this));
	//キャンセル
	$('#login-cancel', page).click(function(event){
		if(self.previousPage) {
			var options = {
				transition: 'pop',
				reverse: true,
				fromPage: self.page
			};
			$.mobile.changePage(self.previousPage, options);
		}
		else {
			window.history.back();
		}
	});
	//後始末
	page.bind('pagehide', function(event){
		$('#login-userid').attr('value', '');
		$('#login-password').attr('value', '');
		self.next = null;
		self.previousPage = null;
	});
};
LoginPage.prototype.prepare = function(path, options) {
	this.next = {
		toPage: options.originalToPage,
		options: options.originalOptions
	};
	this.previousPage = options.previousPage;
	return $.Deferred().resolve(this.page).promise();
};

/**
 * 管理メニュー
 */
AdminPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/admin.html');
});

/**
 * エクスポート
 */
ExportPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/export.html');
});
ExportPage.prototype.customizePage = function(page) {
	$('#export-all').change(function(event){
		if($('#export-all:checked').length) {
			$('#export-targets input:checkbox').checkboxradio('disable');
			$('#export-data').button('enable');
		}
		else {
			$('#export-targets input:checkbox').checkboxradio('enable');
			if($('#export-targets input:checkbox:checked').length) {
				$('#export-data').button('enable');
			}
			else {
				$('#export-data').button('disable');
			}
		}
	});
	$('#export-targets input:checkbox').change(function(event){
		if($('#export-targets input:checkbox:checked').length) {
			$('#export-data').button('enable');
		}
		else {
			$('#export-data').button('disable');
		}
	});
	$('#export-form').submit(function(event){
		var exportTargets = new Array();
		if($('#export-all:checked').length) {
			exportTargets[0] = "all";
		}
		else {
			$('#export-targets input:checkbox:checked').each(function(index, item){
				exportTargets[index] = $(item).val();
			});
		}
		if(exportTargets.length) {
			location.href = "/api/export/" + exportTargets.join('+');
		}
		return false;
	});
};
ExportPage.prototype.prepare = function(path, options) {
	if($('#export-all:checked').length || $('#export-targets input:checkbox:checked').length) {
		$('#export-data').button('enable');
	}
	else {
		$('#export-data').button('disable');
	}
	return new $.Deferred().resolve(this.page).promise();
};

/**
 * インポート
 */
ImportPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/import.html');
});
ImportPage.prototype.customizePage = function(page) {
	$("#execute-upload").click($.proxy(function(event) {
		event.preventDefault();
		$('#upload-file').upload("/api/import", $.proxy(function(data){
			lilac.api.import.list()
				.done($.proxy(function(data, textStatus, jqXHR) {
					this.refleshFileList(data);
				}, this))
				.fail(function (jqXHR, textStatus, errorThrown) {
					window.alert("インポートファイルの取得に失敗: " + textStatus);
				});
		}, this), "html");
		return false;
	}, this));
	$('#import-form').submit($.proxy(function(event) {
		var selectedItem = $('#import-files input:radio:checked');
		if(selectedItem.length) {
			var fileId= selectedItem.val();
			lilac.api.import.importData(fileId);
			lilac.api.import.list()
				.done($.proxy(function(data, textStatus, jqXHR) {
					this.refleshFileList(data);
				}, this))
				.fail(function (jqXHR, textStatus, errorThrown) {
					window.alert("インポートファイルの取得に失敗: " + textStatus);
				});
		}
		return false;
	}, this));
};
ImportPage.prototype.prepare = function(path, option) {
	var deferred = new $.Deferred();
	lilac.api.import.list()
		.done($.proxy(function(data, textStatus, jqXHR) {
			this.refleshFileList(data);
			deferred.resolve(this.page);
		}, this))
		.fail(function (jqXHR, textStatus, errorThrown) {
			window.alert("インポートファイルの取得に失敗: " + textStatus);
			deferred.reject();
		});
	return deferred.promise();
};
ImportPage.prototype.refleshFileList = function(data) {
	var container = $('#import-files');
	$('input:radio,label', container).remove();
	$.each(data, function(index, item) {
		container.append($('<input type="radio" />').attr({
			name: "fileId",
			"id": 'importfile-' + item.id,
			value: item.id
		}));
		container.append($('<label>').attr('for', "importfile-" + item.id)
				.text("[" + item.id + "] " + item.fileName + " (" + item.createdTimestamp + ")"));
	});
	this.page.trigger("create");
};

/**
 * 書籍検索
 */
BookSearchPage = lilac.extend(BookListPageBase, function(id){
	this.__super__.constructor(this, id, 'template/books.html');
});
BookSearchPage.prototype.customizePage = function(page) {
	this.__super__.customizePage.apply(this, arguments);
	$('form#booksearchform', page).submit($.proxy(function(event) {
		event.preventDefault();
		$('#booksearch').click();
		var searchOptions = {
			condition: $('#booksearchform').serialize(),
			page: 0,
			showLoadingMsg: true
		};
		this.listBooks(searchOptions);
		return false;
	}, this));
};
BookSearchPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	if(this.count != null) {
		deferred.resolve(this.page);
	}
	else {
		var searchOptions = {
			condition: undefined,
			page: 0,
			shoLoadingMsg: false
		};
		$.when(this.updateLabelOptions(), this.listBooks(searchOptions))
			.done($.proxy(function() {
				deferred.resolve(this.page);
			}, this))
			.fail(function() {
				deferred.reject();
			});
	}
	return deferred.promise();
};
BookSearchPage.prototype.updateLabelOptions = function() {
	var deferred = $.Deferred();
	lilac.api.label.list()
		.done($.proxy(function (data, textStatus, jqXHR) {
			var select = $('#labeloptions', this.page);
			if(select.length > 0) {
				$('option:first ~ option', select).remove();
				$.each(data, function(index, entity){
					select.append($('<option>').attr("value", entity.name).text(entity.name));
				});
				select.selectmenu('refresh');
			}
			deferred.resolve();
		}, this))
		.fail(function (jqXHR, textStatus, errorThrown) {
			window.alert("レーベルリストの取得に失敗: " + textStatus);
			deferred.reject();
		});
	return deferred.promise();
};

BibliographyPage = lilac.extend(Page, function(id){
	this.__super__.constructor(this, id, 'template/bibliography.html');
	this.rendered = false;
});
BibliographyPage.prototype.customizePage = function(page) {
	var placeholders = $('[id|="bibliography"]', page);
	placeholders.each($.proxy(function(index, element){
		var placeholder = $(element);
		var originalId = placeholder.attr('id');
		placeholder.attr('id', this.id + originalId.substring(12));
	}, this));
};
BibliographyPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	if(this.rendered) {
		deferred.resolve(this.page);
	}
	else {
		lilac.api.book.get(path.bid)
			.done($.proxy(function(data) {
				this.setBookProperty(data, "label", " ");
				this.setBookProperty(data, "title");
				this.setBookProperty(data, "subtitle", " ");
				this.setBookProperty(data, "isbn");
				this.setBookProperty(data, "publicationDate");
				this.setBookProperty(data, "plice");
	
				var authorList = $(this.prefixedId('authors'), this.page);
				$.each(data.authors, function(index, entity){
					var item = $('<li>').attr('id', this.id + '-author-' + entity.id).append(
						$('<a>').attr('href', '#author' + entity.id)
							.text(entity.name + '(' + entity.role + ')')
					);
					authorList.append(item);
				});
				authorList.listview('refresh');
				this.rendered = true;
				deferred.resolve(this.page);
			}, this))
			.fail(function (jqXHR, textStatus, errorThrown) {
				window.alert("書籍情報の取得に失敗:" + textStatus);
				deferred.reject();
			});
	}
	return deferred.promise();
};
BibliographyPage.prototype.setBookProperty = function(entity, key, defaultValue){
	var value = entity[key];
	if(value == null) {
		value = defaultValue || '-';
	}
	$(this.prefixedId(key)).text(value);
};

AuthorPage = lilac.extend(BookListPageBase, function(id){
	this.__super__.constructor(this, id, 'template/author.html');
	this.rendered = false;
});
AuthorPage.prototype.customizePage = function(page) {
	this.__super__.customizePage.apply(this, arguments);
	var placeholders = $('[id|="author"]', page);
	placeholders.each($.proxy(function(index, element){
		var placeholder = $(element);
		var originalId = placeholder.attr('id');
		placeholder.attr('id', this.id + originalId.substring(6));
	}, this));
};
AuthorPage.prototype.prepare = function(path, options){
	var deferred = $.Deferred();
	if(this.rendered) {
		deferred.resolve(this.page);
	}
	else {
		var searchOptions = {
			condition: 'authorId=' + path.aid,
			page: 0,
			showLoadingMsg: false
		};
		$.when(this.getAuthor(path.aid), this.listBooks(searchOptions))
			.done($.proxy(function(){
				this.rendered = true;
				deferred.resolve(this.page);
			}, this))
			.fail(function(){
				deferred.reject();
			});
	}
	return deferred.promise();
};
AuthorPage.prototype.getAuthor = function(aid) {
	var deferred = $.Deferred();
	lilac.api.author.get(aid)
		.done($.proxy(function(data, textStatus) {
			this.setAuthorProperty(data, "name");
			this.setAuthorProperty(data, "website");
			this.setAuthorProperty(data, "twitter");
	
			var synonymList = $(this.prefixedId('synonymlist'), this.page);
			$.each(data.synonym, function(index, entity){
				var item = $('<li>').attr('id', 'author-' + entity.id).append(
						$('<a>').attr('href', '#author' + entity.id).text(entity.name)
					);
				synonymList.append(item);
			});
			synonymList.listview('refresh');
			deferred.resolve();
		}, this))
		.fail(function(jqXHR, textStatus){
			window.alert("著者情報の取得に失敗しました。:" + textStatus);
			deferred.reject();
		});
	return deferred.promise();
};
AuthorPage.prototype.setAuthorProperty = function(entity, key, defaultValue) {
	var value = entity[key];
	if(value == null) {
		value = defaultValue || '-';
	}
	$(this.prefixedId(key)).text(value);
};


// Action定義
lilac.actions = [
	new Action('#main', MainPage),
	new Action('#list', BookSearchPage),
	new Action('#bib(?<bid>\\d+)', BibliographyPage),
	new Action('#author(?<aid>\\d+)', AuthorPage),
	new Action('#login', LoginPage),
	new Action('#admin', AdminPage, true),
	new Action('#export', ExportPage, true),
	new Action('#import', ImportPage, true)
];
