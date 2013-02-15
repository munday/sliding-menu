package ws.munday.slidingmenu.test;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import ws.munday.slidingmenu.test.R;
import ws.munday.slidingmenu.SlidingMenuActivity;

public class TestActivity extends SlidingMenuActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.ws_munday_slidingmenu_test_menu, R.layout.ws_munday_slidingmenu_test_content);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView) findViewById(R.id.content_content);
		
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		
	}
	
	
}
