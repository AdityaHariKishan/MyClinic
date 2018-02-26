package fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clinicapp.R;
import com.clinicapp.ServicesActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import util.CommonClass;

/**
 * Created by subhashsanghani on 1/16/17.
 */

public class TimeSlotFragment  extends Fragment {
    ArrayList<HashMap<String,String>> arrayList;

    CommonClass common;
    Bundle bundleE;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeslot, container, false);
         bundleE =getArguments();
        common = new CommonClass(getActivity());
        ListView listview = (ListView)rootView.findViewById(R.id.listview);
        TimeAdapter adapter = new TimeAdapter();
        try {
            JSONArray jsonArray = new JSONArray(bundleE.getString("slot"));
            arrayList = common.getArrayListFromJsonArray(jsonArray);
            listview.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,String> chooseMap = arrayList.get(i);
                if (chooseMap.get("is_booked").equalsIgnoreCase("false")) {
                    Intent intent = new Intent(getActivity(), ServicesActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("slot", chooseMap);
                    b.putString("date", bundleE.getString("date"));
                    intent.putExtras(b);
                    startActivity(intent);
                }else{
                    common.setToastMessage(getString(R.string.already_booked));
                }
            }
        });
        return rootView;
    }

    class TimeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (arrayList!=null)
                return arrayList.size();
            else
                return 0;
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

                LayoutInflater mInflater = (LayoutInflater)
                        getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.row_time_slot, null);
                HashMap<String,String> map = getItem(i);
                ImageView imgClock = (ImageView)view.findViewById(R.id.clockimage);
                if (map.get("is_booked").equalsIgnoreCase("true")){
                    view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    imgClock.setImageResource(R.drawable.time_clock);
                }else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    imgClock.setImageResource(R.drawable.time_clock_ok);
                }
                TextView timeslot = (TextView) view.findViewById(R.id.timeslot);
                timeslot.setText(parseTime(map.get("slot")));

            return view;
        }
    }
    public String parseTime(String time) {
        String inputPattern = "H:mm:ss";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
