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

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.AutoSelect;
import org.tsukuba_bunko.lilac.entity.Author;
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;


/**
 * 著者情報をエクスポートする {@link ExportEntityHelper} 実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportAuthorHelperImpl extends ExportEntityHelperBase<Author> {

	private XSSFCellStyle flagCellStyle;
	private XSSFCellStyle idCellStyle;
	private XSSFCellStyle commonCellStyle;
	private XSSFCellStyle noteCellStyle;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#getSheetName()
	 */
	@Override
	protected String getSheetName() {
		return "著者";
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#prepare(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void prepare(XSSFWorkbook book) {
		flagCellStyle = book.createCellStyle();
		flagCellStyle.setAlignment(HorizontalAlignment.CENTER);
		flagCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		idCellStyle = book.createCellStyle();

		commonCellStyle = book.createCellStyle();
		commonCellStyle.setAlignment(HorizontalAlignment.LEFT);
		commonCellStyle.setVerticalAlignment(VerticalAlignment.TOP);

		noteCellStyle = book.createCellStyle();
		XSSFFont noteFont = book.createFont();
		noteFont.setFontHeightInPoints((short)9);
		noteCellStyle.setFont(noteFont);
		noteCellStyle.setAlignment(HorizontalAlignment.LEFT);
		noteCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		noteCellStyle.setWrapText(true);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#buildQuery()
	 */
	@Override
	protected AutoSelect<Author> buildQuery() {
		return jdbcManager.from(Author.class).orderBy("id ASC");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#processRow(java.lang.Object, org.apache.poi.xssf.usermodel.XSSFRow, int)
	 */
	@Override
	protected void processRow(Author entity, XSSFRow row, int index) {
		//更新フラグ
		setCellValue(createCell(row, 0, flagCellStyle), "U");
		//ID
		setCellValue(createCell(row, 1, idCellStyle), entity.id);
		//著者名
		setCellValue(createCell(row, 2, commonCellStyle), entity.name);
		//Webサイト
		setCellValue(createCell(row, 3, noteCellStyle), entity.website);
		//備考
		setCellValue(createCell(row, 4, noteCellStyle), entity.note);
		//シノニムキー
		setCellValue(createCell(row, 5, noteCellStyle), entity.synonymKey);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#finish(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void finish(XSSFWorkbook book) {
		// TODO Auto-generated method stub		
	}
}
