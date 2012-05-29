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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.IterationCallback;
import org.seasar.extension.jdbc.IterationContext;
import org.seasar.extension.jdbc.JdbcManager;
import org.tsukuba_bunko.lilac.helper.ExportEntityHelper;


/**
 * {@link ExportEntityHelper} 共通基底クラス。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class ExportEntityHelperBase<ENTITY> implements ExportEntityHelper, IterationCallback<ENTITY, Integer> {

	@Resource
	public JdbcManager jdbcManager;

	protected XSSFSheet sheet;
	
	protected XSSFCellStyle headerCellStyle;

	private int rowCount;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.ExportEntityHelper#exportAll(org.apache.poi.xssf.usermodel.XSSFWorkbook)
	 */
	@Override
	public void exportAll(XSSFWorkbook book) {
		sheet = book.createSheet(getSheetName());

		XSSFFont font = book.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.index);

		headerCellStyle = book.createCellStyle();
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)256, (byte)0, (byte)112, (byte)192}));
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setFont(font);

		prepare(book);

		XSSFRow row = sheet.createRow(rowCount++);
		processHeaderRow(row);

		buildQuery().iterate(this);
		finish(book);
	}

	/**
	 * @see org.seasar.extension.jdbc.IterationCallback#iterate(java.lang.Object, org.seasar.extension.jdbc.IterationContext)
	 */
	@Override
	public Integer iterate(ENTITY entity, IterationContext context) {
		XSSFRow row = sheet.createRow(rowCount);
		processRow(entity, row, rowCount);
		return ++rowCount;
	};

	/**
	 * シート名を取得します
	 * @return シート名
	 */
	protected abstract String getSheetName();

	/**
	 * 前処理を実行します。
	 * @param book OOXMLワークブック
	 */
	protected abstract void prepare(XSSFWorkbook book);

	/**
	 * クエリを構築します。
	 * @return S2JDBCクエリ
	 */
	protected abstract AutoSelect<ENTITY> buildQuery();

	/**
	 * エンティティを処理します。
	 * @param entity エンティティ
	 * @param row　行
	 * @param index 行インデックス
	 */
	protected abstract void processRow(ENTITY entity, XSSFRow row, int index);

	protected abstract void processHeaderRow(XSSFRow row);

	/**
	 * 後処理を実行します。
	 * @param book OOXMLワークブック
	 */
	protected abstract void finish(XSSFWorkbook book);

	/**
	 * 
	 */
	protected XSSFCell createHeaderCell(XSSFRow row, int index, String label) {
		XSSFCell cell = row.createCell(index);
		cell.setCellStyle(headerCellStyle);
		setCellValue(cell, label);
		return cell;
	}

	/**
	 * セルを作成します。
	 * @param row 行
	 * @param index セルインデックス
	 * @param style　セルスタイル
	 * @return セル
	 */
	protected XSSFCell createCell(XSSFRow row, int index, XSSFCellStyle style) {
		XSSFCell cell = row.createCell(index);
		if(style != null) {
			cell.setCellStyle(style);
		}
		return cell;
	}

	/**
	 * セルに値を設定します。
	 * @param cell　セル
	 * @param value 値
	 * @return セル
	 */
	protected XSSFCell setCellValue(XSSFCell cell, String value) {
		if(value != null) {
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(value);
		}
		else {
			cell.setCellType(XSSFCell.CELL_TYPE_BLANK);
		}
		return cell;
	}

	/**
	 * セルに値を設定します。
	 * @param cell　セル
	 * @param value 値
	 * @return セル
	 */
	protected XSSFCell setCellValue(XSSFCell cell, Integer value) {
		if(value != null) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(value);
		}
		else {
			cell.setCellType(XSSFCell.CELL_TYPE_BLANK);
		}
		return cell;
	}

	/**
	 * セルに値を設定します。
	 * @param cell　セル
	 * @param value 値
	 * @return セル
	 */
	protected XSSFCell setCellValue(XSSFCell cell, Date value) {
		if(value != null) {
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(new SimpleDateFormat("yyyy/MM/dd").format(value));
		}
		else {
			cell.setCellType(XSSFCell.CELL_TYPE_BLANK);
		}
		return cell;
	}
}
