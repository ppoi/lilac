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
 */
package org.tsukuba_bunko.lilac.util;

import static org.seasar.framework.unit.S2Assert.*;

import org.junit.Test;


/**
 * {@link DigestUtil} のテストケース
 * @author ppoi
 * @version 2012.04
 */
public class DigestUtilTest {

	@Test
	public void digestResultSame() throws Exception {
		String text = "!$'FSAS123123'SE+*";
		String result1 = DigestUtil.digestText(text);
		String result2 = DigestUtil.digestText(text);
		assertEquals(result1, result2);
	}

	@Test
	public void digestEmptyText() {
		String text = "";
		String result1 = DigestUtil.digestText(text);
		assertEquals("", result1);
	}

	@Test
	public void digestNullText() {
		String text = null;
		String result1 = DigestUtil.digestText(text);
		assertNull(result1);
	}
}
