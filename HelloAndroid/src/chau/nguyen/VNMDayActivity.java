package chau.nguyen;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.ViewSwitcher;
import chau.nguyen.calendar.ui.VNMDatePickerDialog;
import chau.nguyen.calendar.ui.VNMDayViewer;

public class VNMDayActivity extends VNMCalendarViewActivity {
	public static final int SELECT_DATE = 1;
	
	private static final int MENU_SELECT_DATE = 1;
	private static final int MENU_SELECT_TODAY = 2;
	private static final int MENU_MONTH_VIEW = 3;
	private static final int MENU_ADD_EVENT = 4;
	private static final int MENU_ABOUT = 5;
	//private static int MENU_SETTINGS = 4;
	public static final int DATE_DIALOG_ID = 0;
	public static final int ABOUT_DIALOG_ID = 1;
	
	private Date date;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.switcher = new ViewSwitcher(this);
        setContentView(this.switcher);
        BackgroundManager.init(this);
        
        this.date = new Date();         
        this.switcher.addView(new VNMDayViewer(this, this));
        Drawable background = BackgroundManager.getRandomBackground();
        this.switcher.getCurrentView().setBackgroundDrawable(background);
        this.switcher.addView(new VNMDayViewer(this, this));
        this.inAnimationLeft.initialize(this.switcher.getWidth(), this.switcher.getHeight(), this.switcher.getWidth(), this.switcher.getHeight());
        this.inAnimationRight.initialize(this.switcher.getWidth(), this.switcher.getHeight(), this.switcher.getWidth(), this.switcher.getHeight());
        this.outAnimationLeft.initialize(this.switcher.getWidth(), this.switcher.getHeight(), this.switcher.getWidth(), this.switcher.getHeight());
        this.outAnimationRight.initialize(this.switcher.getWidth(), this.switcher.getHeight(), this.switcher.getWidth(), this.switcher.getHeight());                
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_SELECT_DATE, 0, "Chọn ngày").setIcon(android.R.drawable.ic_menu_day);
    	menu.add(0, MENU_SELECT_TODAY, 0, "Hôm nay").setIcon(android.R.drawable.ic_menu_today);    	
    	menu.add(0, MENU_MONTH_VIEW, 0, "Xem tháng").setIcon(android.R.drawable.ic_menu_month);
    	menu.add(0, MENU_ADD_EVENT, 0, "Thêm sự kiện").setIcon(android.R.drawable.ic_menu_add);
    	menu.add(0, MENU_ABOUT, 0, "Giới thiệu").setIcon(android.R.drawable.ic_menu_info_details);;
    	//menu.add(0, MENU_SETTINGS, 0, "Tùy chọn").setIcon(android.R.drawable.ic_menu_preferences);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == MENU_MONTH_VIEW) {
    		showMonthView();
    	} else if (item.getItemId() == MENU_SELECT_DATE) {
    		selectDate();
    	} else if (item.getItemId() == MENU_SELECT_TODAY) {
    		gotoTime(new Date());
    	} else if (item.getItemId() == MENU_ADD_EVENT) {
    		addEvent();
    	} else if (item.getItemId() == MENU_ABOUT) {
    		showDialog(ABOUT_DIALOG_ID);
    	}
    	return true;
    }    	

	public void gotoTime(Date date) {		
		VNMDayViewer currentView = (VNMDayViewer)this.switcher.getCurrentView();		
		Date currentDate = currentView.getDisplayDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(date);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		if (date.after(currentDate)) {
			if (dayOfMonth == currentDayOfMonth) {
				this.switcher.setInAnimation(this.inAnimationUp);
				this.switcher.setOutAnimation(this.outAnimationUp);
			} else {
				this.switcher.setInAnimation(this.inAnimationLeft);
				this.switcher.setOutAnimation(this.outAnimationLeft);
			}
		} else if (date.before(currentDate)) {
			if (dayOfMonth == currentDayOfMonth) {
				this.switcher.setInAnimation(this.inAnimationDown);
				this.switcher.setOutAnimation(this.outAnimationDown);
			} else {
				this.switcher.setInAnimation(this.inAnimationRight);
				this.switcher.setOutAnimation(this.outAnimationRight);
			}
		}
		
		VNMDayViewer next = (VNMDayViewer)this.switcher.getNextView();
		next.setBackgroundDrawable(BackgroundManager.getRandomBackground());
		next.setDate(date);
		this.date = date;
		this.switcher.showNext();
	}
	
	public void selectDate() {
		showDialog(DATE_DIALOG_ID);
	}
	
	public void showMonthView() {
		Intent monthIntent = new Intent(this, VNMMonthActivity.class);
		//startActivity(monthIntent);
		startActivityForResult(monthIntent, SELECT_DATE);
	}	
	
	public void addEvent() {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setClassName("com.android.calendar", "com.android.calendar.EditEvent");
		Calendar eventCal = Calendar.getInstance();
		eventCal.setTime(this.date);
		eventCal.set(Calendar.HOUR_OF_DAY, 8);
		eventCal.set(Calendar.MINUTE, 0);
		intent.putExtra("beginTime", eventCal.getTimeInMillis());
		eventCal.set(Calendar.HOUR_OF_DAY, 9);
        intent.putExtra("endTime", eventCal.getTimeInMillis()); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case SELECT_DATE:
			if (resultCode == RESULT_CANCELED)
				return;
			long result = data.getLongExtra(VNMMonthActivity.SELECTED_DATE_RETURN, 0);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(result);
			gotoTime(cal.getTime());
			break;
		default:
			
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    	case DATE_DIALOG_ID:
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime(this.date);
	    		return new VNMDatePickerDialog(this, mDateSetListener, 
	    				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	    	case ABOUT_DIALOG_ID:
	    		AlertDialog.Builder builder;
	    		AlertDialog aboutDialog;

	    		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    		View layout = inflater.inflate(R.layout.about,
	    		                               (ViewGroup) findViewById(R.id.layoutRoot));

	    		builder = new AlertDialog.Builder(this);
	    		builder.setView(layout);
	    		aboutDialog = builder.create();
	    		aboutDialog.setTitle("Lịch Việt");
	    		aboutDialog.setIcon(R.drawable.icon);
	    		return aboutDialog;
	    }
	    return null;
	}
	
	// the callback received when the user "sets" the date in the dialog
	private VNMDatePickerDialog.OnDateSetListener mDateSetListener = new VNMDatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, boolean isSolarSelected, int year, int monthOfYear,
				int dayOfMonth) {
			VNMDayViewer currentView = (VNMDayViewer)switcher.getCurrentView();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			Log.i("VNMDayActivity", "The day that you selected is: " + dayOfMonth + "/" + monthOfYear + "/" + year);
			if (isSolarSelected) {
				currentView.setDate(cal.getTime());
			} else {
				currentView.setLunarDate(dayOfMonth, monthOfYear, year);
			}
		}
	};
}