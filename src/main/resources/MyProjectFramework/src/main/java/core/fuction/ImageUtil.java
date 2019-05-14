package $Package.core.fuction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import $Package.core.config.BaseConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图像处理工具类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class ImageUtil {
	/**
	 * 默认
	 */
	private static final int DEFAULT = 0;
	/**
	 * 滤镜
	 */
	private static final int LOMO = 1;
	/**
	 * 怀旧
	 */
	private static final int REMINISCENCE = 2;
	/**
	 * 光照
	 */
	private static final int LIGHTING = 3;
	/**
	 * 模糊
	 */
	private static final int BLUR = 5;
	/**
	 * 柔化
	 */
	private static final int SOFTEN = 6;
	/**
	 * 锐化
	 */
	private static final int SHARPEN = 7;
	/**
	 * 底片
	 */
	private static final int NEGATIVE = 8;

	/**
	 * 计算图片的缩放值
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 根据路径获得图片并压缩返回bitmap
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}


	/**
	 * 压缩图片
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static File compressImage(String path) throws Exception {
		File f = new File(path);
		Bitmap bm = getSmallBitmap(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		File compressFile = new File(getAlbumDir(), f.getName());
		FileOutputStream fos = new FileOutputStream(compressFile);

		int options = 80;
		// 如果大于80kb则再次压缩
//			while (baos.toByteArray().length / 1024 > 80 && options != 10) {
		//压缩到
		while (baos.toByteArray().length / (float) 1024 > (float) 2048) {
			// 清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		fos.write(baos.toByteArray());
		fos.close();
		baos.close();
//			bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);
//			String compressPath = PhotoUtil.savePhotoToSDCard(bm);
//			File compressFile = new File(compressPath);
		return compressFile;
	}

	/**
	 * 压缩图片
	 * @param path
	 * @param kb
	 * @return
	 * @throws Exception
	 */
	public static File compressImage(String path, int kb) throws Exception {
		File f = new File(path);
		Bitmap bm = getSmallBitmap(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		File compressFile = new File(getAlbumDir(), f.getName());
		FileOutputStream fos = new FileOutputStream(compressFile);

		int options = 100;
		// 如果大于80kb则再次压缩
//			while (baos.toByteArray().length / 1024 > 80 && options != 10) {
		while (baos.toByteArray().length / 1024 > kb) {
			// 清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 5;
		}
		fos.write(baos.toByteArray());
		fos.close();
		baos.close();
//			bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);
//			String compressPath = PhotoUtil.savePhotoToSDCard(bm);
//			File compressFile = new File(compressPath);
		return compressFile;
	}


	public static Bitmap compressImage(Bitmap bitmap) {
		//图片允许最大空间   单位：KB
		float maxSize = 500;
		//将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		//将字节换成KB
		float mid = b.length / (float) 1024;
		//判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			//获取bitmap大小 是允许最大大小的多少倍
			float i = mid / maxSize;
			//开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
					bitmap.getHeight() / Math.sqrt(i));
		}
		return bitmap;
	}

	/***
	 * 图片的缩放方法
	 *
	 * @param bgImage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgImage, double newWidth,
								   double newHeight) {
		// 获取这个图片的宽和高
		float width = bgImage.getWidth();
		float height = bgImage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgImage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 获取保存图片的目录
	 *
	 * @return
	 */
	public static File getAlbumDir() throws IOException {
//		String path = android.os.Environment.getExternalStorageDirectory() 
//				+ Constant.DIRECTORY + "";
		File dir = new File(SPUtil.getString(BaseConstant.EXTERNAL_STORAGE_DIRECTORY) + "/DCIM/Camera/");
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException("Unable to create path");
			}
		}
		return dir;
	}

	/**
	 * Bitmap to Drawable
	 *
	 * @param bitmap
	 * @return
	 */
	public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		// 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
		return bd.getCurrent();
	}

	/**
	 * 从view 得到图片
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	/**
	 * 根据滤镜类型获取图片
	 *
	 * @param type
	 *            滤镜类型
	 * @param bmp
	 *            图片
	 * @return
	 */
	public static Bitmap getFilter(int type, Bitmap bmp) {
		if (type == DEFAULT) {
			return bmp;
		} else if (type == LOMO) {
			return lomo(bmp);
		} else if (type == REMINISCENCE) {
			return reminiscence(bmp);
		} else if (type == LIGHTING) {
			return lighting(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2);
		} else if (type == BLUR) {
			return blur(bmp);
		} else if (type == SOFTEN) {
			return blurImageAmeliorate(bmp);
		} else if (type == SHARPEN) {
			return sharpenImageAmeliorate(bmp);
		} else if (type == NEGATIVE) {
			return negative(bmp);
		}
		return bmp;
	}

	/**
	 * 滤镜效果--LOMO
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap lomo(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int dst[] = new int[width * height];
		bitmap.getPixels(dst, 0, width, 0, 0, width, height);

		int ratio = width > height ? height * 32768 / width : width * 32768
				/ height;
		int cx = width >> 1;
		int cy = height >> 1;
		int max = cx * cx + cy * cy;
		int min = (int) (max * (1 - 0.8f));
		int diff = max - min;

		int ri, gi, bi;
		int dx, dy, distSq, v;

		int R, G, B;

		int value;
		int pos, pixColor;
		int newR, newG, newB;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixColor = dst[pos];
				R = Color.red(pixColor);
				G = Color.green(pixColor);
				B = Color.blue(pixColor);

				value = R < 128 ? R : 256 - R;
				newR = (value * value * value) / 64 / 256;
				newR = (R < 128 ? newR : 255 - newR);

				value = G < 128 ? G : 256 - G;
				newG = (value * value) / 128;
				newG = (G < 128 ? newG : 255 - newG);

				newB = B / 2 + 0x25;

				// ==========边缘黑暗==============//
				dx = cx - x;
				dy = cy - y;
				if (width > height)
					dx = (dx * ratio) >> 15;
				else
					dy = (dy * ratio) >> 15;

				distSq = dx * dx + dy * dy;
				if (distSq > min) {
					v = ((max - distSq) << 8) / diff;
					v *= v;

					ri = (int) (newR * v) >> 16;
					gi = (int) (newG * v) >> 16;
					bi = (int) (newB * v) >> 16;

					newR = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
					newG = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
					newB = bi > 255 ? 255 : (bi < 0 ? 0 : bi);
				}
				// ==========边缘黑暗end==============//

				dst[pos] = Color.rgb(newR, newG, newB);
			}
		}

		Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return acrossFlushBitmap;
	}

	/**
	 * 怀旧
	 * @param bmp
	 * @return
	 */
	public static Bitmap reminiscence(Bitmap bmp) {
//		if (ImageCache.get("oldRemeber") != null) {
//			return ImageCache.get("oldRemeber");
//		}
		// 速度测试
//		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255
						: newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		long end = System.currentTimeMillis();
//		ImageCache.put("oldRemeber", bitmap);
		return bitmap;
	}

	/**
	 * 光照效果
	 *
	 * @param bmp
	 *            光照中心x坐标
	 * @param centerX
	 *            光照中心要坐标
	 * @param centerY
	 * @return
	 */
	public static Bitmap lighting(Bitmap bmp, int centerX, int centerY) {
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 光照强度 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 素描
	 * @param bmp
	 * @return
	 */
	public static Bitmap sketch(Bitmap bmp) {
//		if (ImageCache.get("sketch") != null) {
//			return ImageCache.get("sketch");
//		}
//		long start = System.currentTimeMillis();
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();

		int pos, row, col, clr;
		int[] pixSrc = new int[width * height];
		int[] pixNvt = new int[width * height];
		// 先对图象的像素处理成灰度颜色后再取反
		bmp.getPixels(pixSrc, 0, width, 0, 0, width, height);

		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;
				pixSrc[pos] = (Color.red(pixSrc[pos]) + Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
				pixNvt[pos] = 255 - pixSrc[pos];
			}
		}


		gaussGray(pixNvt, 5.0, 5.0, width, height);

		// 灰度颜色和模糊后像素进行差值运算
		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;

				clr = pixSrc[pos] << 8;
				clr /= 256 - pixNvt[pos];
				clr = Math.min(clr, 255);

				pixSrc[pos] = Color.rgb(clr, clr, clr);
			}
		}
		bmp.setPixels(pixSrc, 0, width, 0, 0, width, height);
//		long end = System.currentTimeMillis();
//		Log.d("blurImageAmeliorate", "used time=" + (end - start));
//		ImageCache.put("sketch", bmp);
		return bmp;
	}

	/**
	 * 模糊效果
	 * @param bmp
	 * @return
	 */
	public static Bitmap blur(Bitmap bmp) {
//		if (ImageCache.get("blurImage") != null) {
//			return ImageCache.get("blurImage");
//		}
//		// 速度测试
//		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int newColor = 0;

		int[][] colors = new int[9][3];
		for (int i = 1, length = width - 1; i < length; i++) {
			for (int k = 1, len = height - 1; k < len; k++) {
				for (int m = 0; m < 9; m++) {
					int s = 0;
					int p = 0;
					switch (m) {
						case 0:
							s = i - 1;
							p = k - 1;
							break;
						case 1:
							s = i;
							p = k - 1;
							break;
						case 2:
							s = i + 1;
							p = k - 1;
							break;
						case 3:
							s = i + 1;
							p = k;
							break;
						case 4:
							s = i + 1;
							p = k + 1;
							break;
						case 5:
							s = i;
							p = k + 1;
							break;
						case 6:
							s = i - 1;
							p = k + 1;
							break;
						case 7:
							s = i - 1;
							p = k;
							break;
						case 8:
							s = i;
							p = k;
					}
					pixColor = bmp.getPixel(s, p);
					colors[m][0] = Color.red(pixColor);
					colors[m][1] = Color.green(pixColor);
					colors[m][2] = Color.blue(pixColor);
				}

				for (int m = 0; m < 9; m++) {
					newR += colors[m][0];
					newG += colors[m][1];
					newB += colors[m][2];
				}

				newR = (int) (newR / 9F);
				newG = (int) (newG / 9F);
				newB = (int) (newB / 9F);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				newColor = Color.argb(255, newR, newG, newB);
				bitmap.setPixel(i, k, newColor);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
//		long end = System.currentTimeMillis();
//		Log.e("blurImage()", "used time=" + (end - start));
//		ImageCache.put("blurImage", bitmap);
		return bitmap;
	}

	/**
	 * 柔化效果(高斯模糊)(优化后比上面快三倍)
	 *
	 * @param bmp
	 * @return
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
//		if (ImageCache.get("blurImageAmeliorate") != null) {
//			return ImageCache.get("blurImageAmeliorate");
//		}
//		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 16; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		long end = System.currentTimeMillis();
//		Log.d("blurImageAmeliorate", "used time=" + (end - start));
//		ImageCache.put("blurImageAmeliorate", bitmap);
		return bitmap;
	}

	/**
	 * 锐化（拉普拉斯变换）
	 *
	 * @param bmp
	 * @return
	 */
	public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
//		if (ImageCache.get("sharpenImageAmeliorate") != null) {
//			return ImageCache.get("sharpenImageAmeliorate");
//		}
//		long start = System.currentTimeMillis();
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		float alpha = 0.3F;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + n) * width + k + m];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		long end = System.currentTimeMillis();
//		Log.e("sharpenImageAmeliorate", "used time=" + (end - start));
//		ImageCache.put("sharpenImageAmeliorate", bitmap);
		return bitmap;
	}

	/**
	 * 底片效果
	 * @param bmp
	 * @return
	 */
	public static Bitmap negative(Bitmap bmp) {
		// RGBA的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 对取反的像素进行高斯模糊, 强度可以设置，暂定为5.0
	 * @param psrc
	 * @param horz
	 * @param vert
	 * @param width
	 * @param height
	 * @return
	 */
	private static int gaussGray(int[] psrc, double horz, double vert, int width, int height) {
		int[] dst, src;
		double[] n_p, n_m, d_p, d_m, bd_p, bd_m;
		double[] val_p, val_m;
		int i, j, t, k, row, col, terms;
		int[] initial_p, initial_m;
		double std_dev;
		int row_stride = width;
		int max_len = Math.max(width, height);
		int sp_p_idx, sp_m_idx, vp_idx, vm_idx;

		val_p = new double[max_len];
		val_m = new double[max_len];

		n_p = new double[5];
		n_m = new double[5];
		d_p = new double[5];
		d_m = new double[5];
		bd_p = new double[5];
		bd_m = new double[5];

		src = new int[max_len];
		dst = new int[max_len];

		initial_p = new int[4];
		initial_m = new int[4];

		// 垂直方向
		if (vert > 0.0) {
			vert = Math.abs(vert) + 1.0;
			std_dev = Math.sqrt(-(vert * vert) / (2 * Math.log(1.0 / 255.0)));


			findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);

			for (col = 0; col < width; col++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < height; t++) {
					src[t] = psrc[t * row_stride + col];
				}

				sp_p_idx = 0;
				sp_m_idx = height - 1;
				vp_idx = 0;
				vm_idx = height - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[height - 1];

				for (row = 0; row < height; row++) {
					terms = (row < 4) ? row : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i] * val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i] * val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, height);

				for (t = 0; t < height; t++) {
					psrc[t * row_stride + col] = dst[t];
				}
			}
		}

		// 水平方向
		if (horz > 0.0) {
			horz = Math.abs(horz) + 1.0;
			if (Math.abs(horz - vert) >= .0000001) {
				std_dev = Math.sqrt(-(horz * horz) / (2 * Math.log(1.0 / 255.0)));
				findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
			}
			for (row = 0; row < height; row++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < width; t++) {
					src[t] = psrc[row * row_stride + t];
				}

				sp_p_idx = 0;
				sp_m_idx = width - 1;
				vp_idx = 0;
				vm_idx = width - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[width - 1];

				for (col = 0; col < width; col++) {
					terms = (col < 4) ? col : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i] * val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i] * val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, width);

				for (t = 0; t < width; t++) {
					psrc[row * row_stride + t] = dst[t];
				}
			}
		}

		return 0;
	}

	/**
	 * 初试化常量
	 * @param n_p
	 * @param n_m
	 * @param d_p
	 * @param d_m
	 * @param bd_p
	 * @param bd_m
	 * @param std_dev
	 */
	private static void findConstants(double[] n_p, double[] n_m, double[] d_p, double[] d_m, double[] bd_p,
									  double[] bd_m, double std_dev) {
		double div = Math.sqrt(2 * Math.PI) * std_dev;
		double x0 = -1.783 / std_dev;
		double x1 = -1.723 / std_dev;
		double x2 = 0.6318 / std_dev;
		double x3 = 1.997 / std_dev;
		double x4 = 1.6803 / div;
		double x5 = 3.735 / div;
		double x6 = -0.6803 / div;
		double x7 = -0.2598 / div;
		int i;

		n_p[0] = x4 + x6;
		n_p[1] = (Math.exp(x1) * (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math.exp(x0)
				* (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
		n_p[2] = (2
				* Math.exp(x0 + x1)
				* ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3) * Math.sin(x2) - x7 * Math.cos(x2)
				* Math.sin(x3)) + x6 * Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
		n_p[3] = (Math.exp(x1 + 2 * x0) * (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2 * x1)
				* (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
		n_p[4] = 0.0;

		d_p[0] = 0.0;
		d_p[1] = -2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0) * Math.cos(x2);
		d_p[2] = 4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1) + Math.exp(2 * x1) + Math.exp(2 * x0);
		d_p[3] = -2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3) * Math.exp(x1 + 2 * x0);
		d_p[4] = Math.exp(2 * x0 + 2 * x1);

		for (i = 0; i <= 4; i++) {
			d_m[i] = d_p[i];
		}

		n_m[0] = 0.0;
		for (i = 1; i <= 4; i++) {
			n_m[i] = n_p[i] - d_p[i] * n_p[0];
		}

		double sum_n_p, sum_n_m, sum_d;
		double a, b;

		sum_n_p = 0.0;
		sum_n_m = 0.0;
		sum_d = 0.0;

		for (i = 0; i <= 4; i++) {
			sum_n_p += n_p[i];
			sum_n_m += n_m[i];
			sum_d += d_p[i];
		}

		a = sum_n_p / (1.0 + sum_d);
		b = sum_n_m / (1.0 + sum_d);

		for (i = 0; i <= 4; i++) {
			bd_p[i] = d_p[i] * a;
			bd_m[i] = d_m[i] * b;
		}
	}

	private static void transferGaussPixels(double[] src1, double[] src2, int[] dest, int bytes, int width) {
		int i, j, k, b;
		int bend = bytes * width;
		double sum;

		i = j = k = 0;
		for (b = 0; b < bend; b++) {
			sum = src1[i++] + src2[j++];

			if (sum > 255)
				sum = 255;
			else if (sum < 0)
				sum = 0;

			dest[k++] = (int) sum;
		}
	}

	/**
	 * 图片旋转
	 *
	 * @param bmp
	 * @param degree
	 * @return
	 */
	public static Bitmap postRotateBitamp(Bitmap bmp, float degree) {
		// 获得Bitmap的高和宽
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		// 产生resize后的Bitmap对象
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
		return resizeBmp;
	}


	/**
	 * Bitmap转byte[]
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * byte[]转Bitmap
	 * @param bytes
	 * @return
	 */
	public static Bitmap bytes2Bitmap(byte[] bytes) {
		if (bytes.length != 0) {
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} else {
			return null;
		}
	}

	/**
	 * 制作图像倒影
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionBitmap(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 旋转Bitmap
	 * @param bitmap
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**
	 * 保存图片
	 * @param file
	 * @param bitmap
     * @return
     */
	public static void saveImage(File file, Bitmap bitmap) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将Bitmap转换成Base64字符串
	 * @param bitmap
	 * @param quality 图像质量 100表示不压缩
	 * @return
	 */
	public static String bitmap2StrByBase64(Bitmap bitmap, int quality){
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);//参数100表示不压缩
		byte[] bytes=bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	/**
	 * 将Base64字符串转换成Bitmap
	 * @param strBase64
	 * @return
	 */
	public static Bitmap strBase642Bitmap(String strBase64) {
		byte[] bytes = Base64.decode(strBase64, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
