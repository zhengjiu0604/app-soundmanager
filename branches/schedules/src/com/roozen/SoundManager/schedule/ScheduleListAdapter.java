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
 * @author Mike Partridge
 */
public class ScheduleListAdapter extends BaseAdapter {

    private Context mContext;
    
    private ArrayList<Schedule> mItems = new ArrayList<Schedule>();
    
    public ScheduleListAdapter(Context context) {
        mContext = context;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mItems.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    public void addItem(Schedule s) {
        mItems.add(s);
    }
    
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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ScheduleView scheduleView;
        
        Schedule schedule = mItems.get(position);
        
        if (convertView != null &&
                convertView instanceof ScheduleView) {
            scheduleView = (ScheduleView) convertView;
            scheduleView.setDay0(schedule.isDay0());
            scheduleView.setDay1(schedule.isDay1());
            scheduleView.setDay2(schedule.isDay2());
            scheduleView.setDay3(schedule.isDay3());
            scheduleView.setDay4(schedule.isDay4());
            scheduleView.setDay5(schedule.isDay5());
            scheduleView.setDay6(schedule.isDay6());
            scheduleView.setStartTime(schedule.getStartTime());
            scheduleView.setEndTime(schedule.getEndTime());
            scheduleView.setVolume(schedule.getVolume());
        }
        else {
            scheduleView = new ScheduleView(mContext, schedule);
        }
        
        return scheduleView;
    }

}
