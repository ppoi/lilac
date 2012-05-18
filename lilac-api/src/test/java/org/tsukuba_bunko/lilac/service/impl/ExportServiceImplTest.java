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
package org.tsukuba_bunko.lilac.service.impl;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;

import static org.seasar.framework.unit.S2Assert.*;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@RunWith(Seasar2.class)
public class ExportServiceImplTest {

	@Resource
	private ExportServiceImpl service;

	private File outputDir = new File("./target/exportut").getAbsoluteFile();

	@Before
	public void setUp() {
		if(!outputDir.exists()) {
			assertTrue("mkdir " + outputDir, outputDir.mkdirs());
		}
		assertTrue(outputDir.toString(), outputDir.isDirectory());
	}

	@Test
	public void exportAll() throws Exception {
		File targetFile = new File(outputDir, "exportAll.xlsx");
		FileOutputStream target = new FileOutputStream(targetFile);
		service.exportAll(target);
		target.flush();
		target.close();
	}
}
