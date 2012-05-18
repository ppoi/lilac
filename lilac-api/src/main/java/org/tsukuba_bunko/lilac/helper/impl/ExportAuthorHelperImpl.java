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
package org.tsukuba_bunko.lilac.helper.impl;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.JdbcManager;
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportAuthorHelperImpl implements ExportEntityHelper {

	@Resource
	public JdbcManager jdbcManager;

	private XSSFSheet sheet;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.ExportEntityHelper#exportAll(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	public void exportAll(XSSFWorkbook book) {
		sheet = book.createSheet("著者");
	}

}
