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
package org.tsukuba_bunko.lilac.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.ImportFile;
import org.tsukuba_bunko.lilac.entity.UserSession;
import org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper;
import org.tsukuba_bunko.lilac.helper.port.ImportDataHelper;
import org.tsukuba_bunko.lilac.service.ImportFileDescriptor;
import org.tsukuba_bunko.lilac.service.ImportService;
import org.tsukuba_bunko.lilac.service.UserSessionService;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ImportServiceImpl implements ImportService {

	private static final Logger log = Logger.getLogger(ImportServiceImpl.class);

	@Resource
	public UserSessionService userSessionService;

	@Resource
	public UserSessionHelper userSessionHelper;

	@Resource(name="port_importLabelHelper")
	public ImportDataHelper importLabelHelper;

	@Resource(name="port_importAuthorHelper")
	public ImportDataHelper importAuthorHelper;

	@Resource(name="port_importBibliographyHelper")
	public ImportDataHelper importBibliographyHelper;

	@Resource(name="port_importBookshelfHelper")
	public ImportDataHelper importBookshelfHelper;

	@Resource(name="port_importBookHelper")
	public ImportDataHelper importBookHelper;

	@Resource(name="port_importReadingRecordHelper")
	public ImportDataHelper importReadingRecordHelper;

	@Resource
	public JdbcManager jdbcManager;

	@Resource(name="importFileStore")
	public File importFileStore;

	/**
	 * @see org.tsukuba_bunko.lilac.service.ImportService#importData(int)
	 */
	@Override
	public void importData(int fileId) {
		String userId = getCurrentSessionUser();
		ImportFile importFileEntity = jdbcManager.from(ImportFile.class)
									.where(new SimpleWhere()
										.eq("id", fileId)
										.eq("user", userId)
									).disallowNoResult().getSingleResult();

		File importFile = new File(importFileStore, String.format("%016d", importFileEntity.id));
		FileInputStream source = null;
		try {
			source = new FileInputStream(importFile);
			XSSFWorkbook book = new XSSFWorkbook(source);

			XSSFSheet sheet = book.getSheet("レーベル");
			if(sheet != null) {
				importLabelHelper.importData(sheet);
			}

			sheet = book.getSheet("著者");
			if(sheet != null) {
				importAuthorHelper.importData(sheet);
			}

			sheet = book.getSheet("書誌情報");
			if(sheet != null) {
				importBibliographyHelper.importData(sheet);
			}

			sheet = book.getSheet("書棚");
			if(sheet != null) {
				importBookshelfHelper.importData(sheet);
			}

			sheet = book.getSheet("蔵書");
			if(sheet != null) {
				importBookHelper.importData(sheet);
			}

			sheet = book.getSheet("読書履歴");
			if(sheet != null) {
				importReadingRecordHelper.importData(sheet);
			}
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
		finally {
			InputStreamUtil.closeSilently(source);
		}

		importFile.delete();
		jdbcManager.delete(importFileEntity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ImportService#list()
	 */
	@Override
	public List<ImportFile> list() {
		String user = getCurrentSessionUser();
		if(StringUtil.isBlank(user)) {
			return java.util.Collections.emptyList();
		}
		return jdbcManager.from(ImportFile.class)
				.where(new SimpleWhere()
					.eq("user", user)
				)
				.orderBy("createdTimestamp")
				.getResultList();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ImportService#upload(String, java.io.InputStream)
	 */
	@Override
	public ImportFileDescriptor upload(String fileName, InputStream source) {
		ImportFile importFile = new ImportFile();
		importFile.fileName = fileName;
		importFile.user = getCurrentSessionUser();
		jdbcManager.insert(importFile).excludesNull().execute();

		File storedFile = new File(importFileStore, String.format("%016d", importFile.id));
		FileOutputStream target = null;
		try {
			target = new FileOutputStream(storedFile);
			InputStreamUtil.copy(source, target);
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
		finally {
			OutputStreamUtil.close(target);
		}
		ImportFileDescriptor descriptor = new ImportFileDescriptor();
		descriptor.id = importFile.id;
		return descriptor;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ImportService#cancel(int)
	 */
	@Override
	public void cancel(int fileId) {
		String userId = getCurrentSessionUser();
		ImportFile importFileEntity = jdbcManager.from(ImportFile.class)
									.where(new SimpleWhere()
										.eq("id", fileId)
										.eq("user", userId)
									).getSingleResult();
		if(importFileEntity != null) {
			File importFile = new File(importFileStore, String.format("%016d", importFileEntity.id));
			if(importFile.exists()) {
				if(!importFile.delete()) {
					log.log("WAPI1901", new Object[]{importFile});
				}
			}
			jdbcManager.delete(importFileEntity).execute();
		}
	}

	public String getCurrentSessionUser() {
		String sessionId = userSessionHelper.getSessionId();
		if(StringUtil.isNotBlank(sessionId)) {
			UserSession session = userSessionService.getValidSession(sessionId);
			return session.user;
		}
		return null;
	}
}
