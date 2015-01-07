package net.loonggg.test;

import net.loonggg.view.CustomerDateDialog;
import net.loonggg.view.CustomerDateDialog.DateDialogListener;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private int h, m;
	private CustomerDateDialog dialog;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String datetime = DateFormat.format("kk:mm",
						System.currentTimeMillis()).toString();
				String[] strs = datetime.split(":");
				h = Integer.parseInt(strs[0]);
				if (strs[1].charAt(0) == '0') {
					m = strs[1].charAt(1);
				} else {
					m = Integer.parseInt(strs[1]);
				}
				dialog = new CustomerDateDialog(MainActivity.this, h, m);
				dialog.show();
				dialog.setOnDateDialogListener(new DateDialogListener() {
					@Override
					public void getDate() {
						Toast.makeText(
								MainActivity.this,
								"时间是:" + dialog.getSettingHour() + "点"
										+ dialog.getSettingMinute() + "分",
								Toast.LENGTH_LONG).show();
					}
				});

			}
		});

	}

}
