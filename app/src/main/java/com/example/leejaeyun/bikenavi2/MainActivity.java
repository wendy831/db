package com.example.leejaeyun.bikenavi2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.leejaeyun.bikenavi2.findroad.FindRoad;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.util.Log;
import com.example.leejaeyun.bikenavi2.BasicValue;
import com.example.leejaeyun.bikenavi2.Retrofit.AccidentThread;

import android.os.Handler;
import android.os.Message;

/*import com.example.kwan.thinkba.util.ListViewDialog;*/  //나중에쓰이나??

import com.example.leejaeyun.bikenavi2.TmapPointArr;
import com.example.leejaeyun.bikenavi2.Retrofit.AccidentThread;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback{

    final static String TAG = "MainActivity";
    private Context mContext;
    private TMapGpsManager gps = null;
    private GpsInfo gpsInfo;
    private TMapPoint startPoint;
    private static TMapPoint arrivePoint;
    private static TMapPoint passPoint; // 경유지

    ArrayList<TMapPoint> arr_tMapPoint;
    ArrayList<TMapCircle> arr_tMapCircle;
    private TmapPointArr tmapPointArr;
    // TmapPointArr tmapPointArr;

    private FrameLayout mapLayout;
    private TMapView mapView = null;

    private double latitude = 0; //위도
    private double longitude = 0; // 경도
    private double beforeLat = 0;
    private double beforeLon = 0;
    private String arrive; // 도착지 좌표

    private boolean m_bTrackingMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        tMapGps();
        tMapInit();

        Handler handler = new AccidentReceiveHandler();
        Thread accidentThread = new AccidentThread(handler, MainActivity.this);
        accidentThread.start();

        /**
         * nowClickListener
         * 화면중심을 단말의 현재위치로 이동시켜준다.
         */
        FloatingActionButton fab_nowLocation = (FloatingActionButton) findViewById(R.id.fab_nowLocation);
        fab_nowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    gpsInfo = new GpsInfo(MainActivity.this);
                    if (gpsInfo.isGetLocation()) {
                        latitude = gpsInfo.getLatitude();
                        longitude = gpsInfo.getLongitude();
                        mapView.setLocationPoint(longitude, latitude);
                        mapView.setCenterPoint(longitude, latitude);
                        //Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.map_pin_red);
                        //mapView.setIcon(bitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "GPS 연동 실패", Toast.LENGTH_SHORT).show();
                    }
                }catch (NullPointerException e){Log.e(TAG,"now_btnClick 오류");}
            }
        });

        FloatingActionButton fab_findroad = (FloatingActionButton) findViewById(R.id.fab_findroad);
        fab_findroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FindRoad.class);
                intent.putExtra("startPoint",startPoint.toString());
                startActivity(intent);

            }
        });

        FloatingActionButton fab_deletePath = (FloatingActionButton) findViewById(R.id.fab_deletePath);
        fab_deletePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.removeTMapPath();
                mapView.removeAllTMapCircle();
                Toast.makeText(MainActivity.this, "경로 취소", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init(){
        mContext = getApplicationContext();
               mapLayout = (FrameLayout)findViewById(R.id.mapLayout);
               arr_tMapPoint = new ArrayList<TMapPoint>();
               arr_tMapCircle = new ArrayList<TMapCircle>();
    }


    private void tMapInit() {
        startPoint = new TMapPoint(latitude,longitude);
        gpsInfo = new GpsInfo(MainActivity.this);
        if (gpsInfo.isGetLocation()) { // 현재 위치 받아오기
            latitude = gpsInfo.getLatitude();
            longitude = gpsInfo.getLongitude();
            startPoint = new TMapPoint(latitude, longitude);
        }
        mapView = new TMapView(mContext); // 지도 위도, 경도, 줌레벨
        mapView.setSKPMapApiKey(getString(R.string.skplanet_key));
        mapView.setTrackingMode(m_bTrackingMode); // 트래킹 모드 사용
        mapView.setZoomLevel(16);
        mapView.setBicycleInfo(true);//자전거 도로 표시
        mapView.setBicycleFacilityInfo(true);//자전거 시설물 표시
        mapView.setIconVisibility(true); // 현재 위치 표시하는지 여부
        mapView.setLocationPoint(longitude, latitude); // 지도 현재 좌표 설정
        mapView.setCenterPoint(longitude, latitude); // 지도 현재 위치로
        mapView.setTMapLogoPosition(TMapView.TMapLogoPositon.POSITION_BOTTOMLEFT);
        mapLayout.addView(mapView);

    }

    private void tMapGps() {
        gps = new TMapGpsManager(MainActivity.this);
        gps.setMinTime(500);
        gps.setMinDistance(3);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();
    }

    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private void getArriveInfo() {
        try {
            Intent intent = getIntent();
            try {
                arrive = intent.getStringExtra("arrivePoint");
                String temp[] = arrive.split(" ");
                double latitude = Double.parseDouble(temp[1]);
                double longitude = Double.parseDouble(temp[3]);
                arrivePoint = new TMapPoint(latitude, longitude); // 도착지 포인트
            } catch (Exception e) {
                Log.e(TAG, "get arrivePoint error");
            }

            //double pass_latitude = intent.getDoubleExtra("near_latitude", 0);
            //double pass_Longitude = intent.getDoubleExtra("near_longitude", 0);
            //passPoint = new TMapPoint(pass_latitude, pass_Longitude); //경유지 포인트

        } catch (Exception e) {
            Log.d(TAG, "getArriveInfo error");
        }
    }




    private class AccidentReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tmapPointArr = (TmapPointArr) msg.getData().getSerializable("THREAD"); // 스레드에서 tMapPointArr를 받음
            try {
                setCircle(tmapPointArr.gettMapPointArr()); //setcircle 함수
                for (int i = 0; i < arr_tMapCircle.size(); i++) {
                    mapView.addTMapCircle("circle" + i, arr_tMapCircle.get(i)); //지도에 빨간원들을 표시함
                }
            }catch (NullPointerException e){Log.e(TAG,"TmapCircle error :"+e.getMessage());}
        }
    }

    private List<TMapCircle> setCircle(ArrayList<TMapPoint> tMapPointArr) {
        for(int i = 0; i < tMapPointArr.size(); i++){
            TMapPoint tempPoint = tMapPointArr.get(i);
            TMapCircle tempcircle = new TMapCircle();
            tempcircle.setCenterPoint(tempPoint);
            tempcircle.setRadius(70);
            tempcircle.setAreaColor(Color.rgb(255, 0, 0));
            tempcircle.setAreaAlpha(60);
            arr_tMapCircle.add(tempcircle);
        }
        return arr_tMapCircle;
    }


    @Override
    protected void onStart() {
        super.onStart();
        getArriveInfo();
        if (arrivePoint != null) {
            setPath();
        }
    }


    /**
     * setPath
     * 도착지 정보가 있다면 맵에 경로를 그려준다
     */
    private void setPath() {
        try {
            TMapData tmapdata = new TMapData();

            if (arrivePoint != null) { //출발지와 도착지 결과를 받아 경로를 그려줌
                Log.d(TAG, "setPath 2번 진입");
                tmapdata.findPathDataWithType(TMapData.TMapPathType.BICYCLE_PATH, startPoint, arrivePoint, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        mapView.addTMapPath(tMapPolyLine);
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "setPath error");
        }
    }
}
