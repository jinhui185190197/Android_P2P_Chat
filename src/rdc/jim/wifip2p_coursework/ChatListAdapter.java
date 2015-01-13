package rdc.jim.wifip2p_coursework;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 对话列表适配器
 * 
 * @author jim
 * 
 */

public class ChatListAdapter extends BaseAdapter {

	public static final String TAG = "ChatListAdapter";
	private final int INCOME = 1;
	private final int OUTCOME = 0;
	private List<ChatMessage> mMsgList;
	private Context mContext;
	private LayoutInflater layoutInflater;

	public ChatListAdapter(Context context) {
		this(context, new ArrayList<ChatMessage>());
	}

	public ChatListAdapter(Context context, List<ChatMessage> data) {
		this.mContext = context;
		this.mMsgList = data;
		this.layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage message = mMsgList.get(position);
		if (getItemViewType(position) == INCOME) {
			// 创建输入消息视图
			// 判断类型
			if (message.getChatName().equals("file")) {
				convertView = layoutInflater.inflate(
						R.layout.income_itemfile_view, parent, false);
				ImageView contentIm = (ImageView) convertView
						.findViewById(R.id.income_file_iv);
				Bitmap bitmap = BitmapFactory.decodeFile(message
						.getChatContent());
				contentIm.setImageBitmap(bitmap);
			} else {
				convertView = layoutInflater.inflate(R.layout.income_itemview,
						parent, false);
				TextView contentTv = (TextView) convertView
						.findViewById(R.id.income_content_tv);
				contentTv.setText(message.getChatContent());

			}

		} else {
			if (message.getChatName().equals("file")) {
				convertView = layoutInflater.inflate(
						R.layout.outcome_itemfile_view, parent, false);
				ImageView contentIm = (ImageView) convertView
						.findViewById(R.id.outcome_file_iv);
				Bitmap bitmap = BitmapFactory.decodeFile(message
						.getChatContent());
				contentIm.setImageBitmap(bitmap);
			} else {
				convertView = layoutInflater.inflate(R.layout.outcome_itemview,
						parent, false);
				TextView contentTv = (TextView) convertView
						.findViewById(R.id.out_content_tv);
				contentTv.setText(message.getChatContent());

			}

		}

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {

		if (mMsgList.get(position).getType() == ChatMessage.Type.INCOME) {
			// 若为机器人输入
			return INCOME;
		} else {
			return OUTCOME;
		}
	}

	/**
	 * 添加对话内容
	 * 
	 * @param msg
	 */
	public void addMessage(ChatMessage msg) {
		mMsgList.add(msg);
		notifyDataSetChanged();
	}

	/**
	 * 清空对话列表
	 */
	public void clearMessage() {
		mMsgList.clear();
	}

	/**
	 * 添加对话内容队列
	 */
	public void addMessageList(List<ChatMessage> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				mMsgList.add(list.get(i));
			}

			notifyDataSetChanged();
		}

	}
	
	public ChatMessage getChatMessage(int position){
		
		return mMsgList.get(position);
	}

}
