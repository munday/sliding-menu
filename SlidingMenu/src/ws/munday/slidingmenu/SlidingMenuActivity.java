package ws.munday.slidingmenu;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ws.munday.slidingmenu.R.layout;

/**
 * A FragmentActivity subclass that adds a sliding menu.
 */

public class SlidingMenuActivity extends FragmentActivity implements View.OnTouchListener {

    public static final int MENU_TYPE_SLIDING = 1;
    public static final int MENU_TYPE_SLIDEOVER = 2;
    public static final int MENU_TYPE_PARALLAX = 3;

    public static final int DEFAULT_GRABBER_TOP_OFFSET = 150;
    public static final int DEFAULT_GRABBER_SIZE = 75;
    public static final int DEFAULT_SHADOW_WIDTH = 25;
    public static final int DEFAULT_ANIMATION_DURATION = 400;
    public static final int DEFAULT_MAX_MENU_WIDTH_DPS = 375;
    public static final int DEFAULT_MIN_CONTENT_WIDTH_DPS = 50;
    public static final int MOVEMENT_MAX_JITTER = 20;


    private boolean mIsMenuOpen = false;
    private int mMenuWidth;
    public static final String LOG_TAG = "SlidingMenuActivity";
    private int mMenuLayoutId;
    private int mContentLayoutId;
    private long mAnimationDuration = DEFAULT_ANIMATION_DURATION;
    private int mMaxMenuWidthDps = DEFAULT_MAX_MENU_WIDTH_DPS;
    private int mMinContentWidthDps = DEFAULT_MIN_CONTENT_WIDTH_DPS;
    private int mShadowWidth = DEFAULT_SHADOW_WIDTH;
    private Interpolator mInterpolator = new DecelerateInterpolator(1.2f);
    private int mType = MENU_TYPE_SLIDING;
    private boolean mSlideTitleBar = true;
    private boolean mDraggingEnabled = false;
    private boolean mShadowEnabled = true;
    private int mGrabberSize = DEFAULT_GRABBER_SIZE;
    private int mGrabberTopOffset = 0;

    private ViewGroup mRootLayout;

    private boolean mMoving = false;
    private int mOriginX = 0;
    private int mCurrentX = 0;
    private int mLastX = 0;

    public SlidingMenuActivity() {
        this(true);
    }

    /**
     * Constructor that adds the option of sliding the title bar away with the content when the menu
     * is displayed.
     *
     * @param slideTitleBar
     */
    public SlidingMenuActivity(boolean slideTitleBar) {
        mSlideTitleBar = slideTitleBar;
        if(slideTitleBar){
            mGrabberTopOffset = DEFAULT_GRABBER_TOP_OFFSET;
        }
    }

    /**
     * Sets the layoutids for the menu and main content areas.
     * This must be called before onCreate.
     *
     * @param menuLayoutId
     * @param contentLayoutId
     */
    public void setLayoutIds(int menuLayoutId, int contentLayoutId) {
        mMenuLayoutId = menuLayoutId;
        mContentLayoutId = contentLayoutId;
    }

    public void setSlideTitlebar(boolean slide) {
        mSlideTitleBar = slide;
    }

    public void setDraggingEnabled(boolean enabled){
        mDraggingEnabled = enabled;
    }

    public boolean getDraggingEnabled(){
        return mDraggingEnabled;
    }

    public void setShadowEnabled(boolean enabled){
        mShadowEnabled = enabled;
    }

    public boolean getShadowEnabled(){
        return mShadowEnabled;
    }

    /**
     * Sets the hit size for grabbing and dragging the menu opened/closed
     * @param size
     */
    public void setGrabberSize(int size) {
        mGrabberSize = size;
    }

    /**
     * Gets the hit size for grabbing and dragging
     * @return the size
     */
    public int getGrabberSize(){
        return mGrabberSize;
    }

    /**
     * Sets the amount of space from the top of the screen that is ungrabbable (Dragging to open
     * or close above this position will not work). This setting is useful when you have clickable
     * buttons on the left side of the actionbar.
     * @param offset
     */
    public void setGrabberTopOffset(int offset){
        mGrabberTopOffset = offset;
    }

    /**
     * Gets the undraggable offset from the top of the screen
     * @return the offset
     */
    public int getGrabberTopOffset(){
        return mGrabberTopOffset;
    }

    /**
     * Sets the length in milliseconds in which the open and close animation should complete.
     *
     * @param duration
     */
    public void setAnimationDuration(long duration) {
        mAnimationDuration = duration;
    }

    /**
     * Gets the animation duration
     * @return
     */
    public long getAnimationDuration(){
        return mAnimationDuration;
    }

    /**
     * Sets the maximum width in dps for the menu area.
     *
     * @param width
     */
    public void setMaxMenuWidth(int width) {
        mMaxMenuWidthDps = width;
    }

    /**
     * Gets the maximum menu width in dps
     * @return
     */
    public int getMaxMenuWidth(){
        return mMaxMenuWidthDps;
    }

    /**
     * Sets the minimum width of the content area when the menu is displayed.
     *
     * @param width
     */
    public void setMinContentWidth(int width) {
        mMinContentWidthDps = width;
    }

    /**
     * Gets the minimum menu width in dps
     * @return
     */
    public int getMinContentWidth() {
        return mMinContentWidthDps;
    }

    /**
     * Sets the open/close animation type.
     *
     * @param type {@link SlidingMenuActivity#MENU_TYPE_SLIDING},
     *             {@link SlidingMenuActivity#MENU_TYPE_SLIDEOVER} or
     *             {@link SlidingMenuActivity#MENU_TYPE_PARALLAX}
     */
    public void setAnimationType(int type) {
        mType = type;
    }

    /**
     * Gets the open/close animation type
     * @return {@link SlidingMenuActivity#MENU_TYPE_SLIDING},
     *         {@link SlidingMenuActivity#MENU_TYPE_SLIDEOVER} or
     *         {@link SlidingMenuActivity#MENU_TYPE_PARALLAX}
     *
     */
    public int getAnimationType(){
        return mType;
    }

    /**
     * Sets the interpolator for the open/close animation.
     *
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * Gets the interpolator for the open/close animation.
     *
     * @return
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mSlideTitleBar) {

            //Do not move the title bar with the content.
            //Just set our root as the content view.

            setContentView(R.layout.ws_munday_slideovermenu);

            mRootLayout = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_root_layout);
            mRootLayout.setOnTouchListener(this);

            ViewGroup menu = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
            ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);

            LayoutInflater li = getLayoutInflater();
            View contentView = li.inflate(mContentLayoutId, null);
            View menuView = li.inflate(mMenuLayoutId, null);

            if(contentView == null) throw new IllegalArgumentException("Content layout id is not set.");
            if(menuView == null) throw new IllegalArgumentException("Menu layout id is not set.");

            content.addView(contentView);
            menu.addView(menuView);

            menu.setVisibility(View.GONE);

        } else {

            //Move the title bar with the content.
            //Replace the first child of the decor view (the app root view) with our view
            //and re-attach it to our view.

            setContentView(mContentLayoutId);
            Window window = getWindow();

            ViewGroup decor = (ViewGroup) window.getDecorView();
            ViewGroup allContent = (ViewGroup) decor.getChildAt(0);
            if(allContent == null) throw new IllegalArgumentException("Can't find window content.");
            decor.removeView(allContent);

            LayoutInflater li = getLayoutInflater();
            mRootLayout = (ViewGroup) li.inflate(layout.ws_munday_slideovermenu, null);
            if(mRootLayout==null) return;
            mRootLayout.setOnTouchListener(this);

            ViewGroup menu = (ViewGroup) mRootLayout.findViewById(R.id.ws_munday_slidingmenu_menu_frame);
            ViewGroup content = (ViewGroup) mRootLayout.findViewById(R.id.ws_munday_slidingmenu_content_frame);

            int statusbarHeight = Utility.getTopStatusBarHeight(getResources(), getWindowManager());

            ViewGroup mnu = (ViewGroup) li.inflate(mMenuLayoutId, null);

            if(mnu == null) throw new IllegalArgumentException("Menu layout id is not set.");

            mnu.setPadding(mnu.getPaddingLeft(), mnu.getPaddingTop() + statusbarHeight, mnu.getPaddingRight(), mnu.getPaddingTop());
            content.addView(allContent);
            menu.addView(mnu);

            decor.addView(mRootLayout);
            menu.setVisibility(View.GONE);

        }

        setupShadow(mShadowWidth);

        initMenu(false);

    }

    public void setupShadow(int width) {
        mShadowWidth = width;
        View shadowView = findViewById(R.id.ws_munday_slidingmenu_shadow_frame);

        if(shadowView == null) return;

        GradientDrawable shadow = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[] {Color.BLACK, Color.TRANSPARENT});
        ViewGroup.LayoutParams params = shadowView.getLayoutParams();
        params.width = width;
        shadowView.setLayoutParams(params);
        shadowView.setBackgroundDrawable(shadow);
        if (mShadowEnabled) {
            shadowView.setVisibility(View.VISIBLE);
        } else {
            shadowView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        initMenu(true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Toggle the menu on menu key press.

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                toggleMenu();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        //Close the menu on back press if it is open.

        if (mIsMenuOpen) {
            toggleMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean handled = onTouch(mRootLayout, ev);

        if (!handled) handled = super.dispatchTouchEvent(ev);

        return handled;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(!mDraggingEnabled) return false;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // the start point will either be the x coordinate or the menu width
                final int x = (int) motionEvent.getX();
                final int y = (int) motionEvent.getY();
                if(y < mGrabberTopOffset) return false;

                mOriginX = Math.min(x, mMenuWidth);
                mCurrentX = mOriginX;


                if (mIsMenuOpen && x >= mMenuWidth - mGrabberSize && x <= mMenuWidth + mGrabberSize) {
                    // if the user begins the drag on the right edge of the open menu, assume that
                    // they are grabbing to close.
                    mMoving = true;
                } else if (!mIsMenuOpen && mOriginX <= mGrabberSize) {
                    // if the user begins the drag on the left edge of a closed menu, assume that
                    // they are grabbing to open.
                    mMoving = true;
                } else {
                    mMoving = false;
                    return false;
                }

                return true;

            case MotionEvent.ACTION_MOVE:

                if (mMoving) {
                    // keep track of the current and last positions
                    setMenuRightPosition(mCurrentX);
                    mLastX = mCurrentX;
                    int nextX = (int) motionEvent.getX();
                    mCurrentX = Math.min(nextX, mMenuWidth);
                    return true;
                }

                return false;

            case MotionEvent.ACTION_UP:


                if (mMoving) {

                    mCurrentX = Math.min((int) motionEvent.getX(), mMenuWidth);

                    if (mIsMenuOpen && mCurrentX < mMenuWidth) {
                        // animate from the release point to closed
                        AnimateMenuPosition(mCurrentX, 0);
                    } else if (!mIsMenuOpen && mCurrentX > mLastX) {
                        // animate from the release point to opened
                        AnimateMenuPosition(mCurrentX, mMenuWidth);
                    } else {
                        // there is no motion (the last position and current position are equal)
                        // if the move has passed 50% of the menu width, open it. Otherwise, close it.
                        AnimateMenuPosition(mCurrentX, (mCurrentX > mMenuWidth / 4 ? mMenuWidth : 0));
                    }

                    mMoving = false;
                    mOriginX = mCurrentX = 0;

                    return true;
                }

                mOriginX = mCurrentX = 0;

        }

        return false;

    }

    /**
     * Sets the position of the right hand side of the menu.
     *
     * @param right
     */
    public void setMenuRightPosition(float right) {

        right = right > mMenuWidth ? mMenuWidth : right;

        FrameLayout menu = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
        FrameLayout root = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_content_frame);
        FrameLayout shadow = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_shadow_frame);
        menu.setVisibility(View.VISIBLE);

        //update sizes and margins for sliding menu
        RelativeLayout.LayoutParams mp = new RelativeLayout.LayoutParams(mMenuWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams sp = new RelativeLayout.LayoutParams(30, RelativeLayout.LayoutParams.MATCH_PARENT);

        if (mType != MENU_TYPE_SLIDEOVER) {
            mp.leftMargin = (int) right - mMenuWidth;
            mp.rightMargin = -(int) right;
        }

        rp.rightMargin = -(int) right;
        rp.leftMargin = (int) right;
        sp.rightMargin = -(int) right - mShadowWidth;
        sp.leftMargin = (int) right - mShadowWidth;

        menu.setLayoutParams(mp);
        root.setLayoutParams(rp);
        shadow.setLayoutParams(sp);
    }

    /**
     * Toggles the menu.
     */
    public void toggleMenu() {

        switch (mType) {
            case MENU_TYPE_SLIDEOVER:
                toggleSlideOverMenu();
                break;
            case MENU_TYPE_PARALLAX:
                toggleSlidingMenu(mAnimationDuration / 2);
                break;
            default: /*MENU_TYPE_SLIDING*/
                toggleSlidingMenu();
                break;
        }


    }

    /**
     * Animates the menu from a given position to another.
     *
     * @param start the position to start the animation.
     * @param end   the position to end the animation.
     */
    public void AnimateMenuPosition(final int start, final int end) {

        switch (mType) {
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
     *
     * @param start
     * @param end
     */
    public void animateSlideOverMenuPosition(final int start, final int end) {
        View contentView = findViewById(R.id.ws_munday_slidingmenu_content_frame);
        contentView.clearAnimation();
        contentView.setDrawingCacheEnabled(true);

        MarginAnimation contentAnimation = new MarginAnimation(contentView, start, end, mInterpolator);
        contentAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (end > 0) {
                    ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
                    //unhide menu.
                    v1.setVisibility(View.VISIBLE);
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (end <= 0) {
                    ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
                    //Hide the menu to reduce overdraw.
                    v1.setVisibility(View.GONE);
                }
            }
        });

        contentAnimation.setDuration(mAnimationDuration);
        contentView.startAnimation(contentAnimation);

        View shadowView = findViewById(R.id.ws_munday_slidingmenu_shadow_frame);
        shadowView.clearAnimation();
        shadowView.setDrawingCacheEnabled(true);

        int shadowStart = start - mShadowWidth;
        int shadowEnd = end - mShadowWidth;

        MarginAnimation shadowAnimation = new MarginAnimation(shadowView, shadowStart, shadowEnd, mInterpolator);
        shadowAnimation.setDuration(mAnimationDuration);
        shadowView.startAnimation(shadowAnimation);

        mIsMenuOpen = start <= end;
    }

    /**
     * Convenience function to toggle a slide over menu all the way open or closed.
     */
    public void toggleSlideOverMenu() {
        if (mIsMenuOpen) {
            animateSlideOverMenuPosition(mMenuWidth, 0);
        } else {
            animateSlideOverMenuPosition(0, mMenuWidth);
        }

    }

    /**
     * Animates both the menu and content areas independently.
     *
     * @param start
     * @param end
     * @param menuAnimationDuration the time in millis in which the menu animation should finish.
     *                              If this value is different from the one set using
     *                              {@link ws.munday.slidingmenu.SlidingMenuActivity#setAnimationDuration(long)}
     *                              then the menu is considered parralax.
     */
    public void animateSlidingMenuPosition(final int start, final int end, long menuAnimationDuration) {

        boolean parallax = menuAnimationDuration != mAnimationDuration;

        View contentView = findViewById(R.id.ws_munday_slidingmenu_content_frame);
        contentView.clearAnimation();
        contentView.setDrawingCacheEnabled(true);

        MarginAnimation contentAnimation = new MarginAnimation(contentView, start, end, mInterpolator);
        contentAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (end > 0) {
                    ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
                    v1.setVisibility(View.VISIBLE);
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (end <= 0) {
                    ViewGroup v1 = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
                    v1.setVisibility(View.GONE);
                }
            }
        });

        contentAnimation.setDuration(mAnimationDuration);
        contentView.startAnimation(contentAnimation);

        MarginAnimation menuAnimation;
        View menuView = findViewById(R.id.ws_munday_slidingmenu_menu_frame);
        menuView.clearAnimation();
        menuView.setDrawingCacheEnabled(true);


        menuAnimation = new MarginAnimation(menuView, start - mMenuWidth, end - mMenuWidth, mInterpolator);
        if(start > end && parallax) {
            long multiplier = Math.max(menuAnimationDuration / mAnimationDuration, 1 );
            menuAnimation.setDuration(menuAnimationDuration / multiplier);
        }else{
            menuAnimation.setDuration(menuAnimationDuration);
        }
        menuView.startAnimation(menuAnimation);

        View shadowView = findViewById(R.id.ws_munday_slidingmenu_shadow_frame);
        shadowView.clearAnimation();
        shadowView.setDrawingCacheEnabled(true);

        int shadowStart = start - mShadowWidth;
        int shadowEnd = end - mShadowWidth;

        MarginAnimation shadowAnimation = new MarginAnimation(shadowView, shadowStart, shadowEnd, mInterpolator);
        shadowAnimation.setDuration(mAnimationDuration);
        shadowView.startAnimation(shadowAnimation);


        mIsMenuOpen = start <= end;
    }

    /**
     * Convenience function for toggling a standard sliding menu.
     */
    public void toggleSlidingMenu() {
        toggleSlidingMenu(mAnimationDuration);
    }

    /**
     * Convenience function for toggling a parallax menu.
     *
     * @param menuAnimationDuration
     */
    public void toggleSlidingMenu(long menuAnimationDuration) {

        if (mIsMenuOpen) {
            animateSlidingMenuPosition(mMenuWidth, 0, menuAnimationDuration);
        } else {
            animateSlidingMenuPosition(0, mMenuWidth, menuAnimationDuration);
        }

    }

    public void initMenu(boolean isConfigChange) {

        switch (mType) {

            case MENU_TYPE_SLIDEOVER:
                initSlideOverMenu(isConfigChange);
                break;

            default:
                initSlideOutMenu(isConfigChange);
                break;

        }
    }

    @SuppressWarnings("deprecation")
    public void initSlideOutMenu(boolean isConfigChange) {
        //get menu and main layout
        FrameLayout menu = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
        FrameLayout root = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_content_frame);
        FrameLayout shadow = (FrameLayout) findViewById(R.id.ws_munday_slidingmenu_shadow_frame);

        //get screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int x = 0;
        try {
            Method m = Display.class.getMethod("getSize", new Class[]{});
            m.invoke(display, size);
            x = size.x;
        } catch (NoSuchMethodException nsme) {
            x = display.getWidth();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        //make sure that the content doesn't slide all the way off screen
        int minContentWidth = Utility.dipsToPixels(this, mMinContentWidthDps);
        mMenuWidth = Math.min(x - minContentWidth, mMaxMenuWidthDps);

        //update sizes and margins for sliding menu
        RelativeLayout.LayoutParams mp = new RelativeLayout.LayoutParams(mMenuWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(x, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams sp = new RelativeLayout.LayoutParams(x - mShadowWidth, RelativeLayout.LayoutParams.MATCH_PARENT);

        if (isConfigChange) {
            if (mIsMenuOpen) {
                mp.leftMargin = 0;
                rp.leftMargin = mMenuWidth;
                rp.rightMargin = -mMenuWidth;
                sp.leftMargin = mMenuWidth - mShadowWidth;
                sp.rightMargin = -mMenuWidth - mShadowWidth;
            } else {
                mp.leftMargin = -mMenuWidth;
                rp.leftMargin = 0;
                rp.rightMargin = 0;
                sp.leftMargin = -mShadowWidth;
                sp.rightMargin = -mShadowWidth;
            }
        } else {
            mp.leftMargin = -mMenuWidth;
            rp.leftMargin = 0;
            rp.rightMargin = -mMenuWidth;
            sp.leftMargin = -mShadowWidth;
            sp.rightMargin = -mMenuWidth - mShadowWidth;
            mIsMenuOpen = false;
        }

        menu.setLayoutParams(mp);
        menu.requestLayout();

        root.setLayoutParams(rp);
        root.requestLayout();

        shadow.setLayoutParams(sp);
        shadow.requestLayout();
    }

    @SuppressWarnings("deprecation")
    public void initSlideOverMenu(boolean isConfigChange) {
        //get menu and main layout
        ViewGroup menu = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_menu_frame);
        //ViewGroup content = (ViewGroup) findViewById(R.id.ws_munday_slidingmenu_content_frame);

        //get screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int x = 0;
        try {
            Method m = Display.class.getMethod("getSize", new Class[]{});
            m.invoke(display, size);
            x = size.x;
        } catch (NoSuchMethodException nsme) {
            x = display.getWidth();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //make sure that the content doesn't slide all the way off screen
        int minContentWidth = Utility.dipsToPixels(this, mMinContentWidthDps);
        mMenuWidth = Math.min(x - minContentWidth, mMaxMenuWidthDps);

        //update sizes and margins for sliding menu
        menu.setLayoutParams(new RelativeLayout.LayoutParams(mMenuWidth, RelativeLayout.LayoutParams.MATCH_PARENT));
        menu.requestLayout();

        if (isConfigChange) {
            if (mIsMenuOpen) {
                setMenuRightPosition(mMenuWidth);
            } else {
                setMenuRightPosition(0);
            }
        }
    }


}

