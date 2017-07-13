package $Package.core.fuction;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import $Package.core.model.MediaBean;
import $Package.core.config.BaseConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 拍照相册类
 * Created by Vincent on $Time.
 */
public class PhotoUtil {
	
	/**
	 *  相册的RequestCode
	 */
	public static final int INTENT_REQUEST_CODE_ALBUM = 0;
	/**
	 *  照相的RequestCode
	 */
	public static final int INTENT_REQUEST_CODE_CAMERA = 1;
	/**
	 *  裁剪照片的RequestCode
	 */
	public static final int INTENT_REQUEST_CODE_CROP = 2;
	/**
	 *  滤镜图片的RequestCode
	 */
	public static final int INTENT_REQUEST_CODE_FILTER = 3;

	/**
	 * 通过手机相册获取图片
	 *
	 * @param activity
	 */
	public static void selectImage(Activity activity) throws Exception {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
		activity.startActivityForResult(intent, INTENT_REQUEST_CODE_ALBUM);
	}

	/**
	 * 通过手机照相获取图片
	 *
	 * @param activity
	 */
	public static String takePicture(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsssss");
		Date date = new Date(System.currentTimeMillis());
		String path = (String) SPUtil.get(BaseConstant.IMAGE_PATH, "") + "/" +  simpleDateFormat.format(date) + ".jpeg";
		File file = FileUtil.createNewFile(path);
		if (file != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		}
		activity.startActivityForResult(intent, INTENT_REQUEST_CODE_CAMERA);
		return path;
	}

	/**
	 * 删除图片缓存目录
	 */
	public static void deleteImageFile() throws IOException {
		File dir = new File((String) SPUtil.get(BaseConstant.IMAGE_PATH, ""));
		if (dir.exists()) {
			FileUtil.delFolder((String) SPUtil.get(BaseConstant.IMAGE_PATH, ""));
		}
	}

	/**
	 * 从文件中获取图片
	 *
	 * @param path
	 *            图片的路径
	 * @return
	 */
	public static Bitmap getBitmapFromFile(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 从Uri中获取图片
	 *
	 * @param cr
	 *            ContentResolver对象
	 * @param uri
	 *            图片的Uri
	 * @return
	 */
	public static Bitmap getBitmapFromUri(ContentResolver cr, Uri uri) {
		try {
			return BitmapFactory.decodeStream(cr.openInputStream(uri));
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	/**
	 * 根据宽度和长度进行缩放图片
	 *
	 * @param path
	 *            图片的路径
	 * @param w
	 *            宽度
	 * @param h
	 *            长度
	 * @return
	 */
	public static Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取图片的长度和宽度
	 *
	 * @param bitmap
	 *            图片bitmap对象
	 * @return
	 */
	public static Bundle getBitmapWidthAndHeight(Bitmap bitmap) {
		Bundle bundle = null;
		if (bitmap != null) {
			bundle = new Bundle();
			bundle.putInt("width", bitmap.getWidth());
			bundle.putInt("height", bitmap.getHeight());
			return bundle;
		}
		return null;
	}

	/**
	 * 判断图片高度和宽度是否过大
	 *
	 * @param bitmap
	 *            图片bitmap对象
	 * @return
	 */
	public static boolean bitmapIsLarge(Bitmap bitmap) {
		final int MAX_WIDTH = 60;
		final int MAX_HEIGHT = 60;
		Bundle bundle = getBitmapWidthAndHeight(bitmap);
		if (bundle != null) {
			int width = bundle.getInt("width");
			int height = bundle.getInt("height");
			if (width > MAX_WIDTH && height > MAX_HEIGHT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据比例缩放图片
	 *
	 * @param screenWidth
	 *            手机屏幕的宽度
	 * @param filePath
	 *            图片的路径
	 * @param ratio
	 *            缩放比例
	 * @return
	 */
	public static Bitmap CompressionPhoto(float screenWidth, String filePath,
			int ratio) {
		Bitmap bitmap = PhotoUtil.getBitmapFromFile(filePath);
		Bitmap compressionBitmap = null;
		float scaleWidth = screenWidth / (bitmap.getWidth() * ratio);
		float scaleHeight = screenWidth / (bitmap.getHeight() * ratio);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		try {
			compressionBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			return bitmap;
		}
		return compressionBitmap;
	}

	/**
	 * 保存图片到SD卡
	 *
	 * @param bitmap
	 *            图片的bitmap对象
	 * @return
	 */
	public static String savePhotoToSDCard(Bitmap bitmap) throws IOException {
		if (!FileUtil.isSdcardExist()) {
			return null;
		}
		FileOutputStream fileOutputStream = null;
		FileUtil.createDirFile((String) SPUtil.get(BaseConstant.IMAGE_PATH, ""));

		String fileName = UUID.randomUUID().toString() + ".jpg";
		String newFilePath = (String) SPUtil.get(BaseConstant.IMAGE_PATH, "") + fileName;
		File file = FileUtil.createNewFile(newFilePath);
		if (file == null) {
			return null;
		}
		try {
			fileOutputStream = new FileOutputStream(newFilePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		} catch (FileNotFoundException e1) {
			return null;
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
		return newFilePath;
	}

//
//	/**
//	 * 根据文字获取图片
//	 *
//	 * @param text
//	 * @return
//	 */
//	public static Bitmap getIndustry(Context context, String text) {
//		String color = "#ffefa600";
//		if ("艺".equals(text)) {
//			color = "#ffefa600";
//		} else if ("学".equals(text)) {
//			color = "#ffbe68c1";
//		} else if ("商".equals(text)) {
//			color = "#ffefa600";
//		} else if ("医".equals(text)) {
//			color = "#ff30c082";
//		} else if ("IT".equals(text)) {
//			color = "#ff27a5e3";
//		}
//		Bitmap src = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.ic_userinfo_group);
//		int x = src.getWidth();
//		int y = src.getHeight();
//		Bitmap bmp = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
//		Canvas canvasTemp = new Canvas(bmp);
//		canvasTemp.drawColor(Color.parseColor(color));
//		Paint p = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
//		p.setColor(Color.WHITE);
//		p.setFilterBitmap(true);
//		int size = (int) (13 * context.getResources().getDisplayMetrics().density);
//		p.setTextSize(size);
//		float tX = (x - getFontlength(p, text)) / 2;
//		float tY = (y - getFontHeight(p)) / 2 + getFontLeading(p);
//		canvasTemp.drawText(text, tX, tY, p);
//
//		return toRoundCorner(bmp, 2);
//	}

	/**
	 * @return 返回指定笔和指定字符串的长度
	 */
	public static float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}

	/**
	 * @return 返回指定笔的文字高度
	 */
	public static float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}

	/**
	 * @return 返回指定笔离文字顶部的基准距离
	 */
	public static float getFontLeading(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.leading - fm.ascent;
	}

	/**
	 * 获取圆角图片
	 *
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取颜色的圆角bitmap
	 *
	 * @param context
	 * @param color
	 * @return
	 */
	public static Bitmap getRoundBitmap(Context context, int color) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 12.0f, metrics));
		int height = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4.0f, metrics));
		int round = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2.0f, metrics));
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawRoundRect(new RectF(0.0F, 0.0F, width, height), round,
				round, paint);
		return bitmap;
	}

	/**
	 * 从相册数据库中读取图片
	 * @param context
	 * @param bucketId
	 * @param page
	 * @param limit
	 * @param bucketDisplayName
	 * @return
	 */
	public static List<MediaBean> getMediaWithImageList(Context context, String bucketId,
														int page, int limit, String bucketDisplayName) {
		int offset = (page -1) * limit;
		List<MediaBean> mediaBeanList = new ArrayList<>();
		ContentResolver contentResolver = context.getContentResolver();
		List<String> projection = new ArrayList<>();
		projection.add(MediaStore.Images.Media._ID);
		projection.add(MediaStore.Images.Media.TITLE);
		projection.add(MediaStore.Images.Media.DATA);
		projection.add(MediaStore.Images.Media.BUCKET_ID);
		projection.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		projection.add(MediaStore.Images.Media.MIME_TYPE);
		projection.add(MediaStore.Images.Media.DATE_ADDED);
		projection.add(MediaStore.Images.Media.DATE_MODIFIED);
		projection.add(MediaStore.Images.Media.LATITUDE);
		projection.add(MediaStore.Images.Media.LONGITUDE);
		projection.add(MediaStore.Images.Media.ORIENTATION);
		projection.add(MediaStore.Images.Media.SIZE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			projection.add(MediaStore.Images.Media.WIDTH);
			projection.add(MediaStore.Images.Media.HEIGHT);
		}
		String selection = null;
		String[] selectionArgs = null;
//		if(!TextUtils.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
//			selection = MediaStore.Images.Media.BUCKET_ID + "=?";
//			selectionArgs = new String[]{bucketId};
//		}
		if (!bucketDisplayName.equals("")) {
			selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?";
			selectionArgs = new String[]{bucketDisplayName};
		}
		Cursor cursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection.toArray(new String[projection.size()]), selection,
				selectionArgs, MediaStore.Images.Media.DATE_ADDED +" DESC LIMIT " + limit +" OFFSET " + offset);
		if(cursor != null) {
			int count = cursor.getCount();
			if(count > 0) {
				cursor.moveToFirst();
				do {
					MediaBean mediaBean = parseImageCursorAndCreateThumbImage(context, cursor);
					mediaBeanList.add(mediaBean);
				} while (cursor.moveToNext());
			}
		}

		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		cursor = null;
		return mediaBeanList;
	}

	/**
	 * 解析图片cursor并且创建缩略图
	 * @param context
	 * @param cursor
	 * @return
	 */
	private static MediaBean parseImageCursorAndCreateThumbImage(Context context, Cursor cursor) {
		MediaBean mediaBean = new MediaBean();
		long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
		mediaBean.setId(id);
		String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
		mediaBean.setTitle(title);
		String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		mediaBean.setOriginalPath(originalPath);
		String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
		mediaBean.setBucketId(bucketId);
		String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
		mediaBean.setBucketDisplayName(bucketDisplayName);
		String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
		mediaBean.setMimeType(mimeType);
		long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
		mediaBean.setCreateDate(createDate);
		long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
		mediaBean.setModifiedDate(modifiedDate);
//		mediaBean.setThumbnailBigPath(createThumbnailBigFileName(context, originalPath).getAbsolutePath());
//		mediaBean.setThumbnailSmallPath(createThumbnailSmallFileName(context, originalPath).getAbsolutePath());
		int width = 0, height = 0;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
			height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
		} else {
			try {
				ExifInterface exifInterface = new ExifInterface(originalPath);
				width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
				height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
			} catch (IOException e) {
			}
		}
		mediaBean.setWidth(width);
		mediaBean.setHeight(height);
		double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
		mediaBean.setLatitude(latitude);
		double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
		mediaBean.setLongitude(longitude);
		int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
		mediaBean.setOrientation(orientation);
		long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
		mediaBean.setLength(length);
		return mediaBean;
	}

	public static ArrayList<MediaBean> getMediaWithImageOfFolderList(Context context) {
		ArrayList<MediaBean> mediaBeanList = new ArrayList<>();
		ContentResolver contentResolver = context.getContentResolver();
		List<String> projection = new ArrayList<>();
		projection.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		projection.add(MediaStore.Images.Media.DATA);
//		projection.add("count(*)");
		String selection;
		String[] selectionArgs = null;
//		if(!TextUtils.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
//			selection = MediaStore.Images.Media.BUCKET_ID + "=?";
//			selectionArgs = new String[]{bucketId};
//		}
		selection = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
		Cursor cursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection.toArray(new String[projection.size()]), selection,
				selectionArgs, MediaStore.Images.Media.DATE_ADDED +" DESC ");
		if(cursor != null) {
			int count = cursor.getCount();
			if(count > 0) {
				cursor.moveToFirst();
				do {
					MediaBean mediaBean = new MediaBean();
					String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
//
					mediaBean.setBucketDisplayName(bucketDisplayName);
					String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					mediaBean.setOriginalPath(originalPath);
					mediaBeanList.add(mediaBean);
				} while (cursor.moveToNext());
			}
		}

		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		cursor = null;
		return mediaBeanList;
	}
}


