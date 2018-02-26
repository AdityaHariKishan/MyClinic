package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clinicapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Config.ConstValue;
import util.CommonClass;
import util.RoundedImageView;

/**
 * Created by subhashsanghani on 1/17/17.
 */

public class AboutFragment  extends Fragment {

    CommonClass common;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_details, container, false);
        common = new CommonClass(getActivity());

        HashMap<String,String> map = (HashMap<String,String>)getArguments().getSerializable("clincinfo");

        TextView txtDoctName = (TextView)rootView.findViewById(R.id.doct_name);
        TextView txtDoctPhone = (TextView)rootView.findViewById(R.id.doct_phone);
        TextView txtDoctSpeciality = (TextView)rootView.findViewById(R.id.doct_speciality);
        TextView txtDoctFees = (TextView)rootView.findViewById(R.id.doct_fees);
        TextView textDescription = (TextView)rootView.findViewById(R.id.textDescription);

        RoundedImageView doctImage = (RoundedImageView)rootView.findViewById(R.id.doctorPhoto);

        Picasso.with(getActivity()).load(ConstValue.BASE_URL + "/uploads/business/" +map.get("doct_photo")).into(doctImage);

        txtDoctName.setText(map.get("doct_name"));
        txtDoctSpeciality.setText(map.get("doct_degree")+", "+map.get("doct_speciality"));
        txtDoctPhone.setText(map.get("doct_phone"));
        txtDoctFees.setText(common.getCurrencyAmount(map.get("bus_fee")));
        textDescription.setText(Html.fromHtml(map.get("bus_description")));
        return rootView;
    }
}
