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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.cubby.action.SendError;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.InputStreamUtil;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.entity.ImportFile;
import org.tsukuba_bunko.lilac.service.ImportFileDescriptor;
import org.tsukuba_bunko.lilac.service.ImportService;


/**
 * りらDBインポートAPI
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@ActionClass
public class ImportAction {

	@Resource
	public ImportService importService;

	@RequestParameter(name="upload-file")
	public FileItem uploadedFile;

	@RequestParameter
	public Integer fileId;

	@Auth
	@Path("/import")
	@Accept(RequestMethod.POST)
	public ActionResult upload() throws Exception {
		if(uploadedFile == null) {
			return new SendError(HttpServletResponse.SC_BAD_REQUEST, "missing file.");
		}
		InputStream source = uploadedFile.getInputStream();
		try {
			ImportFileDescriptor importFile = importService.upload(uploadedFile.getName(), source);
			BeanMap dto = new BeanMap();
			Beans.copy(importFile, dto).timestampConverter("yyyy/MM/dd HH:mm:ss", "createdTimestamp").execute();
			return new Json(dto);
		}
		finally {
			InputStreamUtil.closeSilently(source);
			uploadedFile.delete();
		}
	}

	@Auth
	@Path("/import")
	@Accept(RequestMethod.GET)
	public ActionResult index() {
		List<Map<String, String>> dtoList = new java.util.ArrayList<Map<String, String>>();
		for(ImportFile importFile : importService.list()) {
			Map<String, String> dto = new java.util.HashMap<String, String>();
			Beans.copy(importFile, dto).excludes("user").timestampConverter("yyyy/MM/dd HH:mm:ss", "createdTimestamp").execute();
			dtoList.add(dto);
		}
		return new Json(dtoList);
	}

	@Auth
	@Path("/import/{fileId,[a-zA-Z0-9]+}")
	@Accept(RequestMethod.PUT)
	public ActionResult importData() {
		if(fileId == null) {
			return new SendError(HttpServletResponse.SC_BAD_REQUEST, "missing fileId.");
		}
		importService.importData(fileId);
		return new Json(Boolean.TRUE);
	}

	@Auth
	@Path("/import/{fileId,[a-zA-Z0-9]+}")
	@Accept(RequestMethod.DELETE)
	public ActionResult cancel() {
		if(fileId == null) {
			return new SendError(HttpServletResponse.SC_BAD_REQUEST, "missing fileId.");
		}
		importService.cancel(fileId);
		return new Json(Boolean.TRUE);
	}
}
