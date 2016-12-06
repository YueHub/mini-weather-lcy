package cn.edu.pku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.activities.R;
import cn.edu.pku.model.City;

/**
 * Created by Yue on 2016/10/11.
 */
public class CityAdapter extends ArrayAdapter<City> {

    private int resourceId;

    public CityAdapter(Context context, int textViewResourceId, List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        } else {
            view = convertView;
        }
        //ImageView cityImage = (ImageView) view.findViewById(R.id.city_image);
        TextView cityName = (TextView) view.findViewById(R.id.city_name);
        cityName.setText(city.getCity());
        return view;
    }
}
