package org.zywx.wbpalmstar.plugin.uexcamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.base.ResoureFinder;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.URLUtil;
import android.widget.Toast;

public class EUExCamera extends EUExBase {

	public static final String function = "uexCamera.cbOpen";
	public static final String function1 = "uexCamera.cbOpenInternal";

	private File m_tempPath;
	private boolean mWillCompress;
	private int mQuality;

	public EUExCamera(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	public void open(String[] parm) {
		mWillCompress = false;
		mQuality = -1;
		if (parm.length == 1) {
			int value = 1;
			try {
				value = Integer.parseInt(parm[0]);
			} catch (Exception e) {
				;
			}
			mWillCompress = value != 0 ? false : true;
		} else if (parm.length == 2) {
			int value = 1;
			try {
				value = Integer.parseInt(parm[0]);
			} catch (Exception e) {
				;
			}
			mWillCompress = value != 0 ? false : true;
			if (mWillCompress) {
				int quality = -1;
				try {
					quality = Integer.parseInt(parm[1]);
				} catch (Exception e) {
					;
				}
				mQuality = quality;
			}
		}
		MemoryInfo outInfo = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(outInfo);
		if (outInfo.lowMemory || outInfo.availMem < 31000000) {
			Toast.makeText(mContext, "内存不足,无法打开相机", Toast.LENGTH_LONG).show();
			Runtime.getRuntime().gc();
			return;
		}
		if (BUtility.sdCardIsWork()) {
			if (!mWillCompress) {
				String path = mBrwView.getCurrentWidget().getWidgetPath()
						+ getName();
				m_tempPath = new File(path);
			} else {
				m_tempPath = new File(BUtility.getSdCardRootPath() + "demo.jpg");
			}
			if (!m_tempPath.exists()) {
				try {
					m_tempPath.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			checkPath();
			if (Build.VERSION.SDK_INT >= 14) {
				// setProcessForeground();
			}
			Intent camaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri uri = Uri.fromFile(m_tempPath);
			camaIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			camaIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			startActivityForResult(camaIntent, 66666);
		} else {
			Toast.makeText(
					mContext,
					ResoureFinder.getInstance().getString(mContext,
							"error_sdcard_is_not_available"),
					Toast.LENGTH_SHORT).show();
			errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN, "Storage error");
			return;
		}
	}

	public void openInternal(String[] parm) {

		mWillCompress = false;
		mQuality = -1;
		if (parm.length == 1) {
			int value = 1;
			try {
				value = Integer.parseInt(parm[0]);
			} catch (Exception e) {
				;
			}
			mWillCompress = value != 0 ? false : true;
		} else if (parm.length == 2) {
			int value = 1;
			try {
				value = Integer.parseInt(parm[0]);
			} catch (Exception e) {
				;
			}
			mWillCompress = value != 0 ? false : true;
			if (mWillCompress) {
				int quality = -1;
				try {
					quality = Integer.parseInt(parm[1]);
				} catch (Exception e) {
					;
				}
				mQuality = quality;
			}
		}
		MemoryInfo outInfo = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(outInfo);
		if (outInfo.lowMemory || outInfo.availMem < 31000000) {
			Toast.makeText(mContext, "内存不足,无法打开相机", Toast.LENGTH_LONG).show();
			Runtime.getRuntime().gc();
			return;
		}
		if (BUtility.sdCardIsWork()) {
			if (!mWillCompress) {
				String path = mBrwView.getCurrentWidget().getWidgetPath()
						+ getName();
				m_tempPath = new File(path);
			} else {
				m_tempPath = new File(BUtility.getSdCardRootPath() + "demo.jpg");
			}
			if (!m_tempPath.exists()) {
				try {
					m_tempPath.createNewFile();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			checkPath();
			if (Build.VERSION.SDK_INT >= 14) {
				// setProcessForeground();
			}
			Intent camaIntent = new Intent();
			camaIntent.setClass(mContext, CustomCamera.class);
			camaIntent.putExtra("photoPath", m_tempPath.getAbsolutePath());
			camaIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			startActivityForResult(camaIntent, 66667);
		} else {
			Toast.makeText(
					mContext,
					ResoureFinder.getInstance().getString(mContext,
							"error_sdcard_is_not_available"),
					Toast.LENGTH_SHORT).show();
			errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN, "Storage error");
			return;
		}

	}

	// private void setProcessForeground(){
	// Intent remoteIntent = new Intent(mContext, KeepForeService.class);
	// mContext.startService(remoteIntent);
	// }
	//
	// private void setProcessBackground(){
	// Intent remoteIntent = new Intent(mContext, KeepForeService.class);
	// mContext.stopService(remoteIntent);
	// }
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Build.VERSION.SDK_INT >= 14) {
			// setProcessBackground();
		}
		String finalPath = "";
		ExifInterface exif = null;
		int degree = 0;
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 66666) {
				try {
					if (null != m_tempPath) {
						finalPath = m_tempPath.getAbsolutePath();
					} else if (null != data) {
						Uri content = data.getData();
						if (null != content) {
							String realPath = null;
							String url = content.toString();
							if (URLUtil.isFileUrl(url)) {
								realPath = url.replace("file://", "");
							} else if (URLUtil.isContentUrl(url)) {
								Activity activity = (Activity) mContext;
								Cursor c = activity.managedQuery(content, null,
										null, null, null);
								boolean isExist = c.moveToFirst();
								if (isExist) {
									realPath = c
											.getString(c
													.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
								}
								c.close();
							}
							if (null != realPath) {
								finalPath = realPath;
							}
						} else {
							Bundle bundle = data.getExtras();
							if (null != bundle) {
								Bitmap bitmap = (Bitmap) bundle.get("data");
								if (null != bitmap) {
									String newfile = BUtility
											.getSdCardRootPath() + "demo.jpg";
									File newFile = new File(newfile);
									if (!newFile.exists()) {
										newFile.createNewFile();
									}
									BufferedOutputStream bos = new BufferedOutputStream(
											new FileOutputStream(new File(
													newfile)));
									bitmap.compress(Bitmap.CompressFormat.JPEG,
											100, bos);
									bos.flush();
									bos.close();
									finalPath = newfile;
								}
							}
						}
					}
					if (null == finalPath) {
						errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN,
								"Storage error or no permission");
						return;
					}
					if (URLUtil.isFileUrl(finalPath)) {
						finalPath = finalPath.replace("file://", "");
					}
					exif = new ExifInterface(finalPath);
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION, -1);
					if (orientation != -1) {
						switch (orientation) {
						case ExifInterface.ORIENTATION_NORMAL:
							degree = 0;
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							degree = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							degree = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							degree = 270;
							break;
						}
					}
					if (!mWillCompress && 0 == degree) {
						jsCallback(function, 0, EUExCallback.F_C_TEXT,
								finalPath);
					} else {
						String tPath = makePictrue(new File(finalPath), degree);
						if (null == tPath) {
							errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN,
									"Storage error or no permission");
						} else {
							jsCallback(function, 0, EUExCallback.F_C_TEXT,
									tPath);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN,
							"Storage error or no permission");
					return;
				}
			} else if (requestCode == 66667) {
				if (null != data)
					;
				finalPath = m_tempPath.getAbsolutePath();
				if (finalPath != null) {
					try {
						exif = new ExifInterface(finalPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION, -1);
					if (orientation != -1) {
						switch (orientation) {
						case ExifInterface.ORIENTATION_NORMAL:
							degree = 0;
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							degree = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							degree = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							degree = 270;
							break;
						}
					}
					if (!mWillCompress && 0 == degree) {
						jsCallback(function1, 0, EUExCallback.F_C_TEXT,
								finalPath);
					} else {
						String tPath = makePictrue(new File(finalPath), degree);
						if (null == tPath) {
							errorCallback(0, EUExCallback.F_E_UEXCAMERA_OPEN,
									"Storage error or no permission");
						} else {
							jsCallback(function1, 0, EUExCallback.F_C_TEXT,
									tPath);
						}
					}
				}
			}
		}

	}

	private String makePictrue(File inPath, int degree) {
		String newPath = mBrwView.getCurrentWidget().getWidgetPath()
				+ getName();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		if (mWillCompress) {
			if (mQuality > 0) {
				;
			} else {
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(inPath.getAbsolutePath(), opts);
				int sampleSize = 1;
				int w = opts.outWidth;
				int h = opts.outHeight;
				if (w > 3600 || h > 3600) {
					sampleSize = 4;
				} else if (w > 2500 || h > 2500) {
					sampleSize = 3;
				} else if (w > 1200 || h > 1200) {
					sampleSize = 2;
				} else {
					sampleSize = 1;
				}
				opts.inSampleSize = sampleSize;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				opts.inJustDecodeBounds = false;
				mQuality = 60;
			}
		} else {
			opts.inSampleSize = 2;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inJustDecodeBounds = false;
			mQuality = 100;
		}
		Bitmap picture = null;
		File newFile = new File(newPath);
		try {
			picture = BitmapFactory.decodeFile(inPath.getAbsolutePath(), opts);
			if (degree > 0 && null != picture) {
				picture = Util.rotate(picture, degree);
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(newFile));
			picture.compress(Bitmap.CompressFormat.JPEG, mQuality, bos);
			bos.flush();
			bos.close();
		} catch (OutOfMemoryError e) {
			Toast.makeText(mContext, "照片尺寸过大，内存溢出，\n请降低尺寸拍摄！",
					Toast.LENGTH_LONG).show();
			return null;
		} catch (IOException e) {

			return null;
		} finally {
			if (null != picture) {
				picture.recycle();
			}
			inPath.delete();
			System.gc();
		}
		mWillCompress = false;
		mQuality = -1;
		return newPath;
	}

	private String getName() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		return "photo/scan" + df.format(date) + ".jpg";
	}

	private void checkPath() {
		String widgetPath = mBrwView.getCurrentWidget().getWidgetPath()
				+ "photo";
		File temp = new File(widgetPath);
		if (!temp.exists()) {
			temp.mkdirs();
		} else {
			// File[] files = temp.listFiles();
			// if (files.length >= 100) {
			// for (File file : files) {
			// file.delete();
			// }
			// }
		}
	}

	@Override
	protected boolean clean() {
		if (null != m_tempPath) {
			m_tempPath = null;
		}
		return true;
	}

}
