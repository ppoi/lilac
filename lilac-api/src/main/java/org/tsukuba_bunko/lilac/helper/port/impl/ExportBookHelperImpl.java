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
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.Book;
import org.tsukuba_bunko.lilac.helper.port.ExportDataHelper;


/**
 *　蔵書情報をエクスポートする {@link ExportDataHelper} 実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportBookHelperImpl extends ExportDataHelperBase<Book> {

	private XSSFCellStyle flagCellStyle;
	private XSSFCellStyle idCellStyle;
	private XSSFCellStyle commonCellStyle;
	private XSSFCellStyle dateCellStyle;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#getSheetName()
	 */
	@Override
	protected String getSheetName() {
		return "蔵書";
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
		XSSFFont idFont = book.createFont();
		idFont.setFontHeightInPoints((short)9);
		idCellStyle.setFont(idFont);

		commonCellStyle = book.createCellStyle();
		commonCellStyle.setAlignment(HorizontalAlignment.LEFT);
		commonCellStyle.setVerticalAlignment(VerticalAlignment.TOP);

		dateCellStyle = book.createCellStyle();
		dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
		dateCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#processHeaderRow(org.apache.poi.xssf.usermodel.XSSFRow)
	 */
	@Override
	protected void processHeaderRow(XSSFRow row) {
		createHeaderCell(row, 0, "S");
		createHeaderCell(row, 1, "ID");
		createHeaderCell(row, 2, "ISBN");
		createHeaderCell(row, 3, "タイトル");
		createHeaderCell(row, 4, "書棚");
		createHeaderCell(row, 5, "購入日");
		createHeaderCell(row, 6, "購入店舗");
		createHeaderCell(row, 7, "備考");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#buildQuery()
	 */
	@Override
	protected AutoSelect<Book> buildQuery() {
		return jdbcManager.from(Book.class)
				.leftOuterJoin("bibliography")
				.leftOuterJoin("location")
				.where(new SimpleWhere()
					.eq("owner", getCurrentSessionUser())
				).orderBy("id");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#finish(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void finish(XSSFWorkbook book) {
		sheet.setColumnWidth(0, 768);
		sheet.setColumnWidth(1, 20);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 48 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 16 * 256);
		sheet.setColumnWidth(6, 16 * 256);
		sheet.setColumnWidth(7, 20 * 256);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#processRow(java.lang.Object, org.apache.poi.xssf.usermodel.XSSFRow, int)
	 */
	@Override
	protected void processRow(Book entity, XSSFRow row, int index) {
		//更新フラグ
		setCellValue(createCell(row, 0, flagCellStyle), "U");
		//ID
		setCellValue(createCell(row, 1, idCellStyle), entity.id);
		//ISBN
		setCellValue(createCell(row, 2, commonCellStyle), entity.bibliography.isbn);
		//タイトル
		setCellValue(createCell(row, 3, commonCellStyle), entity.bibliography.title
				+ (StringUtil.isNotBlank(entity.bibliography.subtitle) ? " - " + entity.bibliography.subtitle : ""));
		//書棚
		String bookshelf = null;
		if(entity.locationId != null) {
			bookshelf = "(" + entity.locationId + ")" + entity.location.label;
		}
		setCellValue(createCell(row, 4, commonCellStyle), bookshelf);
		//購入日
		setCellValue(createCell(row, 5, dateCellStyle), entity.acquisitionDate);
		//購入店舗
		setCellValue(createCell(row, 6, commonCellStyle), entity.purchaseShop);
		//備考
		setCellValue(createCell(row, 7, commonCellStyle), entity.note);
	}
}
