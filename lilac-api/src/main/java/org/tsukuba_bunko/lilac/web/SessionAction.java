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

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.tsukuba_bunko.lilac.entity.UserSession;
import org.tsukuba_bunko.lilac.service.UserSessionService;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
@Path("session")
public class SessionAction {

	@Resource
	public UserSessionService userSessionService;

	@RequestParameter
	public String userId;
	
	@RequestParameter
	public String password;
	
	@RequestParameter
	public String sessionId;

	@Accept({RequestMethod.POST})
	public ActionResult index() {
		return new Json(userSessionService.getValidSession(sessionId));
	}

	@Accept({RequestMethod.POST})
	public ActionResult login() {
		UserSession session = userSessionService.open(userId, password);
		return new Json(session);
	}

	@Accept({RequestMethod.POST})
	public ActionResult validate() {
		boolean result = false;
		try {
			userSessionService.getValidSession(sessionId);
			result = true;
		}
		catch(NoResultException nre) {
			result = false;
		}
		return new Json(result);
	}
}
