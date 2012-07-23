package ws.munday.slidingmenu;

import android.content.Context;

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
}
