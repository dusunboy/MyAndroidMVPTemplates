package $Package.core.fuction;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class FileUtil {
	/**
	 * 读取表情配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 判断SD是否存在
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 创建根目录
	 * 
	 * @param path
	 *            目录路径
	 */
	public static void createDirFile(String path) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException("Unable to create path");
			}
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 *            文件路径
	 * @return 创建的文件
	 */
	public static File createNewFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return file;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            文件夹的路径
	 */
	public static void delFolder(String folderPath) throws IOException {
		delAllFile(folderPath);
		String filePath = folderPath;
		filePath = filePath.toString();
		File myFilePath = new File(filePath);
		if (!myFilePath.delete()) {
			throw new IOException("Unable to delete file");
		}
		System.out.println("删除文件夹成功");
	}

	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @param path
	 *            文件的路径
	 */
	public static void delAllFile(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("文件不存在");
			return;
		}
		if (!file.isDirectory()) {
			System.out.println("路径不是目录");
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				if (!temp.delete()) {
					throw new IOException("Unable to delete file");
				}
				System.out.println("删除文件成功");
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param path
	 */
	public static void delFile(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("文件不存在");
			return;
		}
		if (file.isFile()) {
			if (!file.delete()) {
				throw new IOException("Unable to delete file");
			}
			System.out.println("删除文件成功");
		} else {
			System.out.println("路径不是文件");
		}
	}

	/**
	 * 获取文件的Uri
	 * 
	 * @param path
	 *            文件的路径
	 * @return
	 */
	public static Uri getUriFromFile(String path) {
		File file = new File(path);
		return Uri.fromFile(file);
	}

	/**
	 * 换算文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "未知大小";
		if (size < 1024) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 根据byte数组，生成文件
	 *
	 * @param bytes
	 * @param filePath
	 * @param fileName
	 */
	public static File getFile(byte[] bytes, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
				if (!dir.mkdirs()) {
					throw new IOException("Unable to create path");
				}
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 */
	public static void copyFile(String oldPath, String newPath) throws Exception {
		int byteRead = 0;
		File oldFile = new File(oldPath);
		if (oldFile.exists()) { //文件存在时
			InputStream inStream = new FileInputStream(oldPath); //读入原文件
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			while ( (byteRead = inStream.read(buffer)) != -1) {
//				byteSum += byteRead; //字节数 文件大小
				fs.write(buffer, 0, byteRead);
			}
			inStream.close();
			fs.close();
		}
	}

	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 */
	public static void copyFolder(String oldPath, String newPath) throws Exception {

		(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
		File oldFile = new File(oldPath);
		String[] file = oldFile.list();
		File temp = null;
		for (int i = 0; i < file.length; i++) {
			if (oldPath.endsWith(File.separator)) {
				temp = new File(oldPath + file[i]);
			} else {
				temp = new File(oldPath + File.separator + file[i]);
			}
			if (temp.isFile()) {
				FileInputStream input = new FileInputStream(temp);
				FileOutputStream output = new FileOutputStream(newPath + "/" +
						(temp.getName()).toString());
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
			}
			if (temp.isDirectory()) {//如果是子文件夹
				copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
			}
		}
	}


}
