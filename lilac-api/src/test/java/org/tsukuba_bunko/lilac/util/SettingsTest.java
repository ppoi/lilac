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
package org.tsukuba_bunko.lilac.util;

import java.io.File;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seasar.framework.unit.TestContext;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.ResourceUtil;

import static org.seasar.framework.unit.S2Assert.*;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class SettingsTest {

	@Resource
	private TestContext context;

	private String originalLilacSettings;

	@Before
	public void prepareResource() {
		File resourceFile = ResourceUtil.getResourceAsFile("settings.properties");
		if(resourceFile != null) {
			File backupFile = new File(resourceFile.getParentFile(), "settings.properties.testbackup");
			assertFalse("backupfile already exists.", backupFile.exists());
			assertTrue("fail to bakcup.", resourceFile.renameTo(backupFile));
		}

		File testResource = ResourceUtil.getResourceAsFile("org/tsukuba_bunko/lilac/util/settings.resource.properties");
		assertNotNull("testdata is missing.", testResource);
		FileUtil.copy(testResource, resourceFile);

		originalLilacSettings = System.getProperty("lilac.settings");
	}

	@After
	public void restoreResource() {
		File resourceFile = ResourceUtil.getResourceAsFile("settings.properties");
		if(resourceFile != null) {
			assertTrue("fail to remove testdata", resourceFile.delete());
		}
		File backupFile = new File(resourceFile.getParentFile(), "settings.properties.testbackup");
		if(backupFile.exists()) {
			assertTrue("fail to restore resource.", backupFile.renameTo(resourceFile));
		}

		if(originalLilacSettings != null) {
			System.setProperty("lilac.settings", originalLilacSettings);
		}
		else {
			System.getProperties().remove("lilac.settings");
		}
	}

	@Test
	public void loadFromResource() {
		System.getProperties().remove("lilac.settings");
		Settings settings = new Settings();
		assertEquals("resource_value1", settings.string("lilac.test.key1"));
		assertEquals("resource_value2", settings.string("lilac.test.key2"));
		assertNull(settings.string("lilac.test.key3"));
	}

	@Test
	public void loadFromFile() {
		File testFile = ResourceUtil.getResourceAsFile("org/tsukuba_bunko/lilac/util/settings.file.properties");
		assertNotNull("testdata is missin.", testFile);
		System.setProperty("lilac.settings", testFile.getPath());
		Settings settings = new Settings();
		assertNull(settings.string("lilac.test.key1"));
		assertEquals("file_value2", settings.string("lilac.test.key2"));
		assertEquals("file_value3", settings.string("lilac.test.key3"));
	}

	@Test
	public void stringWithDefault() {
		System.getProperties().remove("lilac.settings");
		Settings settings = new Settings();
		assertNull(settings.string("lilac.test.key3"));
		assertEquals("miku", settings.string("lilac.test.key3", "miku"));
	}
}
