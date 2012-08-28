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
 */
package org.tsukuba_bunko.lilac.web;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Direct;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.cubby.action.SendError;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.entity.Account;
import org.tsukuba_bunko.lilac.entity.UserAuth;
import org.tsukuba_bunko.lilac.service.AccountService;
import org.tsukuba_bunko.lilac.util.DigestUtil;


/**
 * Account API
 * @author ppoi
 * @version 2012.04
 */
@ActionClass
public class AccountAction {

	@Resource
	public AccountService accountService;

	@RequestParameter
	public String username;

	@RequestParameter
	public Object requestData;

	@Auth
	@Path("/account/{username}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		if(StringUtil.isBlank(username)) {
			return new SendError(400);
		}
		Account account = accountService.get(username);
		if(account != null) {
			return new Json(account);
		}
		else {
			return new SendError(404);
		}
	}

	@Auth
	@Path("/account")
	@Accept(RequestMethod.GET)
	public ActionResult list() {
		return new Json(accountService.list());
	}

	@Auth
	@Path("/account")
	@Accept(RequestMethod.PUT)
	public ActionResult create() {
		if(requestData == null) {
			return new SendError(400);
		}
		Account entity = new Account();
		Beans.copy(requestData, entity).excludesNull().execute();
		accountService.create(entity);
		return new Json(entity);
	}

	@Auth
	@Path("/account/{username}")
	@Accept(RequestMethod.POST)
	public ActionResult update() {
		if(requestData == null || StringUtil.isBlank(username)) {
			return new SendError(400);
		}
		Account entity = new Account();
		Beans.copy(requestData, entity).execute();

		if(!username.equals(entity.username)) {
			return new SendError(400, "username mismatch");
		}
		if(accountService.get(entity.username) == null) {
			return new SendError(404);
		}

		try {
			accountService.update(entity);
			return new Json(entity);
		}
		catch(OptimisticLockException ole) {
			return new SendError(409);
		}
	}

	@Auth
	@Path("/account/{username}")
	@Accept(RequestMethod.DELETE)
	public ActionResult delete() {
		if(StringUtil.isBlank(username)) {
			return new SendError(400);
		}

		accountService.delete(username);
		return new Direct();
	}

	@Auth
	@Path("/account/{username}/credential")
	@Accept(RequestMethod.POST)
	public ActionResult setCredential() {
		if(!(requestData instanceof String) || StringUtil.isBlank(username)) {
			return new SendError(400);
		}

		UserAuth credential = new UserAuth();
		credential.username = username;
		credential.password = DigestUtil.digestText((String)requestData);
		accountService.setCredential(credential);
		return new Direct();
	}
}
