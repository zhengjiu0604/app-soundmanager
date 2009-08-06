/**
 * Copyright 2009 Mike Partridge
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.roozen.SoundManager.schedule;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Based on the tutorial from anddev.org at
 * http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 * 
 * @author Mike Partridge
 */
public class ScheduleListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Schedule> mItems = new ArrayList<Schedule>();
    
    /**
     * @param context
     */
    public ScheduleListAdapter(Context context) {
        mContext = context;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mItems.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mItems.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    /**
     * @param s
     */
    public void addItem(Schedule s) {
        mItems.add(s);
    }
    
    /**
     * @param sl
     */
    public void setItems(ArrayList<Schedule> sl) {
        mItems = sl;
    }
    
    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#isEnabled(int)
     */
    @Override
    public boolean isEnabled(int position) {
        try {
             return mItems.get(position).isEnabled();
        }
        catch (IndexOutOfBoundsException e){
             return super.isEnabled(position);
        }
   } 

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ScheduleView scheduleView;
        
        Schedule schedule = mItems.get(position);
        
        /*
         * if the View already exists, set its values; otherwise give it the Schedule to pull them itself
         */
        if (convertView != null &&
                convertView instanceof ScheduleView) {
            scheduleView = (ScheduleView) convertView;
            scheduleView.setFromSchedule(schedule);
        }
        else {
            scheduleView = new ScheduleView(mContext, schedule);
        }
        
        return scheduleView;
    }

}
