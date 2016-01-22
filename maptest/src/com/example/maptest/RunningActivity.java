package com.example.maptest;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;



public class RunningActivity extends Activity {
	private static int AVERAGE_TIME = 5;
	private static float WEIGHT = 60;
	
	private BMapManager manager;

	private MapView mapView;

	private LocationManager locationManager;
	private String provider;
	
	private Chronometer timer;
	private TextView speedTextView;
	private TextView distanceTextView;
	private TextView energyTextView;
	
	private View infoView;
	private WindowManager.LayoutParams infoParams;
	
	List<GeoPoint> points = new ArrayList<GeoPoint>();
	
	private double distance=0;
	private double speed = 0;
	private double energy = 0;
	
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = new BMapManager(this);
		// API Key需要替换成你自己的
		manager.init("0eSXZBIHNT9IUI7h4sen91XM", null);
		setContentView(R.layout.basicmap_activity);
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);//默认也为true
		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 获取所有可用的位置提供器
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			// 当没有可用的位置提供器时，弹出Toast提示用户
			Toast.makeText(this, "No location provider to use",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			navigateTo(location);
		}
		
		
		initInfoView();
		initInfoParams();
		
	}

	private void navigateTo(Location location) {
		MapController controller = mapView.getController();
		// 设置缩放级别
		controller.setZoom(16);
		GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		// 设置地图中心点
		controller.setCenter(point);
		//定位图层初始化
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);
		LocationData locationData = new LocationData();
		// 指定我的位置
		locationData.latitude = location.getLatitude();
		locationData.longitude = location.getLongitude();
		myLocationOverlay.setData(locationData);
		//添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		// 刷新使新增覆盖物生效
		mapView.refresh();
	
	}	
	
	private void initInfoView() {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		infoView = layoutInflater.inflate(R.layout.info_activity, null);

		timer = (Chronometer) infoView.findViewById(R.id.timing);
		timer.setBase(SystemClock.elapsedRealtime());
		speedTextView = (TextView) infoView.findViewById(R.id.speed);
		distanceTextView = (TextView) infoView.findViewById(R.id.distance);
		energyTextView = (TextView) infoView.findViewById(R.id.energy);
	}

	private void initInfoParams() {
		infoParams = new WindowManager.LayoutParams();
		infoParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY + 1;
		infoParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		infoParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		infoParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		infoParams.format = PixelFormat.TRANSPARENT;
	}
	
	private double getDistance() {
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,1,
												new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				//location对象中包含了经纬度海拔等位置信息
				GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6));
				points.add(point);
				
				//Geometry geo=new Geometry();
				//geo.setPolyLine(points);
				
			}
				
		});
		if (points.size() < 2)
			return 0;

		GeoPoint firstpoint;
		GeoPoint secondpoint;
		double distance = 0;

		firstpoint = points.get(0);

		for (int i = 1; i < points.size(); i++) {
			secondpoint = points.get(i);
			distance += DistanceUtil.getDistance(firstpoint, secondpoint);
			firstpoint = secondpoint;
		}

		return distance;
	}
	
	private long calcTime = 0;
	
	//Chronometr是一个简单的定时器
	public void onChronometerTick(Chronometer chronometer) {
		calcTime++;

		if (calcTime % AVERAGE_TIME == 0) {
			distance = getDistance();
			speed = distance / (calcTime * AVERAGE_TIME);
			energy = WEIGHT * distance * 1.036f;
		}

		speedTextView.setText(getResources().getText(R.string.speed) + " : "
				+ speed + "km/s");
		distanceTextView.setText(getResources().getText(R.string.distance)
				+ " : " + distance + "km");
		energyTextView.setText(getResources().getText(R.string.energy) + " : "
				+ energy + "KJ");
	}
	
	private long exitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.once_more),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				DBHelper dbHelper = new DBHelper(getApplicationContext());
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(DBHelper.DATE, new SimpleDateFormat(getResources()
						.getString(R.string.date_format_1)).format(new Date()));
				map.put(DBHelper.TIME,
						timer.getText()
								.toString()
								.substring(getResources()
										   .getString(R.string.time_format).indexOf("h")));
				map.put(DBHelper.DISTANCE, distance);
				map.put(DBHelper.ENERGY, energy);
				dbHelper.insert(map);
				dbHelper.close();
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

	@Override
	protected void onDestroy() {
		mapView.destroy();
		if (manager != null) {
			manager.destroy();
			manager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		if (manager != null) {
			manager.stop();
		}
		super.onPause();
		//悬浮窗退出
		WindowManager manager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		manager.removeView(infoView);
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		if (manager != null) {
			manager.start();
		}
		super.onResume();
		WindowManager manager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		manager.addView(infoView, infoParams);

		timer.start();
	}

}
