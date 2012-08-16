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

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.framework.exception.IORuntimeException;
import org.tsukuba_bunko.lilac.helper.port.ExportDataHelper;
import org.tsukuba_bunko.lilac.service.ExportService;


/**
 * {@link ExportService}実装
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportServiceImpl implements ExportService {

	@Resource(name="port_exportLabelHelper")
	public ExportDataHelper exportLabelHelper;

	@Resource(name="port_exportAuthorHelper")
	public ExportDataHelper exportAuthorHelper;

	@Resource(name="port_exportBibliographyHelper")
	public ExportDataHelper exportBibliographyHelper;

	@Resource(name="port_exportBookshelfHelper")
	public ExportDataHelper exportBookshelfHelper;

	@Resource(name="port_exportBookHelper")
	public ExportDataHelper exportBookHelper;

	@Resource(name="port_exportReadingRecordHelper")
	public ExportDataHelper exportReadingRecordHelper;

	/**
	 * @see org.tsukuba_bunko.lilac.service.ExportService#exportAll(OutputStream)
	 */
	@Override
	public void exportAll(OutputStream target) {
		exportData(target, ExportTarget.All);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ExportService#exportData(java.io.OutputStream, org.tsukuba_bunko.lilac.service.ExportService.ExportTarget[])
	 */
	@Override
	public void exportData(OutputStream target, ExportTarget... exportTargets) {
		if(exportTargets.length == 0) {
			exportData(target, ExportTarget.All);
		}

		XSSFWorkbook book = new XSSFWorkbook();

		for(ExportTarget exportTarget : exportTargets) {
			switch(exportTarget) {
				case Label:
					exportLabelHelper.exportData(book);
					break;
				case Author:
					exportAuthorHelper.exportData(book);
					break;
				case Bibliography:
					exportBibliographyHelper.exportData(book);
					break;
				case Bookshelf:
					exportBookshelfHelper.exportData(book);
					break;
				case Book:
					exportBookHelper.exportData(book);
					break;
				case ReadingRecord:
					exportReadingRecordHelper.exportData(book);
					break;
				case All:
					exportLabelHelper.exportData(book);
					exportAuthorHelper.exportData(book);
					exportBibliographyHelper.exportData(book);
					exportBookshelfHelper.exportData(book);
					exportBookHelper.exportData(book);
					exportReadingRecordHelper.exportData(book);
					break;
			}
		}

		try {
			book.write(target);
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
	}
}
