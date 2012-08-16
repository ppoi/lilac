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

import java.util.List;

import javax.annotation.Resource;
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
import org.seasar.framework.log.Logger;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.service.ExportService;
import org.tsukuba_bunko.lilac.service.ExportService.ExportTarget;


/**
 * りらDBエクスポートAPI。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
public class ExportAction {

	public static final List<String> SUPPORTED_TARGETS = java.util.Arrays.asList("all","label","author","bibliography","bookshelf","book","reading-record");

	private static final Logger log = Logger.getLogger(ExportAction.class);

	@Resource
	public HttpServletRequest request;

	@Resource
	public HttpServletResponse response;

	@Resource
	public ExportService exportService;

	@RequestParameter
	public String targets;

	@Path("/export")
	@Accept(RequestMethod.GET)
	public ActionResult index() throws Exception {
		return new Json(SUPPORTED_TARGETS);
	}

	@Auth
	@Path("/export/{targets,[a-z-]+(?:\\+[a-z-]+)*}")
	@Accept(RequestMethod.GET)
	public ActionResult export() throws Exception {
		List<ExportTarget> targetList = getTargetList(targets);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("content-disposition", "attachment; filename=lilac-" + targets + ".xlsx");
		if(targetList.contains(ExportTarget.All)) {
			exportService.exportAll(response.getOutputStream());
		}
		else {
			exportService.exportData(response.getOutputStream(), targetList.toArray(new ExportTarget[targetList.size()]));
		}
		return new Direct();
	}

	public List<ExportTarget> getTargetList(String targets) {
		List<ExportTarget> targetList = new java.util.ArrayList<ExportTarget>();
		for(String target : targets.split("\\+")) {
			if(!SUPPORTED_TARGETS.contains(target)) {
				log.log("EAPI0801", new Object[]{targets});
				throw new IllegalArgumentException(target);
			}
			ExportTarget exportTarget = null;
			switch(target) {
				case "label":
					exportTarget = ExportTarget.Label;
					break;
				case "author":
					exportTarget = ExportTarget.Author;
					break;
				case "bibliography":
					exportTarget = ExportTarget.Bibliography;
					break;
				case "bookshelf":
					exportTarget = ExportTarget.Bookshelf;
					break;
				case "book":
					exportTarget = ExportTarget.Book;
					break;
				case "reading-record":
					exportTarget = ExportTarget.ReadingRecord;
					break;
				case "all":
					exportTarget = ExportTarget.All;
					break;
			}
			if(!targetList.contains(exportTarget)) {
				targetList.add(exportTarget);
			}
		}
		return targetList;
	}
}
