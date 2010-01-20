package chau.nguyen.calendar.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import chau.nguyen.INavigator;
import chau.nguyen.R;
import chau.nguyen.VNMDayViewActivity;
import chau.nguyen.calendar.VietCalendar;

public class VNMDayViewer extends LinearLayout implements OnCreateContextMenuListener {
	private static int VERTICAL_FLING_THRESHOLD = 5;
	private static final int CREATE_NEW_EVENT = 1;
	private static final int SELECT_DATE = 2;
	private static final int DATE_DIALOG_ID = 0;
	protected INavigator navigator;
	
	private TextView dayOfMonthText;
	private TextView dayOfWeekText;
	private TextView noteText;
	
	protected TextView vnmHourText;
	protected TextView vnmHourInText;
	protected TextView vnmDayOfMonthText;
	protected TextView vnmDayOfMonthInText;
	protected TextView vnmMonthText;
	protected TextView vnmMonthInText;
	protected TextView vnmYearText;
	protected TextView vnmYearInText;
	
	private Date displayDate;
	
	private VNMDayViewActivity monthActivity;
	private GestureDetector gestureDetector;
	private ContextMenuClickHandler contextMenuClickHandler;
	static private String[] dayInVietnamese;
	
	static {
		dayInVietnamese = new String[] {"Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
	}

	public VNMDayViewer(VNMDayViewActivity context, INavigator navigator) {
		super(context);
		init(context, navigator);
	}
	
	public VNMDayViewer(VNMDayViewActivity context, INavigator navigator, AttributeSet attrs) {
		super(context, attrs);
		init(context, navigator);
	}
	
	private void init(VNMDayViewActivity monthActivity, INavigator navigator) {
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.vnm_day_viewer, this, true);
		
		this.monthActivity = monthActivity;
		this.navigator = navigator;
		
		this.dayOfMonthText = (TextView)findViewById(R.id.dayOfMonthText);
		this.dayOfWeekText = (TextView)findViewById(R.id.dayOfWeekText);
		this.noteText = (TextView)findViewById(R.id.noteText);
		
		this.vnmHourText = (TextView) findViewById(R.id.vnmHourText);
		this.vnmHourInText = (TextView) findViewById(R.id.vnmHourInText);
		this.vnmDayOfMonthText = (TextView) findViewById(R.id.vnmDayOfMonthText);
		this.vnmDayOfMonthInText = (TextView) findViewById(R.id.vnmDayOfMonthInText);
		this.vnmMonthText = (TextView) findViewById(R.id.vnmMonthText);
		this.vnmMonthInText = (TextView) findViewById(R.id.vnmMonthInText);
		this.vnmYearText = (TextView) findViewById(R.id.vnmYearText);
		this.vnmYearInText = (TextView) findViewById(R.id.vnmYearInText);
		
		this.displayDate = new Date();
		this.setDate(this.displayDate);
		
		setOnCreateContextMenuListener(this);
		this.contextMenuClickHandler = new ContextMenuClickHandler();
		this.gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				performLongClick();
			}
			
			@Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                    float velocityY) {
                // The user might do a slow "fling" after touching the screen
                // and we don't want the long-press to pop up a context menu.
                // Setting mLaunchDayView to false prevents the long-press.
                int distanceX = Math.abs((int) e2.getX() - (int) e1.getX());
                int distanceY = Math.abs((int) e2.getY() - (int) e1.getY());
                if (distanceX < VERTICAL_FLING_THRESHOLD || distanceX < distanceY) {
                    return false;
                }

                // Switch to a different month
                Calendar calendar = Calendar.getInstance();
    			calendar.setTime(displayDate);
                if (velocityX < 0) {
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					Date afterDate = calendar.getTime();
					VNMDayViewer.this.monthActivity.gotoTime(afterDate);
				} else {
					calendar.add(Calendar.DAY_OF_MONTH, -1);
					Date beforeDate = calendar.getTime();
					VNMDayViewer.this.monthActivity.gotoTime(beforeDate);
				}

                return true;
            }
			
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
		});
		
	}
	
	public void setDate(Date date) {
		this.displayDate = date;
		this.monthActivity.setTitle(this.getDisplayDayText());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		this.dayOfMonthText.setText(dayOfMonth + "");
		this.dayOfWeekText.setText(dayInVietnamese[dayOfWeek - 1]);
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int month = calendar.get(Calendar.MONTH) + 1;
		
		int year = calendar.get(Calendar.YEAR);
		int[] lunars = VietCalendar.convertLunar2SolarInVietnam(date);
		
		this.vnmHourText.setText(hour + ":" + minute);
		this.vnmDayOfMonthText.setText(lunars[0] + "");
		this.vnmMonthText.setText(lunars[1] + "");
		this.vnmYearText.setText(lunars[2] + "");
		
		String[] vnmCalendarTexts = VietCalendar.getCanChiInfo(lunars[0], lunars[1], lunars[2], dayOfMonth, month, year);
		
		this.vnmHourInText.setText(vnmCalendarTexts[VietCalendar.HOUR]);
		this.vnmDayOfMonthInText.setText(vnmCalendarTexts[VietCalendar.DAY]);
		this.vnmMonthInText.setText(vnmCalendarTexts[VietCalendar.MONTH]);
		this.vnmYearInText.setText(vnmCalendarTexts[VietCalendar.YEAR]);
		
		
	}
	
	
	public void setNote(String note) {
		this.noteText.setText(note);
	}
	
	public Date getDisplayDate() {
		return displayDate;
	}

	public String getDisplayDayText() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return simpleDateFormat.format(this.displayDate);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return this.gestureDetector.onTouchEvent(event);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuItem item;
		
		item = menu.add(0, SELECT_DATE, 0, "Chọn ngày");
		item.setOnMenuItemClickListener(this.contextMenuClickHandler);
		item = menu.add(0, CREATE_NEW_EVENT, 0, "Thêm sự kiện");
		item.setOnMenuItemClickListener(this.contextMenuClickHandler);
	}
	
	private class ContextMenuClickHandler implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case CREATE_NEW_EVENT:
				
				break;
			case SELECT_DATE:
				VNMDayViewer.this.monthActivity.showDialog(DATE_DIALOG_ID);
				break;
			default:
				break;
			}
			return false;
		}
		
	}

}
