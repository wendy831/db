package com.example.leejaeyun.bikenavi2.findroad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.leejaeyun.bikenavi2.POI_Data;
import com.example.leejaeyun.bikenavi2.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Lee Jae Yun on 2017-04-14.
 */

public class POI_DataCustomAdapter extends BaseAdapter {
    ArrayList<POI_Data> mListdata = new ArrayList<POI_Data>();
    Context mContext;

    public POI_DataCustomAdapter(Context mContext){ this.mContext = mContext; }
    // 외부에서 아이템 추가 요청 시 사용

    public void setdata(ArrayList<POI_Data> _msg) {
        mListdata=_msg;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return mListdata.size();
    }

    // 현재 아이템의 오브젝트를 리턴, Object를 상황에 맞게 변경하거나 리턴받은 오브젝트를 캐스팅해서 사용
    @Override
    public Object getItem(int position) {
        return mListdata.get(position);
    }

    // 아이템 position의 ID 값 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getPoint(int position){
        return mListdata.get(position).getPoint();
    }

    // 출력 될 아이템 관리
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        TextView POI_name = null;
        TextView POI_address = null;
        TextView POI_distance = null;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if ( convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.findroad_listview_item, parent, false);
            // 리스트 아이템을 터치 했을 때 이벤트 발생
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pathIntent = new Intent(mContext, DrawPath.class);
                    pathIntent.putExtra("arrivePoint",mListdata.get(position).point);
                    mContext.startActivity(pathIntent);
                }
            });
        }

        POI_name = (TextView) convertView.findViewById(R.id.POI_name);
        POI_address = (TextView) convertView.findViewById(R.id.POI_address);
        POI_distance = (TextView) convertView.findViewById(R.id.POI_distance);
        POI_name.setText(mListdata.get(position).getPoiname());
        POI_address.setText(mListdata.get(position).getAddress());
        POI_distance.setText(mListdata.get(position).getDistanceStr());

        return convertView;
    }

}

