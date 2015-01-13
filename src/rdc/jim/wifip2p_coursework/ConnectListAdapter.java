package rdc.jim.wifip2p_coursework;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectListAdapter extends BaseAdapter{
	
	private static final String TAG = "ConnectListAdapter";
	private List<WiFiP2pService> mServicesList;
	private Context mContext;
	
	
	public ConnectListAdapter(Context context){
		this(context, new ArrayList<WiFiP2pService>());
	}
	
	public ConnectListAdapter(Context context, List<WiFiP2pService> list) {
		this.mContext = context;
		this.mServicesList = list;
		
	}

	@Override
	public int getCount() {
		
		return mServicesList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mServicesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.con_itemview, parent,false);
		}
		WiFiP2pService service = mServicesList.get(position);
        if (service != null) {
            TextView nameText = (TextView) view
                    .findViewById(R.id.con_name_tx);

            if (nameText != null) {
                nameText.setText(service.instanceName);
                if(service.isConnect){
                	nameText.setTextColor(Color.RED);
                }
            }
//            TextView statusText = (TextView) v
//                    .findViewById(android.R.id.text2);
//            statusText.setText(getDeviceStatus(service.device.status));
        }
		return view;
	}
	
	/**
	 * 添加列表数据
	 * @param service
	 */
	public void addItemData(WiFiP2pService service){
		
		mServicesList.add(service);
		Log.e(TAG, "size:"+mServicesList.size());
		notifyDataSetChanged();
	}
	public WiFiP2pService getService(int position){
		return mServicesList.get(position);
	}
	/**
	 * 判断该设备是否已经存在
	 * @param ip
	 * @return
	 */
	public boolean isExistService(String ip){
		for(int i = 0; i<mServicesList.size(); i++){
			if(ip.equals(mServicesList.get(i).ip)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 从列表中清除设备
	 * @param ip
	 */
	public void removeService(String ip){
		for(int i = 0; i<mServicesList.size(); i++){
			if(ip.equals(mServicesList.get(i).ip)){
				Log.e(TAG, "remove service :" + ip);
				mServicesList.remove(i);
			}
		}
		notifyDataSetChanged();
	}

	
	

}
