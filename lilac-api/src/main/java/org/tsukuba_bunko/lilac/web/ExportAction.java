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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Direct;
import org.seasar.cubby.action.Path;
import org.tsukuba_bunko.lilac.service.ExportService;


/**
 * りらDBエクスポートAPI。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
@Path("/export")
public class ExportAction {

	@Resource
	public HttpServletRequest request;

	@Resource
	public HttpServletResponse response;

	@Resource
	public ExportService exportService;


	public ActionResult index() throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("content-disposition", "attachment; filename=lilacCatalog-all.xlsx");
		exportService.exportAll(response.getOutputStream());
		return new Direct();
	}
}
