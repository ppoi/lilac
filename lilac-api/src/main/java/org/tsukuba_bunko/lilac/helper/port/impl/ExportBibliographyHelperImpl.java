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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.AutoSelect;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.helper.port.ExportDataHelper;


/**
 * 書籍情報をエクスポートする {@link ExportDataHelper} 実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportBibliographyHelperImpl extends ExportDataHelperBase<Bibliography> {

	private XSSFCellStyle flagCellStyle;
	private XSSFCellStyle idCellStyle;
	private XSSFCellStyle commonCellStyle;
	private XSSFCellStyle authorsCellStyle;
	private XSSFCellStyle dateCellStyle;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#getSheetName()
	 */
	@Override
	protected String getSheetName() {
		return "書誌情報";
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

		commonCellStyle = book.createCellStyle();
		commonCellStyle.setAlignment(HorizontalAlignment.LEFT);
		commonCellStyle.setVerticalAlignment(VerticalAlignment.TOP);

		authorsCellStyle = book.createCellStyle();
		authorsCellStyle.setWrapText(true);
		authorsCellStyle.setAlignment(HorizontalAlignment.LEFT);
		authorsCellStyle.setVerticalAlignment(VerticalAlignment.TOP);

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
		createHeaderCell(row, 3, "レーベル");
		createHeaderCell(row, 4, "タイトル");
		createHeaderCell(row, 5, "サブタイトル");
		createHeaderCell(row, 6, "著者");
		createHeaderCell(row, 7, "出版日");
		createHeaderCell(row, 8, "定価");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#buildQuery()
	 */
	@Override
	protected AutoSelect<Bibliography> buildQuery() {
		return jdbcManager.from(Bibliography.class)
				.innerJoin("authors")
				.innerJoin("authors.author")
				.orderBy("id");
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#finish(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	protected void finish(XSSFWorkbook book) {
		sheet.setColumnWidth(0, 768);
		sheet.setColumnWidth(1, 0);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(4, 32 * 256);
		sheet.setColumnWidth(5, 32 * 256);
		sheet.setColumnWidth(6, 32 * 256);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ExportDataHelperBase#processRow(java.lang.Object, org.apache.poi.xssf.usermodel.XSSFRow, int)
	 */
	@Override
	protected void processRow(Bibliography entity, XSSFRow row, int index) {
		//更新フラグ
		setCellValue(createCell(row, 0, flagCellStyle), "U");
		//ID
		setCellValue(createCell(row, 1, idCellStyle), entity.id);
		//ISBN
		setCellValue(createCell(row, 2, commonCellStyle), entity.isbn);
		//レーベル
		setCellValue(createCell(row, 3, commonCellStyle), entity.label);
		//タイトル
		setCellValue(createCell(row, 4, commonCellStyle), entity.title);
		//サブタイトル
		setCellValue(createCell(row, 5, commonCellStyle), entity.subtitle);
		//著者
		StringBuilder sink = new StringBuilder();
		for(BibAuthor bibauthor : entity.authors) {
			sink.append("(");
			sink.append(bibauthor.author.id);
			sink.append(")");
			sink.append("[");
			sink.append(bibauthor.authorRole);
			sink.append(']');
			sink.append(bibauthor.author.name);
			sink.append("\r\n");
		}
		setCellValue(createCell(row, 6, authorsCellStyle), sink.toString());
		//出版日
		setCellValue(createCell(row, 7, dateCellStyle), entity.publicationDate);
		//定価
		setCellValue(createCell(row, 8, commonCellStyle), entity.price);
	}
}
