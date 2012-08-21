package ws.munday.slidingmenu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

public class MarginAnimation extends Animation {

	private int mEnd;
	private int mStart;
	private int mChange;
	private View mView;
	
	public MarginAnimation(View v, int marginStart, int marginEnd, Interpolator i){

		mStart = marginStart;
		mEnd = marginEnd;
		mView = v;
		
		mChange = mEnd - mStart;
		setInterpolator(i);
		
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		float change = mChange * interpolatedTime;
		LayoutParams lp = (LayoutParams) mView.getLayoutParams();
		lp.setMargins( (int)(mStart + change), 0, -(int)(mStart + change), 0);
		mView.setLayoutParams(lp);
		
		super.applyTransformation(interpolatedTime, t);
	}

}
