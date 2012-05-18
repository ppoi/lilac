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
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;
import org.tsukuba_bunko.lilac.service.ExportService;


/**
 * {@link ExportService}実装
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportServiceImpl implements ExportService {

	@Resource(name="exportLabelHelper")
	public ExportEntityHelper exportLabelHelper;

	@Resource(name="exportAuthorHelper")
	public ExportEntityHelper exportAuthorHelper;

	@Resource(name="exportBookHelper")
	public ExportEntityHelper exportBookHelper;


	/**
	 * @see org.tsukuba_bunko.lilac.service.ExportService#exportAll(OutputStream)
	 */
	@Override
	public void exportAll(OutputStream target) {
		XSSFWorkbook book = new XSSFWorkbook();

		exportLabelHelper.exportAll(book);
		exportAuthorHelper.exportAll(book);
		exportBookHelper.exportAll(book);

		try {
			book.write(target);
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
	}
}
