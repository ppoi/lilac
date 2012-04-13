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
package org.tsukuba_bunko.lilac.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.MessageDigestUtil;
import org.tsukuba_bunko.lilac.entity.UserAuth;
import org.tsukuba_bunko.lilac.entity.UserSession;
import org.tsukuba_bunko.lilac.service.UserSessionService;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class UserSessionServiceImpl implements UserSessionService {

	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.UserSessionService#open(java.lang.String, java.lang.String)
	 */
	@Override
	public UserSession open(String user, String password) {
		//認証
		String digestedPassword = digestPassword(password);
		jdbcManager.from(UserAuth.class).where(new SimpleWhere()
			.eq("user", user)
			.eq("password", digestedPassword)
		).disallowNoResult().getSingleResult();

		//セッション情報の作成
		UserSession session = new UserSession();
		session.id = UUID.randomUUID().toString();
		session.user = user;
		jdbcManager.insert(session).excludesNull().execute();
		return session;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.UserSessionService#getValidSession(java.lang.String)
	 */
	@Override
	public UserSession getValidSession(String sessionId) {
		return jdbcManager.from(UserSession.class).where(new SimpleWhere()
			.eq("id", sessionId)
		).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.UserSessionService#invalidate(java.lang.String)
	 */
	@Override
	public void invalidate(String sessionId) {
		UserSession session = new UserSession();
		session.id = sessionId;
		jdbcManager.delete(session);
	}

	public String digestPassword(String password) {
		MessageDigest md = MessageDigestUtil.getInstance("SHA-1");
		try {
			md.update(password.getBytes("UTF-8"));
			return Base64Util.encode(md.digest());
		}
		catch(UnsupportedEncodingException uee) {
			throw new IllegalStateException(uee);
		}
	}
}
