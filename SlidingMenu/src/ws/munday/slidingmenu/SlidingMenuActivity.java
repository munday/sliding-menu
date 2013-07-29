package ws.munday.slidingmenu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ws.munday.slidingmenu.R.layout;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

/**
 * A FragmentActivity subclass that adds a sliding menu.
 */

public class SlidingMenuActivity extends FragmentActivity {

	public static final int MENU_TYPE_SLIDING = 1;
	public static final int MENU_TYPE_SLIDEOVER = 2;
	public static final int MENU_TYPE_PARALLAX = 3;
	
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
	private boolean mSlideTitleBar = true;
	
	public SlidingMenuActivity(){
		this(true);
	}

    /**
     * Constructor that adds the option of sliding the title bar away with the content when the menu
     * is displayed.
     * @param slideTitleBar
     */
    public SlidingMenuActivity(boolean slideTitleBar){
		mSlideTitleBar = slideTitleBar;
	}

    /**
     * Sets the layoutids for the menu and main content areas.
     * This must be called before onCreate.
     * @param menuLayoutId
     * @param contentLayoutId
     */
	public void setLayoutIds(int menuLayoutId, int contentLayoutId){
		mMenuLayoutId = menuLayoutId;
		mContentLayoutId = contentLayoutId;
	}

    /**
     * sets the length in milliseconds in which the open and close animation should complete.
     * @param duration
     */
	public void setAnimationDuration(long duration){
		mAnimationDuration = duration;
	}

    /**
     * Sets the maximum width in dps for the menu area.
     * @param width
     */
	public void setMaxMenuWidth(int width){
		mMaxMenuWidthDps = width;
	}

    /**
     * Sets the minimum width of the content area when the menu is displayed.
     * @param width
     */
	public void setMinContentWidth(int width){
		mMinMainWidthDps = width;
	}

    /**
     * Sets the open/close animation type.
     * @param type
     *      {@link SlidingMenuActivity#MENU_TYPE_SLIDING},
     *      {@link SlidingMenuActivity#MENU_TYPE_SLIDEOVER} or
     *      {@link SlidingMenuActivity#MENU_TYPE_PARALLAX}
     */
	public void setAnimationType(int type){
		mType = type;
	}

    /**
     * Gets the interpolator for the open/close animation.
     * @return
     */
	public Interpolator getInterpolator(){
		return mInterpolator;
	}

    /**
     * Sets the interpolator for the open/close animation.
     * @param interpolator
     */
	public void setInterpolator(Interpolator interpolator){
		mInterpolator = interpolator;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!mSlideTitleBar){ 

            //Do not move the title bar with the content.
            //Just set our root as the content view.

			setContentView(R.layout.ws_munday_slideovermenu);
			
			RelativeLayout main = (RelativeLayout) findViewById(R.id.ws_munday_slidingmenu_root_layout);
			
			ViewGroup menu = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
			ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);
	
			LayoutInflater li = getLayoutInflater();
			
			content.addView(li.inflate(mContentLayoutId, null));
			menu.addView(li.inflate(mMenuLayoutId, null));
			
			menu.setVisibility(View.GONE);
			
		}else{

            //Move the title bar with the content.
            //Replace the first child of the decor view (the app root view) with our view
            //and re-attach it to our view.

			setContentView(mContentLayoutId);
			Window window = getWindow();
			
			ViewGroup decor = (ViewGroup) window.getDecorView();
			ViewGroup allContent = (ViewGroup)decor.getChildAt(0);
			decor.removeView(allContent);
			
			LayoutInflater li = getLayoutInflater();
			
			RelativeLayout main = (RelativeLayout) li.inflate(layout.ws_munday_slideovermenu, null);
			
			ViewGroup menu = (ViewGroup) main.findViewById(R.id.ws_munday_slidingmenu_menu_frame);
			ViewGroup content = (ViewGroup) main.findViewById(R.id.ws_munday_slidingmenu_content_frame);
			
			int statusbarHeight = (int)Utility.getTopStatusBarHeight(getResources(), getWindowManager());
			
			ViewGroup mnu = (ViewGroup) li.inflate(mMenuLayoutId, null);
			mnu.setPadding(mnu.getPaddingLeft(), mnu.getPaddingTop()+statusbarHeight, mnu.getPaddingRight(), mnu.getPaddingTop());
			content.addView(allContent);
			content.setBackgroundDrawable(Utility.getThemeBackground(this));
			menu.addView(mnu);
			
			decor.addView(main);
			menu.setVisibility(View.GONE);
			
		}
		
		initMenu(false);	
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initMenu(true);
		super.onConfigurationChanged(newConfig);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Toggle the menu on menu key press.

        switch(keyCode){
	    	case KeyEvent.KEYCODE_MENU:
	    		toggleMenu();
	    		return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {

        //Close the menu on back press if it is open.

        if(mIsLayoutShown){
			toggleMenu();
		}else{
			super.onBackPressed();
		}
	}

    /**
     * Sets the position of the right hand side of the menu.
     * @param right
     */
	public void setMenuRightPosition(float right){
	
		FrameLayout menu = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		FrameLayout root = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_content_frame);
		menu.setVisibility(View.VISIBLE);
		
		//update sizes and margins for sliding menu
		RelativeLayout.LayoutParams mp = new RelativeLayout.LayoutParams(mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT ,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		if(mType != MENU_TYPE_SLIDEOVER){
			mp.leftMargin = (int)right - mMenuWidth;
			mp.rightMargin = -(int)right; 
		}
		
		rp.rightMargin = -(int)right;
		rp.leftMargin = (int)right;
		
		menu.setLayoutParams(mp);
		root.setLayoutParams(rp);
				
	}

    /**
     * Toggles the menu.
     */
	public void toggleMenu(){

		switch(mType){
			case MENU_TYPE_SLIDEOVER:
				toggleSlideOverMenu();
				break;
			case MENU_TYPE_PARALLAX:
				toggleSlidingMenu(mAnimationDuration/2);
				break;
			default: /*MENU_TYPE_SLIDING*/
				toggleSlidingMenu();
				break;
		}
		
		
	}

    /**
     * Animates the menu from a given position to another.
     * @param start the position to start the animation.
     * @param end the position to end the animation.
     */
	public void AnimateMenuPosition(final int start,final int end){

		switch(mType){
			case MENU_TYPE_SLIDEOVER:
				animateSlideOverMenuPosition(start, end);
				break;
			case MENU_TYPE_PARALLAX:
				animateSlidingMenuPosition(start, end, mAnimationDuration / 2);
				break;
			default: /*MENU_TYPE_SLIDING*/
				animateSlidingMenuPosition(start, end, mAnimationDuration);
				break;
		}
		
	}

    /**
     * Animates the content over top of a fixed position menu.
     * @param start
     * @param end
     */
    public void animateSlideOverMenuPosition(final int start, final int end){
		View v2 = findViewById(R.id.ws_munday_slidingmenu_content_frame);
		v2.clearAnimation();
		v2.setDrawingCacheEnabled(true);
		
		MarginAnimation a = new MarginAnimation(v2, start, end, mInterpolator);
		a.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				if(end > 0){
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					//unhide menu.
                    v1.setVisibility(View.VISIBLE);
				}
			}				
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation animation) {
				if(end <= 0){
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					//Hide the menu to reduce overdraw.
                    v1.setVisibility(View.GONE);
				}
			}
		});
			
		a.setDuration(mAnimationDuration);
		v2.startAnimation(a);

		mIsLayoutShown = start < end;
	}

    /**
     * Convenience function to toggle a slide over menu all the way open or closed.
     */
    public void toggleSlideOverMenu(){
		if(mIsLayoutShown){
			animateSlideOverMenuPosition(mMenuWidth, 0);
		}else{
			animateSlideOverMenuPosition(0, mMenuWidth);
		}
		
	}

    /**
     * Animates both the menu and content areas independently.
     * @param start
     * @param end
     * @param menuAnimationDuration the time in millis in which the menu animation should finish.
     *                              If this value is different from the one set using
     *                              {@link ws.munday.slidingmenu.SlidingMenuActivity#setAnimationDuration(long)}
     *                              then the menu is considered parralax.
     */
	public void animateSlidingMenuPosition(final int start, final int end, long menuAnimationDuration){
		
		boolean parallax = menuAnimationDuration!=mAnimationDuration;
		
		View v2 = findViewById(R.id.ws_munday_slidingmenu_content_frame);
		v2.clearAnimation();
		v2.setDrawingCacheEnabled(true);
		
		View vMenu = findViewById(R.id.ws_munday_slidingmenu_menu_frame);
		vMenu.clearAnimation();
		vMenu.setDrawingCacheEnabled(true);
		
		MarginAnimation a = new MarginAnimation(v2, start, end, mInterpolator);
		a.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				if(end > 0){
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.VISIBLE);
				}
			}
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation animation) {
				if(end <= 0){
					ViewGroup v1 = (ViewGroup)findViewById(R.id.ws_munday_slidingmenu_menu_frame);
					v1.setVisibility(View.GONE);
				}
			}
		});
		
		a.setDuration(mAnimationDuration);
		v2.startAnimation(a);
		
		if(parallax){
			MarginAnimation a2 = null;
			if(start > end){
				a2 = new MarginAnimation(vMenu, end, -start, mInterpolator);
			} else {
				a2 = new MarginAnimation(vMenu, -start, end, mInterpolator);
			}
			a2.setDuration(menuAnimationDuration);
			vMenu.startAnimation(a2);
		}

		mIsLayoutShown = start < end;
	}

    /**
     * Convenience function for toggling a standard sliding menu.
     */
    public void toggleSlidingMenu(){
        toggleSlidingMenu(mAnimationDuration);
    }

    /**
     * Convenience function for toggling a parallax menu.
     * @param menuAnimationDuration
     */
	public void toggleSlidingMenu(long menuAnimationDuration){
		
		if(mIsLayoutShown){
			animateSlidingMenuPosition(mMenuWidth, 0, menuAnimationDuration);
		}else{
			animateSlidingMenuPosition(0, mMenuWidth, menuAnimationDuration);
		}
	
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
		

		//make sure that the content doesn't slide all the way off screen
		int minContentWidth = Utility.dipsToPixels(this, mMinMainWidthDps);
		mMenuWidth = Math.min(x - minContentWidth, mMaxMenuWidthDps);
		
		//update sizes and margins for sliding menu
		RelativeLayout.LayoutParams mp = new RelativeLayout.LayoutParams(mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(x ,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		if(isConfigChange){
			if(mIsLayoutShown){
				mp.leftMargin = 0;
				rp.leftMargin = mMenuWidth;
				rp.rightMargin = -mMenuWidth;
			}else{
				mp.leftMargin = -mMenuWidth;
				rp.leftMargin = 0;
				rp.rightMargin = 0;
			}
		}else{
			mp.leftMargin = -mMenuWidth;
			rp.leftMargin = 0;
			rp.rightMargin = -mMenuWidth;
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
		
		//make sure that the content doesn't slide all the way off screen
		int minContentWidth = Utility.dipsToPixels(this, mMinMainWidthDps);
		mMenuWidth = Math.min(x - minContentWidth, mMaxMenuWidthDps);
		
		//update sizes and margins for sliding menu
		menu.setLayoutParams(new RelativeLayout.LayoutParams(mMenuWidth,RelativeLayout.LayoutParams.MATCH_PARENT));
		menu.requestLayout();
		
		if(isConfigChange){
			if(mIsLayoutShown){
				setMenuRightPosition(mMenuWidth);
			}else{
				setMenuRightPosition(0);
			}
		}
	}
	
	
	
}

