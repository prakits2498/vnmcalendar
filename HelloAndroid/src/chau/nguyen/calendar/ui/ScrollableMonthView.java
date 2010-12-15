package chau.nguyen.calendar.ui;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import chau.nguyen.BackgroundManager;
import chau.nguyen.calendar.ui.MonthView.OnDateSelectedListener;
import chau.nguyen.calendar.widget.VerticalScrollView;

public class ScrollableMonthView extends VerticalScrollView {
	private OnDateChangedListener onDateChangedListener;
	private OnDateSelectedListener onDateSelectedListener;
	
	public ScrollableMonthView(Context context) {
		super(context);		
		this.setOnScreenSelectedListener(onScreenSelectedListener);
	}
	
	public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
		this.onDateChangedListener = onDateChangedListener;
	}
	public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
		this.onDateSelectedListener = onDateSelectedListener;
	}

	public void setDate(Date date) {
		if (this.getChildCount() == 3) {
			MonthView previousView = (MonthView)getChildAt(0);
			previousView.setDate(addYears(date, -1));
			
			MonthView currentView = (MonthView)getChildAt(1);
			currentView.setDate(date);
			
			MonthView nextView = (MonthView)getChildAt(2);
			nextView.setDate(addYears(date, 1));
    	} else {
    		this.removeAllViews();
    		
	    	MonthView previousView = new MonthView(getContext());
	    	previousView.setOnDateSelectedListener(onDateSelectedListener);
			previousView.setDate(addYears(date, 1));
			previousView.setBackgroundDrawable(BackgroundManager.getRandomBackground());
			this.addView(previousView);
			 
			MonthView currentView = new MonthView(getContext());
			currentView.setOnDateSelectedListener(onDateSelectedListener);
			currentView.setDate(date);
			currentView.setBackgroundDrawable(BackgroundManager.getRandomBackground());
			this.addView(currentView);
			 
			MonthView nextView = new MonthView(getContext());
			nextView.setOnDateSelectedListener(onDateSelectedListener);
			nextView.setDate(addYears(date, 1));
			nextView.setBackgroundDrawable(BackgroundManager.getRandomBackground());
			this.addView(nextView);
    	}
				
		this.setOnScreenSelectedListener(null);
		this.showScreen(1);
		this.setOnScreenSelectedListener(onScreenSelectedListener);
	}
	
	public Date getDate() {
		MonthView currentView = (MonthView)getChildAt(1);
		return currentView.getDate();
	}
	
	protected void prepareOtherViews(int selectedIndex) {
    	MonthView currentView = (MonthView)this.getChildAt(selectedIndex);
    	Date currentDate = currentView.getDate();    	
    	if (selectedIndex == 0) {
    		// remove last view, add new view at the beginning
    		MonthView previousView = new MonthView(getContext());
    		previousView.setOnDateSelectedListener(onDateSelectedListener);
    		previousView.setDate(addYears(currentDate, -1));
    		previousView.setBackgroundDrawable(BackgroundManager.getRandomBackground());
    		this.prependView(previousView);    	

    		if (this.getChildCount() > 2) {
    			this.removeViewAt(2);
    		}
    	} else if (selectedIndex == 2) {    		
    		// remove first view, append new view at the end
    		MonthView nextView = new MonthView(getContext());
    		nextView.setOnDateSelectedListener(onDateSelectedListener);
    		nextView.setDate(addYears(currentDate, +1));
    		nextView.setBackgroundDrawable(BackgroundManager.getRandomBackground());    		
    		this.addView(nextView);
    		
    		if (this.getChildCount() > 3) {
    			this.removeFirstView();
    		}
    	}
    	
    	if (this.onDateChangedListener != null) {
    		this.onDateChangedListener.onDateChanged(currentDate);
    	}
	}
	
	private Date addYears(Date date, int years) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.YEAR, years);
    	return cal.getTime();
    }
	
	private OnScreenSelectedListener onScreenSelectedListener = new OnScreenSelectedListener() {
		public void onSelected(int selectedIndex) {				
			prepareOtherViews(selectedIndex);
		}
    };
	
	public interface OnDateChangedListener {
		void onDateChanged(Date date);
	}
}
