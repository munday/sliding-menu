package ws.munday.slidingmenu;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utility {

	public static int dipsToPixels(Context context, int dips)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dips * scale + 0.5f);
	}
	
	public static int pixelstoDips(Context context, int pixels)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pixels / scale);
	}
	
	public static double getStatusbarHeight(Context c){

		return Math.ceil(25 * c.getResources().getDisplayMetrics().density);
		
	}

	/***
	 * Gets the top status bar height.
	 * If the device has a bottom 
	 * status bar, returns 0
	 * @return int the size of the top status bar
	 */
	public static int getTopStatusBarHeight(Resources r, WindowManager m) {
		  int result = 0;
		  
		  if(!Utility.isStatusBarAtTop(m))
			  return result;
		  
		  int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
		  
		  if (resourceId > 0) {
		      result = r.getDimensionPixelSize(resourceId);
		  }
		  
		  return result;
	}
	
	/***
	 * The idea here is that devices at sw600 and up 
	 * and between honeycomb and jelly bean have the combined bottom
	 * status bar and no top bar.
	 * @return boolean wheather or not the device has a status bar at the top.
	 */
	public static boolean isStatusBarAtTop(WindowManager m){
		
		DisplayMetrics dm = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		
		if(width>=600 || height>=600){
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			     return false;
			}
		}
		 
		return true;
	}
	
}
