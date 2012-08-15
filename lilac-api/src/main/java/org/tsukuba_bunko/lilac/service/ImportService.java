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

import java.io.InputStream;
import java.util.List;

import org.tsukuba_bunko.lilac.entity.ImportFile;


/**
 * Excel(OOXML)形式のデータをLilac DBにインポートします。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public interface ImportService {

	/**
	 * Excel(OOXML)形式のデータをLilac DBにインポートします。
	 * @param fileId インポートファイルID
	 */
	public void importData(int fileId);

	/**
	 * 現在のセッションユーザがアップロードしたインポートファイルの一覧を取得します。
	 * @return 現在のセッションユーザがアップロードしたインポートファイルの一覧。未ログインの場合はnull
	 */
	public List<ImportFile> list();

	/**
	 * インポートファイルをアップロードします。
	 * @param fileName アップロードファイル名
	 * @param source インポートファイルの読み込み元ストリーム
	 * @return インポートファイル情報
	 */
	public ImportFileDescriptor upload(String fileName, InputStream source);

	/**
	 * インポートをキャンセルし、アップロードしたインポートファイルを削除します。
	 * @param fileId インポートファイルID
	 */
	public void cancel(int fileId);
}
