package ws.munday.slidingmenu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class SlidingMenuActivity extends FragmentActivity {

	public static final int MENU_TYPE_SLIDING = 1;
	public static final int MENU_TYPE_SLIDEOVER = 2;
	
	private boolean mIsLayoutShown = false;
	private int mMenuWidth;
	public static final String LOG_TAG = "SlidingMenuActivity";
	private int mMenuLayoutId;
	private int mContentLayoutId;
	private long mAnimationDuration = 400;
	private int mMaxMenuWidthDps = 375;
	private int mMinMainWidthDps = 50;
	private Interpolator mInterpolator = new DecelerateInterpolator(1.2f);
	private int mType = MENU_TYPE_SLIDING;
	private boolean mShowTitleBar = false;
	
	public SlidingMenuActivity(){
		this(false);
	}
	
	public SlidingMenuActivity(boolean showTitleBar){
		mShowTitleBar = showTitleBar;
	}
	
	public void setLayoutIds(int menuLayoutId, int contentLayoutId){
		mMenuLayoutId = menuLayoutId;
		mContentLayoutId = contentLayoutId;
	}
	
	public void setAnimationDuration(long duration){
		mAnimationDuration = duration;
	}
	
	public void setMaxMenuWidth(int width){
		mMaxMenuWidthDps = width;
	}
	
	public void setMinContentWidth(int width){
		mMinMainWidthDps = width;
	}
	
	public void setAnimationType(int type){
		mType = type;
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
		if(!mShowTitleBar) requestWindowFeature(Window.FEATURE_NO_TITLE);
		switch(mType){
			default:
				setContentView(R.layout.ws_munday_slideovermenu);
				break;
		}
		
		ViewGroup menu = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);

		LayoutInflater li = LayoutInflater.from(this);
		menu.addView(li.inflate(mMenuLayoutId, null));
		content.addView(li.inflate(mContentLayoutId, null));
		menu.setVisibility(View.GONE);
		initMenu(false);	
		
		Log.d(LOG_TAG,"onCreate finished");
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initMenu(true);
		super.onConfigurationChanged(newConfig);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch(keyCode){
	    	case KeyEvent.KEYCODE_MENU:
	    		toggleMenu();
	    		return true;
	    }
	    return super.onKeyDown(keyCode, event);
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

		switch(mType){
			case MENU_TYPE_SLIDEOVER:
				toggleSlidingMenu();
				break;
			default:
				toggleSlideOverMenu();
				break;
		}
	}
	
	public void toggleSlideOverMenu(){
		
		View v2 = findViewById(R.id.ws_munday_slidingmenu_content_frame);
		v2.clearAnimation();
		v2.setDrawingCacheEnabled(true);
		
		if(mIsLayoutShown){
			MarginAnimation a = new MarginAnimation(v2, mMenuWidth, 0, mInterpolator);
			a.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.GONE);
				}
			});
			
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
		}else{	
			MarginAnimation a = new MarginAnimation(v2, 0, mMenuWidth, mInterpolator);
			
			a.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
					ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.VISIBLE);
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
				}
		
			});
			
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
		}
		
		mIsLayoutShown = !mIsLayoutShown;
	
	
	}
	
	public void toggleSlidingMenu(){
		
		View v2 = findViewById(R.id.ws_munday_slidingmenu_content_frame);
		v2.clearAnimation();
		v2.setDrawingCacheEnabled(true);
		
		View vMenu = findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		vMenu.clearAnimation();
		vMenu.setDrawingCacheEnabled(true);
		
		if(mIsLayoutShown){
			
			MarginAnimation a2 = new MarginAnimation(vMenu, 0, -mMenuWidth, mInterpolator);
			a2.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.GONE);
				}
			});
			
			a2.setDuration(mAnimationDuration);
			vMenu.startAnimation(a2);
			
			MarginAnimation a = new MarginAnimation(v2, mMenuWidth, 0, mInterpolator);
			a.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.GONE);
				}
			});
			
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
			
		}else{	
			MarginAnimation a = new MarginAnimation(v2, 0, mMenuWidth, mInterpolator);
			
			a.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
				}
		
			});
			
			a.setDuration(mAnimationDuration);
			v2.startAnimation(a);
			
			MarginAnimation a2 = new MarginAnimation(vMenu, -mMenuWidth, 0, mInterpolator);
			a2.setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
					ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.VISIBLE);
				}
				
				public void onAnimationRepeat(Animation animation) {
				}
				
				public void onAnimationEnd(Animation animation) {
				}
			});
			
			a2.setDuration(mAnimationDuration);
			vMenu.startAnimation(a2);
			
		}
		
		mIsLayoutShown = !mIsLayoutShown;
	
	}
	
	public void initMenu(boolean isConfigChange){
		
		switch(mType){
		
			case MENU_TYPE_SLIDEOVER:
				initSlideOverMenu(isConfigChange);
				break;
			
			default:
				initSlideOutMenu(isConfigChange);
				break;
		
		}
	}
	
	@SuppressWarnings("deprecation")
	public void initSlideOutMenu(boolean isConfigChange){
		//get menu and main layout
		FrameLayout menu = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		FrameLayout root = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_content_frame);
		
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
		
		int iconWidth = Utility.dipsToPixels(this, mMinMainWidthDps);
		
		//offset the width by 100 to leave room for the icon
		mMenuWidth = Math.min(x - iconWidth, mMaxMenuWidthDps);
		
		//update sizes and margins for sliding menu
		RelativeLayout.LayoutParams mp = new RelativeLayout.LayoutParams(x+mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(x+mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		
		if(isConfigChange){
			if(mIsLayoutShown){
				mp.leftMargin = -mMenuWidth;
				rp.leftMargin = 0;
			}else{
				mp.leftMargin = 0;
				rp.leftMargin = mMenuWidth;
			}
		}else{
			mp.leftMargin = -mMenuWidth;
			rp.leftMargin = 0;
			mIsLayoutShown = false;
		}
		
		menu.setLayoutParams(mp);
		menu.requestLayout();
		
		root.setLayoutParams(rp);
		root.requestLayout();
	}
	
	@SuppressWarnings("deprecation")
	public void initSlideOverMenu(boolean isConfigChange){
		//get menu and main layout
		ViewGroup menu = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		//ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);

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
		int iconWidth = Utility.dipsToPixels(this, mMinMainWidthDps);
		
		//offset the width by 100 to leave room for the icon
		mMenuWidth = Math.min(x - iconWidth, mMaxMenuWidthDps);
		
		//update sizes and margins for sliding menu
		menu.setLayoutParams(new RelativeLayout.LayoutParams(mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT));
		//menu.requestLayout();
		
		if(isConfigChange){
			mIsLayoutShown = !mIsLayoutShown;
			toggleMenu();
		}
	}
	
}

