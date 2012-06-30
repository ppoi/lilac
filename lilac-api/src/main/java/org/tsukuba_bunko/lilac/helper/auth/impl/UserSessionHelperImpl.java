/*
 * All Rights Reserved.
 * Copyright (C) 2012 Tsukuba Bunko.
 *
 * Licensed under the BSD License ("the License"); you may not use
 * this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.tsukuba-bunko.org/licenses/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id:　$
 */
package org.tsukuba_bunko.lilac.helper.auth.impl;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper;


/**
 * ユーザセッションHelper実装
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class UserSessionHelperImpl implements UserSessionHelper {

	/**
	 * セッションクッキーのキー名
	 */
	public static final String SESSION_COOKIE_KEY = "LILACSESSION";

	/**
	 * クッキーのセキュリティ設定
	 */
	@Resource
	public Boolean secureCookie;

	@Resource
	public HttpServletRequest request;

	@Resource
	public HttpServletResponse response;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper#getSessionId()
	 */
	@Override
	public String getSessionId() {
		if(request != null) {
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if(SESSION_COOKIE_KEY.equals(cookie.getName())) {
						return cookie.getValue();
					}
				}
			}
		}
		return null;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper#setSessionId(java.lang.String)
	 */
	@Override
	public void setSessionId(String sessionId) {
		Cookie cookie = new Cookie(SESSION_COOKIE_KEY, sessionId);
		cookie.setSecure(secureCookie);
		cookie.setPath("/api/");
		if(sessionId == null) {
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}
}
