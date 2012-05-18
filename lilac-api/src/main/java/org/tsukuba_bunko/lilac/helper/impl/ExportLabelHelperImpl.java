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

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.IterationCallback;
import org.seasar.extension.jdbc.IterationContext;
import org.seasar.extension.jdbc.JdbcManager;
import org.tsukuba_bunko.lilac.entity.Label;
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;


/**
 * {@link ExportEntityHelper}実装
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ExportLabelHelperImpl implements ExportEntityHelper, IterationCallback<Label, XSSFSheet> {

	@Resource
	public JdbcManager jdbcManager;

	private XSSFSheet sheet;

	private XSSFCellStyle flagCellStyle;

	private XSSFCellStyle noteCellStyle;

	private int rows;

	private int maxLabelNameLength;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.ExportEntityHelper#exportAll(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	public void exportAll(XSSFWorkbook book) {
		sheet = book.createSheet("Label");

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

		jdbcManager.from(Label.class).orderBy("name ASC").iterate(this);

		sheet.setColumnWidth(0, 768);
		sheet.setColumnWidth(1, maxLabelNameLength * 512);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 20 * 256);
	}

	/**
	 * @see org.seasar.extension.jdbc.IterationCallback#iterate(java.lang.Object, org.seasar.extension.jdbc.IterationContext)
	 */
	@Override
	public XSSFSheet iterate(Label label, IterationContext context) {
		XSSFRow row = sheet.createRow(rows);

		XSSFCell flagCell = row.createCell(0);
		flagCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		flagCell.setCellStyle(flagCellStyle);
		flagCell.setCellValue("U");

		XSSFCell nameCell = row.createCell(1);
		nameCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		nameCell.setCellValue(label.name);

		XSSFCell noteCell = row.createCell(2);
		if(label.note != null) {
			noteCell.setCellType(XSSFCell.CELL_TYPE_STRING);
			noteCell.setCellValue(label.note);
		}
		else {
			noteCell.setCellType(XSSFCell.CELL_TYPE_BLANK);
		}
		noteCell.setCellStyle(noteCellStyle);

		XSSFCell websiteCell = row.createCell(3);
		if(label.website != null) {
			websiteCell.setCellType(XSSFCell.CELL_TYPE_STRING);
			websiteCell.setCellValue(label.website);
		}
		else {
			websiteCell.setCellType(XSSFCell.CELL_TYPE_BLANK);
		}
		websiteCell.setCellStyle(noteCellStyle);
		
		if(label.name.length() > maxLabelNameLength) {
			maxLabelNameLength = label.name.length();
		}

		rows++;
		return sheet;
	}
}
