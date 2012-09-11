
/**
 * 書籍一覧ページ共通
 */
BookListPageBase = lilac.extend(Page, function(self, id, template) {
	this.__super__.constructor(self, id, template);
	self.search = {
		condition: null,
		page: -1,
		showLoadingMsg: false
	};
	self.count = 0;
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
		}).appendTo(booklistHolder);
	this.nextMoreButtonHolder = $('<div>').addClass('morebutton-holder').appendTo(booklistHolder);
};
BookListPageBase.prototype.listBooks = function(options) {
	var deferred = $.Deferred();

	if(options.showLoadingMsg) {
		$.mobile.loading('show');
	}

	lilac.api.booksearch.list(options.condition, options.page)
		.done($.proxy(function (data, textStatus, jqXHR) {
			this.makeResultList(data, options);
			this.search.condition = options.condition;
			this.search.page = options.page;
			if(options.showLoadingMsg) {
				$.mobile.loading('hide');
			}
			deferred.resolve();
		}, this))
		.fail(function (jqXHR, textStatus, errorThrown) {
			window.alert(textStatus);
			deferred.reject();
		});

	return deferred.promise();			
};
BookListPageBase.prototype.makeResultList = function(result, options) {
	if(options.page == 0) {
		this.booklist.empty().append(this.createDivider());
		this.count = 0;
	}

	$.each(result.items, $.proxy(function(index, entity){
		entity.index = this.count + index + 1;
		this.booklist.append(this.createItem(index, entity));
	}, this));
	this.count += result.items.length;

	if(this.count >= result.count) {
		if(this.nextMoreButton) {
			this.nextMoreButton.unbind().remove();
			this.nextMoreButton = null;
		}
	}
	else if(!this.nextMoreButton) {
		this.nextMoreButton = this.createMoreButton().appendTo(this.nextMoreButtonHolder).button();
		this.nextMoreButton.bind('vclick', $.proxy(function(event) {
			event.preventDefault();
			var options = {
				condition: this.search.condition,
				page: this.search.page + 1,
				showLoadingMsg: true
			};
			this.listBooks(options);
		}, this));
	}

	$('span.booklist-count', this.page).text(result.count);
	$('span.booklist-remaining-count', this.page).text(result.count - this.count);
	this.booklist.listview('refresh');
};
BookListPageBase.prototype.createDivider = function() {
	return $('<li>').attr('data-role', 'list-divider').append(
		document.createTextNode("一覧"),
		$('<span>').addClass('booklist-count').addClass('ui-li-count'));
};
BookListPageBase.prototype.createItem = function(index, entity) {
	return $('<li>').append(
		$('<span>').addClass("li-index").text(entity.index),
		document.createTextNode(" "),
		$('<a>').attr("href", "#bib" + entity.id).append(
			$('<p>').append($('<small>').text(entity.label)),
			$('<p>').append($('<strong>').text(entity.title)),
			$('<p>').text(entity.subtitle))
	);
};
BookListPageBase.prototype.createMoreButton = function() {
	return $('<a>').attr({
		'href': '#',
		'data-role': 'button',
		'data-icon': 'forward',
		'data-iconpos': 'bottom',
		'data-theme': 'd'
	}).text('さらに表示... ').append($('<span class="booklist-count-button">(<span class="booklist-remaining-count"></span>件)</span>'));
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
		$.mobile.loading('show');
		lilac.api.session.login(userId.attr('value'), password.attr('value'))
			.done($.proxy(function(data, status, jqXHR){
				$.mobile.loading('hide');
				$.mobile.loading('show', {
					theme: "a",
					text: "ログインに成功しました",
					textVisible: true,
					textonly: true});
				setTimeout($.proxy(function() {
					$.mobile.loading('hide');
					lilac.session = data;
					var toPage = this.next.toPage;
					var options = this.next.options;
					options.transition = 'pop';
					options.reverse = true;
					options.fromPage = this.page;
					$.mobile.changePage(toPage, options);
				}, this), 1500 );
			}, this)).fail(function(jqXHR, status){
				lilac.showErrorMsg('ログインに失敗しました');
			});
		return false;
	}, this));
	//キャンセル
	$('#login-cancel', page).click($.proxy(function(event){
		if(this.previousPage) {
			var options = {
				transition: 'pop',
				reverse: true,
				fromPage: this.page
			};
			$.mobile.changePage(this.previousPage, options);
		}
		else {
			window.history.back();
		}
	}, this));
	//後始末
	page.bind('pagehide', $.proxy(function(event){
		$('#login-userid').attr('value', '');
		$('#login-password').attr('value', '');
		this.next = null;
		this.previousPage = null;
	}, this));
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
	this.loginButton = $('<button id="login-button" data-theme="b">').text("ログイン");
	this.logoutButton = $('<button id="logout-button" data-theme="a">').text("ログアウト");
});
AdminPage.prototype.prepare = function(path, option) {
	this.resetAuthInfo();
	return $.Deferred().resolve(this.page).promise();
};
AdminPage.prototype.resetAuthInfo = function() {
	if(lilac.session.id) {
		$('#admin-current-username').text(lilac.session.username);
		this.loginButton.unbind();
		$('#authbuttonfolder').empty().append(this.logoutButton);
		this.logoutButton.click($.proxy(function(event){
			event.preventDefault();
			$.mobile.loading('show');
			lilac.api.session.logout()
				.done($.proxy(function(){
					lilac.session = {};
					$.mobile.loading('hide');
					this.resetAuthInfo();
				}, this))
				.fail(function() {
					$.mobile.loading('hide');
					lilac.showErrorMsg('ERROR');
				});
			return false;
		}, this));
		this.logoutButton.button();
	}
	else {
		$('#admin-current-username').text("(未ログイン)");
		this.logoutButton.unbind();
		$('#authbuttonfolder').empty().append(this.loginButton);
		this.loginButton.click($.proxy(function(event){
			event.preventDefault();
			var loginData = {
					transition: 'pop',
					changeHash: false,
					originalToPage: '#admin',
					originalOptions: {
						allowSamePageTransition: true
					},
					previousPage: $.mobile.activePage
				};
			$.mobile.changePage('#login', loginData);
			return false;
		}, this));
		this.loginButton.button();
	}
};

/**
 * アカウント情報ページ
 */
AccountPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/account.html');
});
AccountPage.prototype.customizePage = function(page) {
	$('#update-credential').click(function(event) {
		event.preventDefault();
		$.mobile.changePage('#credential', {
			transition: 'pop',
			changeHash: false
		});
		return false;
	});
};
AccountPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	lilac.api.account.get(lilac.session.username)
		.done($.proxy(function(data, textStatus, jqXHR) {
			this.setAuthorProperty(data, 'username');
			this.setAuthorProperty(data, 'realname', '(未登録)');
			this.setAuthorProperty(data, 'emailAddress', '(未登録)');
			this.setAuthorProperty(data, 'libraryName', '(未登録)');
			this.setAuthorProperty(data, 'note');
			deferred.resolve(this.page);
		}, this))
		.fail(function(){
			deferred.reject();
		});
	return deferred.promise();
};
AccountPage.prototype.setAuthorProperty = function(entity, key, defaultValue) {
	var value = entity[key];
	if(value == null) {
		value = defaultValue || '-';
	}
	$(this.prefixedId(key)).text(value);
};

/**
 * 認証情報ページ
 */
CredentialPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/credential.html');
});
CredentialPage.prototype.customizePage = function(page) {
	$('#credential-update').click($.proxy(function(event) {
		event.preventDefault();
		var password = $('#credential-password').val();
		var passwordConfirm = $('#credential-password-confirm').val();
		if(password && password == passwordConfirm) {
			lilac.api.account.setCredential(lilac.session.username, password)
				.done($.proxy(function() {
					$.mobile.loading('show', {
						theme: "a",
						text: "認証情報を更新しました",
						textVisible: true,
						textonly: true});
					setTimeout($.proxy(function() {
						$.mobile.loading('hide');
						this.close();
					}, this), 1500 );
				}, this))
				.fail(function (jqXHR, textStatus, errorThrown) {
					lilac.showErrorMsg("認証情報の更新に失敗: " + textStatus);
				});
		}
		else {
			lilac.showErrorMsg("入力されたパスワードが不正です");
		}
		return false;
	}, this));
	$('#credential-cancel').click($.proxy(function(event) {
		event.preventDefault();
		this.close();
		return false;
	}, this));
};
CredentialPage.prototype.close = function() {
	$('#credential-password').val("");
	$('#credential-password-confirm').val("");
	$.mobile.changePage('#account', {
		transition: 'pop',
		reverse: true,
		fromPage: this.page
	});
};

InformationPage = lilac.extend(Page, function(id) {
	this.__super__.constructor(this, id, 'template/info.html');
});
InformationPage.prototype.customizePage = function(page) {
	$('#console-version').text(lilac.version);
};
InformationPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	$.mobile.loading('show');
	lilac.api.version()
		.done($.proxy(function(data){
			$('#api-version').text(data.version);
			$.mobile.loading('hide');
			deferred.resolve(this.page);
		}, this))
		.fail($.proxy(function() {
			$('#api-version').text("-");
			$.mobile.loading('hide');
			deferred.resolve(this.page);
		}));
	return deferred.promise();	
};

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
		$.mobile.loading('show');
		$('#upload-file').upload("/api/import", $.proxy(function(data){
			lilac.api.importData.list()
				.done($.proxy(function(data, textStatus, jqXHR) {
					this.refleshFileList(data);
					$.mobile.loading('hide');
				}, this))
				.fail(function (jqXHR, textStatus, errorThrown) {
					window.alert("インポートファイルの取得に失敗: " + textStatus);
					$.mobile.loading('hide');
				});
		}, this), "html");
		return false;
	}, this));
	$('#import-data').click($.proxy(function(event) {
		this.action = 'importData';
	}, this));
	$('#cancel').click($.proxy(function(event) {
		this.action = 'cancel';
	}, this));
	$('#import-form').submit($.proxy(function(event) {
		var selectedItem = $('#import-files input:radio:checked');
		if(selectedItem.length) {
			var fileId= selectedItem.val();
			var fileLabel = $('label[for="importfile-' + fileId + '"]').text();
			var confirmMessage = this.action == 'importData' ? "以下のファイルをインポートします" : "以下のファイルを削除します";
			if(window.confirm(confirmMessage + "\n" + fileLabel)) {
				$.mobile.loading('show');
				lilac.api.importData[this.action](fileId);
					lilac.api.importData.list()
					.done($.proxy(function(data, textStatus, jqXHR) {
						this.refleshFileList(data);
						$.mobile.loading('hide');
					}, this))
					.fail(function (jqXHR, textStatus, errorThrown) {
						window.alert("インポートファイルの取得に失敗: " + textStatus);
						$.mobile.loading('hide');
					});
			}
		}
		return false;
	}, this));
};
ImportPage.prototype.prepare = function(path, option) {
	var deferred = new $.Deferred();
	lilac.api.importData.list()
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
 * 蔵書検索ページ
 */
BookSearchPage = lilac.extend(BookListPageBase, function(id){
	this.__super__.constructor(this, id, 'template/booksearch.html');
});
BookSearchPage.prototype.customizePage = function(page) {
	this.__super__.customizePage.apply(this, arguments);
	$('form#booksearchform', page).submit($.proxy(function(event) {
		event.preventDefault();
		var condition = {};
		var value;
		(value = $('#booksearch-keyword').val()) && (condition['keyword'] = value);
		(value = $('#booksearch-label').val()) && (condition['label'] = value);
		(value = $('#booksearch-publicationDateBegin').val()) && (condition['publicationDateBegin'] = value);
		(value = $('#booksearch-publicationDateEnd').val()) && (condition['publicationDateEnd'] = value);
		(value = $('#booksearch-acquisitionDateBegin').val()) && (condition['acquisitionDateBegin'] = value);
		(value = $('#booksearch-acquisitionDateEnd').val()) && (condition['acquisitionDateEnd'] = value);
		var searchOptions = {
			condition: condition,
			page: 0,
			showLoadingMsg: true
		};
		this.listBooks(searchOptions)
			.done(function() {
				$('#booksearch-condition').trigger('collapse');
				$.mobile.silentScroll();
			});
		return false;
	}, this));
};
BookSearchPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	if(this.search.page >= 0) {
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
BookSearchPage.prototype.createItem = function(index, entity) {
	var item = $('<a>').attr("href", "#bib" + entity.id);
	item.append($('<p>').append($('<small>').text(entity.label)));
	item.append($('<p>').append($('<strong>').text(entity.title)));
	if(entity.subtitle) {
		item.append($('<p>').text(entity.subtitle));
	}
	var authors = $('<p class="booklist-authors">').text("著者: ").appendTo(item);
	for(var i = 0; i < entity.authors.length; ++i) {
		if(i != 0) {
			authors.append(document.createTextNode(', '));
		}
		authors.append($('<span>').text(entity.authors[i].name));
	}
	return $('<li>').append(
		$('<span>').addClass("li-index").text(entity.index),
		document.createTextNode(" "),
		item);
};
BookSearchPage.prototype.updateLabelOptions = function() {
	var deferred = $.Deferred();
	lilac.api.label.list()
		.done($.proxy(function (data, textStatus, jqXHR) {
			var select = $('#booksearch-label', this.page);
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

/**
 * 蔵書更新情報ページ
 */
ArrivalPage = lilac.extend(BookListPageBase, function(id){
	this.__super__.constructor(this, id, 'template/arrival.html');
	this.prepared = false;
});
ArrivalPage.prototype.customizePage = function(page) {
	this.__super__.customizePage.apply(this, arguments);
	$('#arrival-selectform').submit($.proxy(function(event) {
		event.preventDefault();
		$.mobile.loading('show');
		this.updateArrival($('#arrival-select-year').val(), $('#arrival-select-month').val())
			.done(function() {
				$('#arrival-month-title').click();
				$.mobile.silentScroll();
				$.mobile.loading('hide');
			})
			.fail(function() {
				$.mobile.loading('hide');
			});
		return false;
	}, this));
};
ArrivalPage.prototype.prepare = function(path, options) {
	var deferred = $.Deferred();
	if(this.prepared) {
		deferred.resolve(this.page);
	}
	else {
		var now = new Date();
		this.updateArrival(now.getFullYear(), now.getMonth() + 1)
			.done($.proxy(function() {
				this.prepared = true;
				deferred.resolve(this.page);
			}, this))
			.fail(function() {
				deferred.reject();
			});
	}
	return deferred.promise();
};
ArrivalPage.prototype.createItem = function(index, entity) {
	var item = $('<a>').attr("href", "#bib" + entity.id);
	item.append($('<p>').append($('<small>').text(entity.label)));
	item.append($('<p>').append($('<strong>').text(entity.title)));
	if(entity.subtitle) {
		item.append($('<p>').text(entity.subtitle));
	}
	var authors = $('<p class="booklist-authors">').text("著者: ").appendTo(item);
	for(var i = 0; i < entity.authors.length; ++i) {
		if(i != 0) {
			authors.append(document.createTextNode(', '));
		}
		authors.append($('<span>').text(entity.authors[i].name));
	}
	item.append($('<p class="booklist-description">').append(
			$('<small>').text('購入日: ' + entity.books[0].acquisitionDate)));
	return $('<li>').append(
		$('<span>').addClass("li-index").text(entity.index),
		document.createTextNode(" "),
		item);
};
ArrivalPage.prototype.updateArrival = function(year, month) {
	var deferred = $.Deferred();

	var lastDayOfMonth = new Date(year, month, 0);
	var searchOptions = {
		condition: {
			acquisitionDateBegin: this.dateToString(year, month, 1),
			acquisitionDateEnd: this.dateToString(lastDayOfMonth.getFullYear(), lastDayOfMonth.getMonth() + 1, lastDayOfMonth.getDate()),
			sort1: 'acquisitionDateAsc',
			sort2: 'labelAsc',
			sort3: 'titleAsc'
		},
		page: 0,
		showLoadingMsg: false
	};
	this.listBooks(searchOptions)
		.done($.proxy(function() {
			$('#arrival-select-year').val(year);
			$('#arrival-select-month').val(month);
			$('#arrival-select-year').selectmenu('refresh');
			$('#arrival-select-month').selectmenu('refresh');
			$('#arrival-month').text('' + year + '年' + month + '月');
			deferred.resolve();
		}, this))
		.fail(function() {
			deferred.reject();
		});
	return deferred.promise();
};
ArrivalPage.prototype.dateToString = function(year, month, day) {
	return '' + year + '-' + (month < 10 ? '0' + month : month) + '-' + (day < 10 ? '0' + day : day);
};

/**
 * 書誌情報ページ
 */
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
		lilac.api.bibliography.get(path.bid)
			.done($.proxy(function(data) {
				this.setEntityProperty(data, "label", " ");
				this.setEntityProperty(data, "title");
				this.setEntityProperty(data, "subtitle", " ");
				this.setEntityProperty(data, "isbn");
				this.setEntityProperty(data, "publicationDate");
				this.setEntityProperty(data, "price");
	
				var authorList = $(this.prefixedId('authors'), this.page);
				$.each(data.authors, $.proxy(function(index, entity){
					authorList.append(
						$('<li>').append(
							$('<a>').attr('href', '#author' + entity.id)
								.text(entity.name + '(' + this.roleName(entity.role) + ')')));
				}, this));
				authorList.listview('refresh');

				var bookList = $(this.prefixedId('books'), this.page);
				$.each(data.books, $.proxy(function(index, entity) {
					var item = $('<li>');
					if(entity.location) {
						item.append($('<span>').text('[' + entity.owner + '] ' + entity.location.label));
					}
					else {
						item.append(
							$('<span>').text('[' + entity.owner + '] '),
							$('<span class="li-book-orphan">(未整理)</span>'));
					}							
					if(entity.acquisitionDate || entity.purchaseShop) {
						item.append(
							$('<br>'),
							$('<span class="li-book-acuisition">').text(
								(entity.acquisitionDate ? entity.acquisitionDate : '')
								+ (entity.purchaseShop ? ' @' + entity.purchaseShop : '')));
					}
					bookList.append(item);
				}, this));
				bookList.listview('refresh');

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
BibliographyPage.prototype.roleName = function(role) {
	switch(role) {
	case "author":
		return "著者";
	case "illustrator":
		return "イラスト";
	case "originator":
		return "原作";
	case "supervisor":
		return "監修";
	case "editor":
		return "編集";
	default:
		return role;
	}
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
			condition: {
				authorId: path.aid,
				sort1: 'publicationDateAsc',
				sort2: 'titleAsc'
			},
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
			this.setEntityProperty(data, "name");
			if(data.website) {
				$(this.prefixedId('website')).empty().append(
						$('<a target="_blank">').attr('href', data.website).text(data.website));
			}
			if(data.twitter) {
				$(this.prefixedId('twitter')).empty().append(
						$('<a target="_blank">').attr('href', 'http://twitter.com/' + data.twitter).text('@' + data.twitter));
			}
	
			var synonymList = $(this.prefixedId('synonymlist'), this.page);
			if(!data.synonym.length) {
				synonymList.remove();
			}
			else {
				$.each(data.synonym, function(index, entity){
					var item = $('<li>').attr('id', 'author-' + entity.id).append(
							$('<a>').attr('href', '#author' + entity.id).text(entity.name)
						);
					synonymList.append(item);
				});
				synonymList.listview('refresh');
			}
			deferred.resolve();
		}, this))
		.fail(function(jqXHR, textStatus){
			window.alert("著者情報の取得に失敗しました。:" + textStatus);
			deferred.reject();
		});
	return deferred.promise();
};


// Action定義
lilac.actions = [
	new Action('#main', MainPage),
	new Action('#booksearch', BookSearchPage),
	new Action('#arrival', ArrivalPage),
	new Action('#bib(?<bid>\\d+)', BibliographyPage),
	new Action('#author(?<aid>\\d+)', AuthorPage),
	new Action('#login', LoginPage),
	new Action('#admin', AdminPage),
	new Action('#info', InformationPage),
	new Action('#account', AccountPage, true),
	new Action('#credential', CredentialPage, true),
	new Action('#export', ExportPage, true),
	new Action('#import', ImportPage, true)
];
