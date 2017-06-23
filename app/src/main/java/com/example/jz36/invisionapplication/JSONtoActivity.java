package com.example.jz36.invisionapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jz36 on 22.06.17.
 */

public class JSONtoActivity {
    private Activity activity;
    private LinearLayout topPanel;
    private LinearLayout scrollPanel;
    private JSONObject json;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSSSSS", Locale.forLanguageTag("ru"));
    private final String[] months = new String[]{"Январе", "Феврале", "Марте", "Апреле", "Мае", "Июне",
            "Июле", "Августе", "Сентябре", "Октябре", "Ноябре", "Декабре"};

    public JSONtoActivity(Activity activity, LinearLayout topPanel, LinearLayout scrollPanel, JSONObject jsonObject){
        this.activity = activity;
        this.topPanel = topPanel;
        this.scrollPanel = scrollPanel;
        json = jsonObject;
    }

    public void setOrganizationName() throws JSONException{
        JSONObject organization = json.getJSONObject("organization");
        TextView organizationName = (TextView) topPanel.findViewById(R.id.organizationName);
        organizationName.setText((String) organization.get("name"));
    }

    public void setOrganizationStatus() throws JSONException{
        JSONObject organizationStatus = json.getJSONObject("organization").getJSONObject("status");
        TextView organizationStatusText = (TextView) topPanel.findViewById(R.id.statusName);
        organizationStatusText.setText((String) organizationStatus.get("name"));
        if ((int) organizationStatus.get("statusId") == 0){
            topPanel.setBackgroundResource(R.drawable.background_with_shadow);
        }
        else{
            topPanel.setBackgroundResource(R.drawable.background_with_shadow_red);
        }
    }

    public void setEvents() throws JSONException, ParseException {
        JSONArray events = getSortedEvents();
        Log.d("My log", events.toString());
        Date currentDate = dateFormat.parse(events.getJSONObject(0).getString("date"));
        Calendar calendarCurrentDate = Calendar.getInstance();
        calendarCurrentDate.setTime(currentDate);
        int currentMonthNumber = calendarCurrentDate.get(Calendar.MONTH);
        String currentMonth = months[currentMonthNumber];
        int countEventsInMonth = 0;
        String sdan = "";
        String otchet = "";

        TextView subtitle = createSubtitle();
        LinearLayout eventsContainer = createMonthEventsContainer();

        scrollPanel.addView(subtitle);
        scrollPanel.addView(eventsContainer);

        for (int i = 0; i < events.length(); i++){
            JSONObject event = events.getJSONObject(i);
            Date eventDate = dateFormat.parse(event.getString("date"));
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTime(eventDate);
            int eventMonthNumber = eventCalendar.get(Calendar.MONTH);


            if (eventMonthNumber == currentMonthNumber){
                countEventsInMonth++;





            }
            else{
                if (countEventsInMonth == 1){
                    sdan = "сдан";
                    otchet = "отчет";
                } else if( countEventsInMonth > 1 && countEventsInMonth < 5) {
                    sdan = "сданы";
                    otchet = "отчета";
                } else if(countEventsInMonth > 4){
                    sdan = "сданы";
                    otchet = "отчетов";

                }

                subtitle.setText("В " + currentMonth + " " + sdan + " " + countEventsInMonth + " " + otchet);
                subtitle = createSubtitle();
                eventsContainer = createMonthEventsContainer();
                scrollPanel.addView(subtitle);
                scrollPanel.addView(eventsContainer);
                currentDate = eventDate;
                calendarCurrentDate.setTime(currentDate);
                currentMonthNumber = calendarCurrentDate.get(Calendar.MONTH);
                currentMonth = months[currentMonthNumber];
                countEventsInMonth = 1;
            }
            RelativeLayout eventContainer = createEventContainer();
            eventsContainer.addView(eventContainer);
            TextView eventTitle = createEventTitle(event.getString("name"));
            int tempId = View.generateViewId();
            eventTitle.setId(tempId);
            eventContainer.addView(eventTitle);
            TextView eventPeriod = createEventPeriod(event.getString("period"), tempId);
            tempId = View.generateViewId();
            eventPeriod.setId(tempId);
            eventContainer.addView(eventPeriod);
            TextView eventDone = createEventDone(event.getJSONObject("status"),tempId);
            eventContainer.addView(eventDone);

        }
        if (countEventsInMonth == 1){
            sdan = "сдан";
            otchet = "отчет";
        } else if( countEventsInMonth > 1 && countEventsInMonth < 5) {
            sdan = "сданы";
            otchet = "отчета";
        } else if(countEventsInMonth > 4){
            sdan = "сданы";
            otchet = "отчетов";

        }

        subtitle.setText("В " + currentMonth + " " + sdan + " " + countEventsInMonth + " " + otchet);
    }

    private JSONArray getSortedEvents() throws JSONException{
        JSONArray events = json.getJSONArray("events");
        List<JSONObject> eventsList = new ArrayList<JSONObject>();

        for(int i = 0; i < events.length(); i++){
            eventsList.add(events.getJSONObject(i));
        }

        Collections.sort(eventsList, new Comparator<JSONObject>() {

            private Date prepareDate(JSONObject jsonObject){
                Date result = new Date();
                try {
                    result = dateFormat.parse(jsonObject.getString("date"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            public int compare(JSONObject jsonObject, JSONObject t1){
                Date jsonObjectDate1 = prepareDate(jsonObject);
                Date jsonObjectDate2 = prepareDate(t1);
                int result = jsonObjectDate1.compareTo(jsonObjectDate2);
                return result;
            }
        });
        Collections.reverse(eventsList);
        return new JSONArray(eventsList);
//        return events;
    }

    private TextView createSubtitle(){
        TextView subtitle = new TextView(activity);
        int d = (int) activity.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(24*d, 16*d, 0, 16*d);
        subtitle.setLayoutParams(params);
        subtitle.setTypeface(Typeface.DEFAULT_BOLD);
        subtitle.setTextColor(ContextCompat.getColor(activity, R.color.textSubtitleColor));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return subtitle;
    }

    private LinearLayout createMonthEventsContainer(){
        int d = (int) activity.getResources().getDisplayMetrics().density;
        LinearLayout monthEventsContainer = new LinearLayout(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8*d, 0, 8*d, 0);
        monthEventsContainer.setLayoutParams(params);
        monthEventsContainer.setOrientation(LinearLayout.VERTICAL);
        monthEventsContainer.setBackgroundResource(R.drawable.boxed_panel);
        monthEventsContainer.setId(View.generateViewId());
        return monthEventsContainer;
    }

    private RelativeLayout createEventContainer(){
        int d = (int) activity.getResources().getDisplayMetrics().density;
        RelativeLayout eventContainer = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16 * d, 0, 0);
        eventContainer.setPadding(0, 0, 0, 16*d);
        eventContainer.setLayoutParams(params);
        eventContainer.setBackgroundResource(R.drawable.border_bottom);
        return eventContainer;
    }

    private TextView createEventTitle(String textContent){
        int d = (int) activity.getResources().getDisplayMetrics().density;
        TextView eventTitle = new TextView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16*d, 16 * d, 16*d, 0);
        eventTitle.setLayoutParams(params);
        eventTitle.setTypeface(Typeface.DEFAULT_BOLD);
        eventTitle.setLineSpacing(0, 1.1f);
        eventTitle.setTextColor(ContextCompat.getColor(activity, R.color.textTitleColor));
        eventTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        eventTitle.setText(textContent);
        return eventTitle;
    }
    private TextView createEventPeriod(String textContent, int id){
        int d = (int) activity.getResources().getDisplayMetrics().density;
        TextView eventContent = new TextView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16*d, 0, 16*d, 0);
        params.addRule(RelativeLayout.BELOW, id);
        eventContent.setLayoutParams(params);
        eventContent.setTextColor(ContextCompat.getColor(activity, R.color.textSubtitleColor));
        eventContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        eventContent.setText(textContent);
        return eventContent;
    }
    private TextView createEventDone(JSONObject textContent, int id) throws JSONException{
        int d = (int) activity.getResources().getDisplayMetrics().density;
        TextView eventDone = new TextView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16*d, 0, 16*d, 16 * d);
        params.addRule(RelativeLayout.BELOW, id);
        eventDone.setLayoutParams(params);
        eventDone.setText(textContent.getString("name"));
        if (textContent.getString("statusId").equals("2")){
            eventDone.setTextColor(ContextCompat.getColor(activity, R.color.textDone));
        } else{
            eventDone.setTextColor(ContextCompat.getColor(activity, R.color.ErrorColor));
        }

        return eventDone;
    }
}
