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

import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestParameter;
import org.tsukuba_bunko.lilac.annotation.Auth;


/**
 * りらDBインポートAPI
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
@Path("/import")
public class ImportAction {

	@RequestParameter
	public String fileId;

	@Auth
	public ActionResult upload() {
		return new Json(Boolean.FALSE);
	}

	@Auth
	@Path("/{id,[a-zA-Z0-9]+}")
	public ActionResult importAll() {
		return new Json(Boolean.FALSE);
	}
}
