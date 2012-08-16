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
package org.tsukuba_bunko.lilac.helper.port.impl;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.tsukuba_bunko.lilac.entity.Bookshelf;
import org.tsukuba_bunko.lilac.helper.port.ExportDataHelper;


/**
 * レーベル情報をエクスポートする {@link ExportDataHelper} 実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportBookshelfHelperImpl extends ExportDataHelperBase<Bookshelf> {

	private XSSFCellStyle flagCellStyle;
	private XSSFCellStyle idCellStyle;
	private XSSFCellStyle noteCellStyle;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#getSheetName()
	 */
	@Override
	protected String getSheetName() {
		return "書棚";
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#prepare(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void prepare(XSSFWorkbook book) {
		flagCellStyle = book.createCellStyle();
		flagCellStyle.setAlignment(HorizontalAlignment.CENTER);
		flagCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		idCellStyle = book.createCellStyle();

		noteCellStyle = book.createCellStyle();
		XSSFFont noteFont = book.createFont();
		noteFont.setFontHeightInPoints((short)9);
		noteCellStyle.setFont(noteFont);
		noteCellStyle.setAlignment(HorizontalAlignment.LEFT);
		noteCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		noteCellStyle.setWrapText(true);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#buildQuery()
	 */
	@Override
	protected AutoSelect<Bookshelf> buildQuery() {
		return jdbcManager.from(Bookshelf.class)
					.where(new SimpleWhere()
						.eq("owner", getCurrentSessionUser())
					).orderBy("label ASC");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#processHeaderRow(org.apache.poi.xssf.usermodel.XSSFRow)
	 */
	@Override
	protected void processHeaderRow(XSSFRow row) {
		createHeaderCell(row, 0, "S");
		createHeaderCell(row, 1, "ID");
		createHeaderCell(row, 2, "ラベル");
		createHeaderCell(row, 3, "備考");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#processRow(java.lang.Object, org.apache.poi.xssf.usermodel.XSSFRow, int)
	 */
	@Override
	protected void processRow(Bookshelf entity, XSSFRow row, int index) {
		//更新フラグ
		setCellValue(createCell(row, 0, flagCellStyle), "U");
		//ラベル
		setCellValue(createCell(row, 1, idCellStyle), entity.id);
		//ラベル
		setCellValue(createCell(row, 2, null), entity.label);
		//備考
		setCellValue(createCell(row, 3, noteCellStyle), entity.note);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#finish(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void finish(XSSFWorkbook book) {
		sheet.setColumnWidth(0, 768);
		sheet.setColumnWidth(1, 0);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 20 * 256);
	}
}
