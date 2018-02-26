package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.clinicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import getset.ServiceRowdata;
import util.CommonClass;

/**
 * Created by subhashsanghani on 1/17/17.
 */

public class ServicesFragment  extends Fragment {
    ArrayList<ServiceRowdata> serviceArray;
    ListView listview;
    ServiceAdapter adapter;
    CommonClass common;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_service, container, false);
        common = new CommonClass(getActivity());
        serviceArray = new ArrayList<>();
        listview = (ListView)rootView.findViewById(R.id.listView);
        adapter = new ServiceAdapter();
        listview.setAdapter(adapter);

        Bundle bundle = getArguments();
        try {

            JSONArray loginArray = new JSONArray(bundle.getString("services"));
            for (int  i = 0 ; i < loginArray.length(); i++){
                JSONObject jmap = null;
                try {
                    jmap = loginArray.getJSONObject(i);
                    ServiceRowdata row = new ServiceRowdata(false,i,jmap.getString("id"),jmap.getString("service_price"),jmap.getString("service_discount"),jmap.getString("service_title"));
                    serviceArray.add(row);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }
    class ServiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(serviceArray!= null)
                return serviceArray.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.row_service_list, null);


            }
            final ServiceRowdata inforow = serviceArray.get(i);
            TextView txtPrice = (TextView)view.findViewById(R.id.textView1);
            txtPrice.setText(common.getCurrencyAmount(inforow.getDiscountAmount()));

            TextView txttitle = (TextView)view.findViewById(R.id.textView2);
            txttitle.setText(inforow.getServiceName());


            return view;
        }
    }
}
