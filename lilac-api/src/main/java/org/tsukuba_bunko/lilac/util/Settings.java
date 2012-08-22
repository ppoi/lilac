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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class Settings {

	private Properties properties = new Properties();;

	public Settings() {
		InputStream source = getSourceStream();
		try {
			loadSettings(source);
		}
		finally {
			InputStreamUtil.closeSilently(source);
		}
	}

	public String string(String key) {
		return string(key, null);
	}

	public String string(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if(value == null) {
			return defaultValue;
		}
		else {
			return value;
		}
	}

	@SuppressWarnings("resource")
	private InputStream getSourceStream() {
		InputStream source = null;
		String sourceFilePath = System.getProperty("lilac.settings");
		if(StringUtil.isNotBlank(sourceFilePath)) {
			File sourceFile = new File(sourceFilePath);
			if(sourceFile.isFile()) {
				try {
					source = new FileInputStream(sourceFile);
				}
				catch(IOException ioe) {
					throw new IORuntimeException(ioe);
				}
			}
		}

		if(source == null) {
			source = ResourceUtil.getResourceAsStream("settings.properties");
		}

		return source;
	}

	private void loadSettings(InputStream source) {
		try {
			properties.load(source);
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
	}

}
