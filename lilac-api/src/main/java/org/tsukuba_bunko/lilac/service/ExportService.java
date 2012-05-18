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
package org.tsukuba_bunko.lilac.service;

import java.io.OutputStream;


/**
 * Lilac DBの内容をExcel(OOXML)形式でエクスポートします。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public interface ExportService {

	/**
	 * Lilac DBの全内容をExcel(OOXML)形式でエクスポートします。
	 * @param target
	 */
	public void exportAll(OutputStream target);
}
