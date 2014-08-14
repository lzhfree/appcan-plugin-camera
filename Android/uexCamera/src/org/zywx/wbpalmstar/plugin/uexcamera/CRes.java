package org.zywx.wbpalmstar.plugin.uexcamera;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;

public class CRes{

	/*public static int plugin_scan_bg;
	public static int plugin_scan_lig_off;
	public static int plugin_scan_lig_on;
	public static int plugin_scan_cancel_off;
	public static int plugin_scan_cancel_on;
	public static int plugin_scan_cancel_x_off;
	public static int plugin_scan_cancel_x_on;
	public static int plugin_scan_input_off;
	public static int plugin_scan_input_on;
	public static int plugin_scan_ok_off;
	public static int plugin_scan_ok_on;*/
	
	
	public static int plugin_camera_take_pic;
	
	public static int beep;
	public static int app_name;
	
	
	public static String DISPLAY_TEXT;
	public static String DISPLAY_TEXT_BUG;
	public static String DISPLAY_TEXT_OK;
	public static String DISPLAY_BTN_TEXT_OK;
	public static String DISPLAY_BTN_TEXT_CANCEL;
	
	private static boolean init;
	//add by tmf 
	public static int plugin_camera_layout;
	public static int plugin_camera_bt_cancel;
	public static int plugin_camera_bt_complete;
	public static int plugin_camera_bt_takepic;
	public static int plugin_camera_bt_changefacing;
	public static int plugin_camera_iv_preshow;
	public static int plugin_camera_surfaceview;
	public static int plugin_camera_bt_flash1;
	public static int plugin_camera_bt_flash2;
	public static int plugin_camera_bt_flash3;
	public static int plugin_camera_flash_drawale_auto;
	public static int plugin_camera_flash_drawale_open;
	public static int plugin_camera_flash_drawale_close;
	
	
	
	public static boolean init(Context context){
		if(init){
			return init;
		}
		String packg = context.getPackageName();
		Resources res = context.getResources();
		
		//------------add by tmf--------------------------- 
		
		plugin_camera_layout=res.getIdentifier("plugin_camera_layout", "layout", packg);
		plugin_camera_bt_cancel=res.getIdentifier("plugin_camera_bt_cancel", "id", packg);
		plugin_camera_bt_complete=res.getIdentifier("plugin_camera_bt_complete", "id", packg);
		plugin_camera_bt_takepic=res.getIdentifier("plugin_camera_bt_takepic", "id", packg);
		plugin_camera_bt_changefacing=res.getIdentifier("plugin_camera_bt_changefacing", "id", packg);
		plugin_camera_iv_preshow=res.getIdentifier("plugin_camera_iv_preshow", "id", packg);
		plugin_camera_surfaceview=res.getIdentifier("plugin_camera_surfaceview", "id", packg);
		
		plugin_camera_flash_drawale_auto=res.getIdentifier("plugin_camera_flash_auto_selector", "drawable", packg);
		plugin_camera_flash_drawale_open=res.getIdentifier("plugin_camera_flash_open_selector", "drawable", packg);
		plugin_camera_flash_drawale_close=res.getIdentifier("plugin_camera_flash_close_selector", "drawable", packg);
		
		plugin_camera_bt_flash1=res.getIdentifier("plugin_camera_bt_flash1", "id", packg) ;
		plugin_camera_bt_flash2=res.getIdentifier("plugin_camera_bt_flash2", "id", packg) ;
		plugin_camera_bt_flash3=res.getIdentifier("plugin_camera_bt_flash3", "id", packg) ;
		
		//--------------------end add by tmf--------------------------
		
		/*	plugin_scan_bg = res.getIdentifier("plugin_scan_bg", "drawable", packg);
		plugin_scan_lig_off = res.getIdentifier("plugin_scan_lig_off", "drawable", packg);
		plugin_scan_lig_on = res.getIdentifier("plugin_scan_lig_on", "drawable", packg);
		plugin_scan_cancel_off = res.getIdentifier("plugin_scan_cancel_off", "drawable", packg);;
		plugin_scan_cancel_on = res.getIdentifier("plugin_scan_cancel_on", "drawable", packg);;
		plugin_scan_cancel_x_off = res.getIdentifier("plugin_scan_cancel_x_off", "drawable", packg);;
		plugin_scan_cancel_x_on = res.getIdentifier("plugin_scan_cancel_x_on", "drawable", packg);;
		plugin_scan_input_off = res.getIdentifier("plugin_scan_input_off", "drawable", packg);;
		plugin_scan_input_on = res.getIdentifier("plugin_scan_input_on", "drawable", packg);;
		plugin_scan_ok_off = res.getIdentifier("plugin_scan_ok_off", "drawable", packg);;
		plugin_scan_ok_on = res.getIdentifier("plugin_scan_ok_on", "drawable", packg);
		
		plugin_camera_take_pic = res.getIdentifier("plugin_camera_take_pic", "drawable", packg);
		*/
		beep = res.getIdentifier("beep", "raw", packg);
		app_name = res.getIdentifier("app_name", "string", packg);
		//add by tmf
		if(plugin_camera_layout == 0 
				|| plugin_camera_bt_cancel == 0 
				|| plugin_camera_bt_complete == 0
				|| plugin_camera_bt_takepic == 0
				|| plugin_camera_iv_preshow == 0
				|| plugin_camera_bt_changefacing == 0
				|| beep == 0 
				|| app_name == 0
				||plugin_camera_surfaceview==0){
			return false;
		}
		
		/*if(plugin_scan_bg == 0 
				|| plugin_scan_lig_off == 0 
				|| plugin_scan_lig_on == 0
				|| plugin_scan_cancel_off == 0
				|| plugin_scan_cancel_on == 0
				|| plugin_scan_cancel_x_off == 0
				|| plugin_scan_cancel_x_on == 0
				|| plugin_scan_input_off == 0
				|| plugin_scan_input_on == 0
				|| plugin_scan_ok_off == 0
				|| plugin_scan_ok_on == 0
				|| beep == 0 
				|| app_name == 0){
			return false;
		}*/
		Locale language = Locale.getDefault();
		if(language.equals(Locale.CHINA) 
				|| language.equals(Locale.CHINESE) 
				|| language.equals(Locale.TAIWAN) 
				|| language.equals(Locale.TRADITIONAL_CHINESE)
				|| language.equals(Locale.SIMPLIFIED_CHINESE)
				|| language.equals(Locale.PRC)){
			
			DISPLAY_TEXT = "将目标放置于镜头范围内进行扫描";
			DISPLAY_TEXT_BUG = "很抱歉，设备相机出现问题。\n您可能没有配置使用相机的权限。";
			DISPLAY_TEXT_OK = "扫描成功";
			DISPLAY_BTN_TEXT_OK = "确定";
			DISPLAY_BTN_TEXT_CANCEL = "取消";
		}else{
			DISPLAY_TEXT = "Keep the pictrue in the right place";
			DISPLAY_TEXT_BUG = "Sorry, the Android camera encountered a problem.\n You need to add permission:'android.permission.CAMERA'.";
			DISPLAY_TEXT_OK = "Found plain text";
			DISPLAY_BTN_TEXT_OK = "Ok";
			DISPLAY_BTN_TEXT_CANCEL = "Cancel";
		}
		init = true;
		return true;
	}
}
