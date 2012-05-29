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
import org.tsukuba_bunko.lilac.entity.Label;
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;


/**
 * レーベル情報をエクスポートする {@link ExportEntityHelper} 実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportLabelHelperImpl extends ExportEntityHelperBase<Label> {

	private XSSFCellStyle flagCellStyle;

	private XSSFCellStyle noteCellStyle;

	private int maxLabelNameLength;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#getSheetName()
	 */
	@Override
	protected String getSheetName() {
		return "レーベル";
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#prepare(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void prepare(XSSFWorkbook book) {
		flagCellStyle = book.createCellStyle();
		flagCellStyle.setAlignment(HorizontalAlignment.CENTER);
		flagCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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
	protected AutoSelect<Label> buildQuery() {
		return jdbcManager.from(Label.class).orderBy("name ASC");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#processHeaderRow(org.apache.poi.xssf.usermodel.XSSFRow)
	 */
	@Override
	protected void processHeaderRow(XSSFRow row) {
		createHeaderCell(row, 0, null);
		createHeaderCell(row, 1, "レーベル名");
		createHeaderCell(row, 2, "備考");
		createHeaderCell(row, 3, "Webサイト");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#processRow(java.lang.Object, org.apache.poi.xssf.usermodel.XSSFRow, int)
	 */
	@Override
	protected void processRow(Label entity, XSSFRow row, int index) {
		//更新フラグ
		setCellValue(createCell(row, 0, flagCellStyle), "U");
		//レーベル名
		setCellValue(createCell(row, 1, null), entity.name);
		//備考
		setCellValue(createCell(row, 2, noteCellStyle), entity.note);
		//Webサイト
		setCellValue(createCell(row, 3, noteCellStyle), entity.website);

		//最長レーベル名
		if(entity.name.length() > maxLabelNameLength) {
			maxLabelNameLength = entity.name.length();
		}
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.impl.ExportEntityHelperBase#finish(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void finish(XSSFWorkbook book) {
		sheet.setColumnWidth(0, 768);
		sheet.setColumnWidth(1, maxLabelNameLength * 512);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 20 * 256);
	}
}
