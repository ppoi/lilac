/*
 * All Rights Reserved.
 * Copyright (C) 2011-2012 Tsukuba Bunko.
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

import java.util.Map;

import javax.annotation.Resource;

import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Forward;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;


/**
 * Service Inforamtion API
 * @author ppoi
 * @version 2012.06
 */
@ActionClass
@Path("/")
public class ServiceInfoAction {

	/**
	 * API リスト
	 */
	public static final String[] API_LIST = {
		"label", "author", "bibliograpy", "book", "booksearch", "reading-record",
		"session", "account", "export", "import"
	};

	/**
	 * API バージョン
	 */
	@Resource
	public String lilacApiVersion;

	public ActionResult index() {
		Map<String, Object> dto = new java.util.HashMap<String, Object>();
		dto.put("version", lilacApiVersion);
		dto.put("api", API_LIST);
		return new Json(dto);
	}
	
	public ActionResult author() {
		return new Forward(AuthorAction.class);
	}

	public ActionResult label() {
		return new Forward(LabelAction.class);
	}
}
