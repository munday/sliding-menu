package ws.munday.slidingmenu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

public class SlidingMenuActivity extends FragmentActivity {

	private boolean mIsLayoutShown = false;
	private int mMenuWidth;
	public static final String LOG_TAG = "SlidingMenuActivity";
	private int mMenuLayoutId;
	private int mContentLayoutId;
	private long mAnimationDuration = 400;
	private int mMaxMenuWidth = 375;
	private Interpolator mInterpolator = new DecelerateInterpolator(1.2f);
	
	public void setLayoutIds(int menuLayoutId, int contentLayoutId){
		mMenuLayoutId = menuLayoutId;
		mContentLayoutId = contentLayoutId;
	}
	
	public void setAnimationDuration(long duration){
		mAnimationDuration = duration;
	}
	
	public void setMaxMenuWidth(int width){
		mMaxMenuWidth = width;
	}
	
	public Interpolator getInterpolator(){
		return mInterpolator;
	}
	
	public void setInterpolator(Interpolator i){
		mInterpolator = i;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ws_munday_slidingmenu);

		ViewGroup menu = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);

		LayoutInflater li = LayoutInflater.from(this);
		menu.addView(li.inflate(mMenuLayoutId, null));
		content.addView(li.inflate(mContentLayoutId, null));
		
		initSlidingMenu(true);	
		
		Log.d(LOG_TAG,"onCreate finished");
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initSlidingMenu(false);
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {
		if(mIsLayoutShown){
			toggleMenu();
		}else{
			super.onBackPressed();
		}
	}
	
	public void toggleMenu(){
		
		View v2 = findViewById(R.id.ws_munday_slidingmenu_root_layout);
		v2.clearAnimation();
		v2.setDrawingCacheEnabled(true);
		
		if(mIsLayoutShown){
			ScrollToAnimation a = new ScrollToAnimation(v2, 0, -(mMenuWidth), mInterpolator);
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
		}else{	
			ScrollToAnimation a = new ScrollToAnimation(v2, -(mMenuWidth), 0, mInterpolator);
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
		}
		
		mIsLayoutShown = !mIsLayoutShown;
	
	}
	
	@SuppressWarnings("deprecation")
	public void initSlidingMenu(boolean setScroll){
		//get menu and main layout
		View menu = findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.ws_munday_slidingmenu_root_layout);
		
		//get screen width
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int x = 0;
		try{
			Method m = Display.class.getMethod("getSize", new Class[] {} );
			m.invoke(display, size);
			x = size.x;
		}catch(NoSuchMethodException nsme){
			x = display.getWidth();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		//icon width with 2dp pad on each side
		int iconWidth = Utility.dipsToPixels(this, 74);
		
		//offset the width by 100 to leave room for the icon
		mMenuWidth = Math.min(x - iconWidth, mMaxMenuWidth);
		
		//update sizes and margins for sliding menu
		menu.setLayoutParams(new RelativeLayout.LayoutParams(mMenuWidth,LayoutParams.MATCH_PARENT));
		menu.requestLayout();
		
		root.setLayoutParams(new LayoutParams(x + mMenuWidth,LayoutParams.MATCH_PARENT));
		
		if(setScroll)
			root.scrollTo(mMenuWidth, 0);
		
	}
	
}

