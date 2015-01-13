package rdc.jim.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 文件操作工具类
 * 
 * @author 橘子哥
 *
 */

public class FileUtil {

	public static final String TAG = "FileUtil";

	/**
	 * 获取SD卡根目录
	 * 
	 * @return
	 */
	public static String getSDDir() {
		// 检查SD卡是否存在
		String sdPath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			sdPath = Environment.getExternalStorageDirectory().getPath();
			Log.d(TAG, "SD exist....." + sdPath);
		} else {
			Log.d(TAG, "SD not exist.........");
		}
		return sdPath;

	}

	public static String getRealFilePath(Context context, Uri uri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null,
				null, null);
		if (cursor.moveToFirst()) {
			;
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		Log.e(TAG, res);
		return res;
	}

	/**
	 * 将文件转换成字节
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getFileByte(File file) {
		if (file != null) {

			try {
				FileInputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
						2048);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, count);
				}
				inputStream.close();
				outputStream.close();
				return outputStream.toByteArray();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将字节数字保存为文件
	 * 
	 * @param buffer
	 *            源字节数组
	 * @param offset
	 *            起始位置
	 * @param path
	 *            保存路径
	 * @param length
	 *            读取长度
	 * @return
	 */
	public static File saveFileByByte(byte[] buffer, int offset, int length,
			String path, String fileName) {
		File rootFile = null;
		File ret = null;
		BufferedOutputStream stream = null;
		try {
			rootFile = new File(path);
			if (!rootFile.exists()) {
				rootFile.mkdir();
			}
			Log.e(path, "test path : " + path + "/" + fileName);
			ret = new File(path + "/" + fileName);
			if (!ret.exists()) {
				ret.createNewFile();
			}
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(buffer, offset, length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
}
