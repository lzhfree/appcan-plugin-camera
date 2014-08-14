package org.zywx.wbpalmstar.plugin.uexcamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.R.integer;
import android.app.Activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;

import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;

import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import android.widget.Toast;

public class CustomCamera extends Activity implements Callback,AutoFocusCallback
{

	public SurfaceView mSurfaceView;
	private Button mBtnCancel;
	private Button mBtnHandler;
	private Button mBtnTakePic;
	private Button mBtnChangeFacing;
	private Button mBtnFlash1;
	private Button mBtnFlash2;
	private Button mBtnFlash3;
	private ImageView mIvPreShow;
	public Camera mCamera;
	public String filePath=null ;// 照片保存路径
	private boolean hasSurface;
	private boolean isOpenFlash=true;
	private Object lock=new Object();
	

	public int cameraCurrentlyLocked;
	// The first rear facing camera
    private ArrayList<Integer> flashDrawableIds;

	
	private boolean mPreviewing = false;
	protected boolean isHasPic=false;
	private boolean  isHasFrontCamera=false;//是否有前置摄像头
	private boolean isHasBackCamera=false;
	private boolean ismCameraCanFlash=false;
	
	private HandlePicAsyncTask mHandleTask;
	
	
   
    
	private final int NEED_CLOSE_FLASH_BTS=1;
	
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
	
			if(msg.what==NEED_CLOSE_FLASH_BTS){
				
				if(isOpenFlash){
					isOpenFlash=false;
				    mBtnFlash2.setVisibility(View.INVISIBLE);
					mBtnFlash3.setVisibility(View.INVISIBLE);
					Log.d("visible", " after close flash view,bts visible is"+mBtnFlash2.getVisibility()+" ,"+mBtnFlash3.getVisibility());
					
				}
			
				}
			
			super.handleMessage(msg);
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CRes.init(getApplication());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	
		setContentView(CRes.plugin_camera_layout);
		filePath=getIntent().getStringExtra("photoPath");
	
	   int	 numberOfCameras = Camera.getNumberOfCameras(); 
	   CameraInfo cameraInfo = new CameraInfo();
	   for (int i = 0; i < numberOfCameras; i++) {
		 Camera.getCameraInfo(i, cameraInfo); 
		 if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) 
		 {
			 
			 isHasBackCamera=true;
		 }
		 else if(cameraInfo.facing ==CameraInfo.CAMERA_FACING_FRONT)
		 {    isHasFrontCamera=true;
			
			 } 
		 }
		 if(isHasBackCamera){
			 cameraCurrentlyLocked=CameraInfo.CAMERA_FACING_BACK; 
		 }else if(isHasFrontCamera){
			 cameraCurrentlyLocked=CameraInfo.CAMERA_FACING_FRONT;
		 }else{
			Toast.makeText(this, "no camera find", Toast.LENGTH_SHORT).show();
			return;
		 }
		
	
		mSurfaceView = (SurfaceView) findViewById(CRes.plugin_camera_surfaceview);
		mBtnCancel = (Button) findViewById(CRes.plugin_camera_bt_cancel);
		mBtnHandler = (Button) findViewById(CRes.plugin_camera_bt_complete);
		mBtnChangeFacing = (Button) findViewById(CRes.plugin_camera_bt_changefacing);
		if(!isHasBackCamera||!isHasFrontCamera){
			mBtnChangeFacing.setVisibility(View.INVISIBLE);
		}
		mIvPreShow = (ImageView) findViewById(CRes.plugin_camera_iv_preshow);
		mBtnTakePic = (Button) findViewById(CRes.plugin_camera_bt_takepic);
		mBtnFlash1=(Button) findViewById(CRes.plugin_camera_bt_flash1);
		mBtnFlash2=(Button) findViewById(CRes.plugin_camera_bt_flash2);
		mBtnFlash3=(Button) findViewById(CRes.plugin_camera_bt_flash3);
		
		flashDrawableIds=new ArrayList<Integer>();
		flashDrawableIds.add(Integer.valueOf(CRes.plugin_camera_flash_drawale_auto));
		flashDrawableIds.add(Integer.valueOf(CRes.plugin_camera_flash_drawale_open));
		flashDrawableIds.add(Integer.valueOf(CRes.plugin_camera_flash_drawale_close));
		
		
		mBtnFlash1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if(isOpenFlash){
					
					updateFlashButtonState(0);
					Log.d("visible", " when open,after click flash1 view,bts visible is"+mBtnFlash2.getVisibility()+" ,"+mBtnFlash3.getVisibility());
					
				}else{
					isOpenFlash=true;
					mBtnFlash2.setVisibility(View.VISIBLE);
					mBtnFlash3.setVisibility(View.VISIBLE);
					mBtnFlash2.bringToFront();
					mBtnFlash3.bringToFront();
					Log.d("visible", "when close, after click flash1 view,bts visible is"+mBtnFlash2.getVisibility()+" ,"+mBtnFlash3.getVisibility());
					
					
					mHandler.sendEmptyMessageDelayed(NEED_CLOSE_FLASH_BTS,4000);
					
				}
				
				
			 }
		});
		mBtnFlash2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateFlashButtonState(1);
			
			}
		});
		mBtnFlash3.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			updateFlashButtonState(2);
		}
	});
   
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				onPause();
				finish();
			}
		});
		mBtnHandler.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isHasPic){
					setResult(RESULT_OK);
					onPause();
					finish();
				}
              
			}
		});
		mBtnTakePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(mPreviewing){
					mPreviewing=false;
					Parameters p=mCamera.getParameters();
					mCamera.setParameters(p);
					mCamera.takePicture(null, null, jpeg);
					
				}else{
					Toast.makeText(CustomCamera.this, "摄像机正忙", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mBtnChangeFacing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mCamera!=null){
					mCamera.stopPreview();// 停掉原来摄像头的预览
					mPreviewing=false;
					
					Log.d("mPreviewing", " into change facing mPreviewing changed to :"+mPreviewing);
					
					mCamera.release();
					mCamera = null;
					if (cameraCurrentlyLocked == Camera.CameraInfo.CAMERA_FACING_BACK){
						cameraCurrentlyLocked = Camera.CameraInfo.CAMERA_FACING_FRONT;
						mCamera = Camera.open(cameraCurrentlyLocked);
						
						
					}else{
						cameraCurrentlyLocked = Camera.CameraInfo.CAMERA_FACING_BACK;
						mCamera = Camera.open(cameraCurrentlyLocked);// 打开当前选中的摄像头
						
						
					}
					try {
						mCamera.setPreviewDisplay(mSurfaceView.getHolder());// 通过surfaceview显示取景画面
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initCameraParameters();
					
					mCamera.startPreview();// 开始预览
					mPreviewing=true;
					Log.d("mPreviewing", "mPreviewing changed to :"+mPreviewing);
					
				}
				
			}

		});
		mHandler.sendEmptyMessageDelayed(NEED_CLOSE_FLASH_BTS, 1000);
		Log.d("visible", " after oncreate flash view,bts visible is"+mBtnFlash2.getVisibility()+" ,"+mBtnFlash3.getVisibility());
		
	}
    private void updateFlashButtonState(int index){
    	isOpenFlash=false;
    	mHandler.removeMessages(NEED_CLOSE_FLASH_BTS);
    	mBtnFlash2.setVisibility(View.INVISIBLE);
		mBtnFlash3.setVisibility(View.INVISIBLE);
		
		Integer i=	flashDrawableIds.get(index);
		flashDrawableIds.remove(index);
		flashDrawableIds.add(0, i);
		mBtnFlash1.setBackgroundResource(flashDrawableIds.get(0));
		mBtnFlash2.setBackgroundResource(flashDrawableIds.get(1));
		mBtnFlash3.setBackgroundResource(flashDrawableIds.get(2));
		checkFlash(i);
		
    }
	private void checkFlash(Integer i) {
		// TODO Auto-generated method stub
		int j=i;
		Parameters par=mCamera.getParameters();
				
		if(j== CRes.plugin_camera_flash_drawale_auto){
			
			par.setFlashMode(Parameters.FLASH_MODE_AUTO);
		}else if(j== CRes.plugin_camera_flash_drawale_open){
			par.setFlashMode(Parameters.FLASH_MODE_ON);
		}else if(j== CRes.plugin_camera_flash_drawale_close){
			par.setFlashMode(Parameters.FLASH_MODE_OFF);
		}
			mCamera.setParameters(par);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mHandleTask!=null){
			if(mHandleTask.cancel(true));
		}
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mPreviewing=false;
			Log.d("mPreviewing", "mPreviewing changed to :"+mPreviewing);
			
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			if (null == mCamera) {
				mCamera = Camera.open();
			}
			if (null == mCamera) {
				throw new RuntimeException("camera error!");
			}
			
			initCameraParameters();
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
			mPreviewing=true;
			Log.d("mPreviewing", "after inti camera mPreviewing changed to :"+mPreviewing);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initCameraParameters() {
		Camera.Parameters parameters = mCamera.getParameters();
		
		String mod = Build.MODEL;
		if (Build.VERSION.SDK_INT >= 8) {
			// MZ 180， other 90...
			if ("M9".equalsIgnoreCase(mod) || "MX".equalsIgnoreCase(mod)) {
				setDisplayOrientation(mCamera, 180);
			} else {
				setDisplayOrientation(mCamera, 90);
			}
		} else {
			parameters.set("orientation", "portrait");
			parameters.set("rotation", 90);
		}
		
		
		if(cameraCurrentlyLocked==CameraInfo.CAMERA_FACING_FRONT){
			ismCameraCanFlash=false;
		}else{
			ismCameraCanFlash=true;
			parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);//单独测试崩溃,说明是有问题的
			List<String> focuseMode =parameters.getSupportedFocusModes();
			boolean isContinuousFocus=false;
			for(int i=0; i<focuseMode.size(); i++){
				if(focuseMode.get(i).contains("continuous-picture")){
					isContinuousFocus=true;
					break;
				}
				if(isContinuousFocus){
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				}else{
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				}
			}
			
		}
		if(!ismCameraCanFlash){
			mBtnFlash1.setVisibility(View.INVISIBLE);
			mBtnFlash2.setVisibility(View.INVISIBLE);
			mBtnFlash3.setVisibility(View.INVISIBLE);
		}else{
		
			mBtnFlash1.setVisibility(View.VISIBLE);
			
		}
		
		
		parameters.setPictureFormat(ImageFormat.JPEG);//测试通过
	   
		
		/*List<Camera.Size> preSizes = parameters.getSupportedPreviewSizes();
		Camera.Size defualtSize=parameters.getPreviewSize();
		Log.d("size", "defualt preview size: w="+defualtSize.width+" h="+defualtSize.height);
		
		Camera.Size miniPre = computeNeedSize(preSizes);
		Log.d("size", "compute preview size: w="+miniPre.width+" h="+miniPre.height);
	  //	parameters.setPreviewSize(miniPre.width, miniPre.height);//测试通过
		
		defualtSize=parameters.getPictureSize();
		Log.d("size", "defualt picture size: w="+defualtSize.width+" h="+defualtSize.height);
		List<Camera.Size> picSizes = parameters.getSupportedPictureSizes();
		Camera.Size miniPic = computeNeedSize(picSizes);
		Log.d("size", "compute picture size: w="+miniPic.width+" h="+miniPic.height);
		
	//	parameters.setPictureSize(miniPic.width, miniPic.height);//测试通过
*/		if(parameters.isZoomSupported()){
			parameters.setZoom(1);//测试通过
		}
		mCamera.setParameters(parameters);
	}


	private void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null) {
				downPolymorphic.invoke(camera, new Object[] { angle });
			}
		} catch (Exception e1) {
		}
	}

	private Camera.Size computeNeedSize(List<Camera.Size> sizes) {
		if (null == sizes || 0 == sizes.size()) {
			return null;
		}
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Camera.Size best = null;
		int screenPix = dm.widthPixels * dm.heightPixels;
		for (Camera.Size size : sizes) {
			int sizeOne = size.width * size.height;
		//	Log.d("ldx", "superSize: " + size.width + "*" + size.height);
			if (sizeOne == screenPix) {
				best = size;
				break;
			}
		}
		if (null == best) {
			int length = sizes.size();
			if (2 >= length) {
				best = sizes.get(0);
			}else{
				int harf = length/2+1;
				best = sizes.get(harf);
			}
			
				
				
				
			
		}
		return best;
	}

	private boolean mOnKeyDown;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mOnKeyDown = true;
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mOnKeyDown) {
				finish();
			}
			mOnKeyDown = false;
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}




	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// if(null != mCamera){
		// mCamera.startPreview();
		// }
	}
	
	

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	PictureCallback jpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("run", "into picture call back");
			mHandleTask=new HandlePicAsyncTask();
			mHandleTask.execute(data);
			Log.d("run", "execute asynctask");
		}
	};
  private class HandlePicAsyncTask  extends AsyncTask<byte[], integer, Bitmap>{

	@Override
	protected Bitmap doInBackground(byte[]... params) {
		// TODO Auto-generated method stub
		Log.d("run", "background  start run");
		
		Bitmap bm=null;
		try {
			
			bm = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
			System.gc();
			if(bm!=null){
				
				Matrix m = new Matrix();
				if(cameraCurrentlyLocked==Camera.CameraInfo.CAMERA_FACING_FRONT){
					
					m.setRotate(-90, (float) bm.getWidth() / 2,
							(float) bm.getHeight() / 2);
					
				}else if(cameraCurrentlyLocked==Camera.CameraInfo.CAMERA_FACING_BACK){
					
					m.setRotate(90, (float) bm.getWidth() / 2,
							(float) bm.getHeight() / 2);
					
				}
				bm=Bitmap.createBitmap(
						
						bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
						true);
				
				File file = new File(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			}
			
			
		} catch (Exception e) {
			
		  e.printStackTrace();
	
		}finally{
			
			
			return bm;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bm) {
		// TODO Auto-generated method stub
		super.onPostExecute(bm);
		Log.d("run", "postexecute  start run");
		if(bm!=null){
			
			mIvPreShow.setScaleType(ScaleType.CENTER_CROP);
			mIvPreShow.setImageBitmap(bm);
			isHasPic=true;
			mIvPreShow.setVisibility(View.VISIBLE);
			
		}else{
			Toast.makeText(CustomCamera.this, "拍照失败", Toast.LENGTH_SHORT).show();
		}
		mCamera.startPreview();
		mPreviewing=true;
		Log.d("mPreviewing", " after take pic mPreviewing changed to :"+mPreviewing);
		Log.d("run", "postexecute  end run");
		
	}
	
	  
  }


	private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;



	public static int roundOrientation(int orientation) {
		return ((orientation + 45) / 90 * 90) % 360;
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	
}
