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

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.helper.port.ImportDataHelper;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class ImportDataHelperBase implements ImportDataHelper {

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.ImportDataHelper#importData(org.apache.poi.xssf.usermodel.XSSFSheet)
	 */
	@Override
	public void importData(XSSFSheet sheet) {
		List<String> properties = getProperties(sheet);
		int lastRowNumber = sheet.getLastRowNum();
		for(int i = 1; i <= lastRowNumber; ++i) {
			XSSFRow row = sheet.getRow(i);
			Map<String, String> record = readRecord(row, properties);
			if(record == null) {
				continue;
			}
			String status = record.get("status");
			if("U".equals(status)) {
				//変更なし
			}
			else if(status == null || "C".equals(status)) {
				insertRecord(record);
			}
			else if("M".equals(status)) {
				updateRecord(record);
			}
			else if("D".equals(status)) {
				deleteRecord(record);
			}
		}
	}

	private List<String> getProperties(XSSFSheet sheet) {
		Map<String, String> propertyNameMap = getPropertyNameMap();
		List<String> properties = new java.util.ArrayList<String>();
		XSSFRow headerRow = sheet.getRow(0);
		for(Cell cell : headerRow) {
			String header = getCellValue(cell);
			String propertyName = propertyNameMap.get(header);
			if(propertyName != null) {
				properties.add(propertyName);
			}
			else {
				properties.add(header);
			}
		}
		return properties;
	}

	private Map<String, String> readRecord(XSSFRow row, List<String> properties) {
		Map<String, String> record = new java.util.HashMap<String, String>();
		int propertyCount = properties.size();
		boolean emptyRecord = true;
		for(int i = 0; i < propertyCount; ++i) {
			XSSFCell cell = row.getCell(i);
			if(cell != null) {
				String value = getCellValue(cell);
				if(!StringUtil.isBlank(value)) {
					emptyRecord = false;
				}
				record.put(properties.get(i), getCellValue(cell));
			}
		}
		if(emptyRecord) {
			return null;
		}
		else {
			return record;
		}
	}

	private String getCellValue(Cell cell) {
		switch(cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case Cell.CELL_TYPE_NUMERIC:
				return String.valueOf(Math.round(cell.getNumericCellValue()));
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
		}
		return null;
	}

	protected abstract Map<String, String> getPropertyNameMap();

	protected abstract void insertRecord(Map<String, String> record);
	protected abstract void updateRecord(Map<String, String> record);
	protected abstract void deleteRecord(Map<String, String> record);
}
