package net.loonggg.view;

import net.loonggg.test.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class TimeWheel extends LinearLayout {
	private String TAG = "TEST";
	private Button setBtn;
	private TextView arrow_up;
	private TextView tv01, tv02;
	private ScrollView sv01, sv02;
	private LinearLayout llTimeWheel;
	private int lastY;
	private int flag;// 标记时分
	private int itemHeight;// 每一行的高度
	private int pHour, pMinute;// 初始化时显示的时分时间
	private int setHour, setMinute;

	public TimeWheel(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param hour
	 *            初始化时显示的时
	 * @param minute
	 *            初始化时显示的分
	 */
	public TimeWheel(Context context, final int hour, final int minute) {
		this(context);
		View.inflate(context, R.layout.time_wheel, this);
		tv01 = (TextView) this.findViewById(R.id.tv01);
		tv02 = (TextView) this.findViewById(R.id.tv02);
		sv01 = (ScrollView) this.findViewById(R.id.sv01);
		sv02 = (ScrollView) this.findViewById(R.id.sv02);
		setBtn = (Button) findViewById(R.id.setBtn);
		arrow_up = (TextView) this.findViewById(R.id.arrow_up);
		this.pHour = hour;
		this.pMinute = minute;
		setHour = pHour;
		setMinute = pMinute;

		llTimeWheel = (LinearLayout) this.findViewById(R.id.ll_time_wheel);
		setHourDial(tv01);
		setMinuteDial(tv02);

		sv01.setOnTouchListener(tListener);
		sv02.setOnTouchListener(tListener);

		final ViewTreeObserver observer = sv01.getViewTreeObserver();// observer
																		// 作用当视图完全加载进来的时候再取控件的高度，否则取得值是0
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			public void onGlobalLayout() {
				int tvHeight = tv02.getHeight();
				itemHeight = tvHeight / 180;
				if (sv01.getViewTreeObserver().isAlive()) {
					sv01.getViewTreeObserver().removeGlobalOnLayoutListener(
							this);
				}
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, (itemHeight * 3)
								+ arrow_up.getHeight() * 2);
				llTimeWheel.setLayoutParams(params);
				sv01.setLayoutParams(new LinearLayout.LayoutParams(tv02
						.getWidth(), (itemHeight * 3)));
				sv02.setLayoutParams(new LinearLayout.LayoutParams(tv02
						.getWidth(), (itemHeight * 3)));
				sv01.scrollTo(0, (pHour + 23) * itemHeight);
				sv02.scrollTo(0, (pMinute + 59) * itemHeight);

			}
		});

		setBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(),
						"设置的时间是:" + setHour + "时" + setMinute + "分",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	private OnTouchListener tListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (v == sv01) {
				flag = 1;
			} else {
				flag = 2;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				final ScrollView sv = (ScrollView) v;
				lastY = sv.getScrollY();
				System.out.println("lastY" + lastY);
				handler.sendMessageDelayed(handler.obtainMessage(0, v), 50);
			}
			return false;
		}
	};

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			ScrollView sv = (ScrollView) msg.obj;

			if (msg.what == 0) {
				if (lastY == sv.getScrollY()) {

					int num = lastY / itemHeight;
					int over = lastY % itemHeight;
					Log.i(TAG, "num == " + num + "  over == " + over);
					if (over > itemHeight / 2) {// 超过一半滚到下一格
						locationTo((num + 1) * itemHeight, sv, flag);
					} else {// 不到一半滚回上一格
						locationTo(num * itemHeight, sv, flag);
					}
				} else {
					lastY = sv.getScrollY();
					handler.sendMessageDelayed(handler.obtainMessage(0, sv), 50);// 滚动还没停止隔50毫秒再判断
				}
			}

		};
	};

	private void locationTo(int position, ScrollView scrollview, int flag) {
		Log.i(TAG, "position == " + position);
		switch (flag) {
		case 1:
			int mPosition = 0;
			if (position <= 23 * itemHeight) {
				mPosition = position + 24 * itemHeight;
				scrollview.scrollTo(0, mPosition);
			} else if (position >= 48 * itemHeight) {
				mPosition = position - 24 * itemHeight;
				scrollview.scrollTo(0, mPosition);
			} else {
				mPosition = position;
				scrollview.smoothScrollTo(0, position);
			}
			setHour = (mPosition / itemHeight - 23) % 24;
			break;

		case 2:
			int hPosition = 0;
			if (position <= 57 * itemHeight) {
				hPosition = position + 60 * itemHeight;
				scrollview.scrollTo(0, hPosition);
			} else if (position >= 120 * itemHeight) {
				hPosition = position - 60 * itemHeight;
				scrollview.scrollTo(0, hPosition);
			} else {
				hPosition = position;
				scrollview.smoothScrollTo(0, position);
			}
			setMinute = (hPosition / itemHeight) % 60 + 1;
			break;
		}

	}

	/**
	 * 设置分刻度盘
	 * 
	 * @param tv
	 */

	private void setMinuteDial(TextView tv) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 60; j++) {
				if (j <= 9) {
					buff.append("0" + j);
				} else {
					buff.append(j + "");
				}
			}
		}

		tv.setText(buff);
	}

	/**
	 * 设置时刻度盘
	 * 
	 * @param tv
	 */
	private void setHourDial(TextView tv) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 24; j++) {
				if (j <= 9) {
					buff.append("0" + j);
				} else {
					buff.append(j + "");
				}
			}

		}

		tv.setText(buff);
	}

	public void setpHour(int pHour) {
		this.pHour = pHour;
	}

	public void setpMinute(int pMinute) {
		this.pMinute = pMinute;
	}
}
