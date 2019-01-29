package com.example.orgot.bluelight;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by kevin on 4/16/15.
 */
public class AlarmAdapter extends ArrayAdapter<MainActivity.AlarmObject>
{

    Resources r;

    public AlarmAdapter(Context context, ArrayList<MainActivity.AlarmObject> values)
    {
        super(context, R.layout.list_item, values);
        r = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View v = inflater.inflate(R.layout.list_item, parent, false);

        final MainActivity.AlarmObject alarmObject = getItem(position);

        TextView timeText = (TextView) v.findViewById(R.id.listItemTimeText);
        String s = alarmObject.getTimeString();
        int start = s.indexOf(":");
        SpannableString ss1=  new SpannableString(s);
        timeText.setTextSize(12);
        timeText.setTypeface(null, Typeface.BOLD);
        ss1.setSpan(new RelativeSizeSpan(2f), 0, start + 3, 0); // set size
        timeText.setTextColor(r.getColor(R.color.White));
        timeText.setText(ss1);


        TextView dayText = (TextView) v.findViewById(R.id.listItemDayText);
        dayText.setTextColor(r.getColor(R.color.White));
        dayText.setText(alarmObject.getDaysString());
        dayText.setTextSize(12);
        dayText.setText(alarmObject.getDaysString());

        Switch toggle = (Switch) v.findViewById(R.id.listItemToggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    alarmObject.activate();
                } else {
                    alarmObject.deactivate();
                }
            }
        });

        return v;
    }

}
