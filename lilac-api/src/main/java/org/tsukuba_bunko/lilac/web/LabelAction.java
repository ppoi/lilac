/*
 * All Rights Reserved.
 * Copyright (C) 2011 Tsukuba Bunko.
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

import java.util.List;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.entity.Label;
import org.tsukuba_bunko.lilac.service.LabelService;
import org.tsukuba_bunko.lilac.web.converter.NoopConverter;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
@Path("label")
@Accept({RequestMethod.GET})
public class LabelAction {

	public LabelService labelService;

	@RequestParameter(converter=NoopConverter.class)
	public String name;

	@RequestParameter
	public Object requestData;

	public ActionResult index() {
		List<Label> labels = labelService.list();
		return new Json(labels);
	}

	@Path("{name,.+}")
	public ActionResult get() {
		Label label = labelService.get(name);
		return new Json(label);
	}

	@Accept(RequestMethod.POST)
	@Path("new")
	@Auth
	public ActionResult create() {
		Label label = new Label();
		Beans.copy(requestData, label).execute();
		labelService.create(label);
		return new Json(label);
	}

	@Accept(RequestMethod.PUT)
	@Path("{name,.+}")
	@Auth
	public ActionResult update() {
		Label label = labelService.get(name);
		Beans.copy(requestData, label).execute();
		if(!name.equals(label.name)) {
			//TODO: validation error
		}
		labelService.update(label);
		return new Json(label);
	}
}
