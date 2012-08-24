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
package org.tsukuba_bunko.lilac.web;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Direct;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.framework.beans.util.BeanMap;
import org.tsukuba_bunko.lilac.entity.UserSession;
import org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper;
import org.tsukuba_bunko.lilac.service.UserSessionService;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
public class SessionAction {

	@Resource
	public UserSessionService userSessionService;

	@Resource
	public UserSessionHelper userSessionHelper;

	@Resource
	public HttpServletRequest request;

	@Resource
	public HttpServletResponse response;

	@RequestParameter
	public String userId;
	
	@RequestParameter
	public String password;

	@Path("/session")
	public ActionResult index() {
		String sessionId = userSessionHelper.getSessionId();
		if(sessionId != null) {
			try {
				return new Json(userSessionService.getValidSession(sessionId));
			}
			catch(NoResultException nre) {
				//セッションが無効の場合，セッションクッキーを削除する
				userSessionHelper.setSessionId(null);
			}
		}
		return new Json(new BeanMap());
	}

	@Path("/session/login")
	@Accept({RequestMethod.POST})
	public ActionResult login() {
		UserSession session = userSessionService.open(userId, password);
		userSessionHelper.setSessionId(session.id);
		return new Json(session);
	}

	@Path("/session/logout")
	public ActionResult logout() {
		String sessionId = userSessionHelper.getSessionId();
		if(sessionId != null) {
			userSessionService.invalidate(sessionId);
			userSessionHelper.setSessionId(null);
		}
		return new Direct();
	}
}
