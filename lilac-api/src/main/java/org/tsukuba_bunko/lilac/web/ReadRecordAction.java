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

import java.util.Date;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.tsukuba_bunko.lilac.annotation.Auth;


/**
 * 読書履歴API
 * @author ppoi
 * @version 2012.06
 */
@ActionClass
public class ReadRecordAction {

	@RequestParameter
	public String isbn;

	@RequestParameter
	public Date comeleteDateBegin;

	@RequestParameter
	public Date comeleteDateEnd;

	@RequestParameter
	public Integer page;

	@Path("/readrecord(:?/list/{page,\\d+})?")
	@Accept(RequestMethod.GET)
	public ActionResult list() {
		return new Json(null);
	}

	@Path("/readrecord/incomplete")
	@Accept(RequestMethod.GET)
	public ActionResult listIncompleted() {
		return new Json(null);
	}

	@Path("/readrecord/{recordId,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		return new Json(null);
	}

	@Path("/readrecord")
	@Auth
	@Accept(RequestMethod.PUT)
	public ActionResult create() {
		return new Json(null);
	}

	@Path("/readrecord/{recordId,\\d+}$")
	@Auth
	@Accept(RequestMethod.POST)
	public ActionResult update() {
		return null;
	}

	@Path("/readrecord/start$")
	@Auth
	@Accept(RequestMethod.PUT)
	public ActionResult start() {
		return null;
	}

	@Path("/readrecord/{recordId,\\d+}/complete$")
	@Auth
	@Accept(RequestMethod.POST)
	public ActionResult complete() {
		return null;
	}
}
