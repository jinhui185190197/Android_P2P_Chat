package rdc.jim.wifip2p_coursework;

import java.io.File;
import java.io.UnsupportedEncodingException;

import rdc.jim.receiver.WifiBroadcastReceiver;
import rdc.jim.service.UdpBrocastService;
import rdc.jim.socket.connect.ChatManager;
import rdc.jim.socket.connect.ClientHandler;
import rdc.jim.socket.connect.ServerHandler;
import rdc.jim.udpbrocast.ServiceCheckHelper;
import rdc.jim.util.FileUtil;
import rdc.jim.util.MessageUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String TAG = "CHAT_ACTIVITY";
	public static final int FILE_SELECT_CODE = 0x1;

	private ListView mChatLv;
	private ListView mServicesLv;
	private TextView mNameTv;
	private TextView mTitleTv;
	private Button mSendBtn;
	private Button mGroupSendBtn;
	private Button mFileSelectBtn;
	private EditText mMsgEt;
	private ConnectListAdapter adapter;
	private ChatListAdapter chatAdapter;
	private ImageView mShowIm;

	// 当前聊天的对象
	private int mCurPosition;

	public Handler handler;

	// 广播
	private WifiBroadcastReceiver receiver;
	private final IntentFilter filter = new IntentFilter();
	private WifiP2pManager manager;
	private Channel channel;
	private String mNickName;

	Intent intent;

	// 输入法控制
	private InputMethodManager mInputManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNickName = getIntent().getStringExtra("nickName");

		handler = new MsgHandler();
		mCurPosition = -1;

		mInputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// 初始化相关设置
		// 设置广播
		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		filter.addAction("peers");
		filter.addAction("message");
		filter.addAction(ServerHandler.NEW_CONNECTION);
		filter.addAction(ServiceCheckHelper.DELAY_SERVICE);

		// 创建wifip2pManager
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		// 创建chanel
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WifiBroadcastReceiver(this, manager, channel, handler);

		// 初始化控件
		mShowIm = (ImageView) findViewById(R.id.main_show_im);
		mChatLv = (ListView) findViewById(R.id.chat_list_lv);
		mNameTv = (TextView) findViewById(R.id.name_tv);
		mServicesLv = (ListView) findViewById(R.id.con_lv);
		mTitleTv = (TextView) findViewById(R.id.name_top_tv);
		mMsgEt = (EditText) findViewById(R.id.input_et);
		mFileSelectBtn = (Button) findViewById(R.id.file_btn);
		mGroupSendBtn = (Button) findViewById(R.id.groupsumit_btn);

		mNameTv.setText(mNickName);

		chatAdapter = new ChatListAdapter(this);
		mChatLv.setAdapter(chatAdapter);

		adapter = new ConnectListAdapter(this);

		mServicesLv.setAdapter(adapter);
		mServicesLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long itemId) {
				doConnectToService(position);
				mTitleTv.setText(adapter.getService(position).instanceName);
				mCurPosition = position;
				// 切换对话内容列表
				chatAdapter.clearMessage();
				chatAdapter.addMessageList(MessageContainer.getInstance()
						.getChatMessages(adapter.getService(position).ip));

			}
		});

		mSendBtn = (Button) findViewById(R.id.sumit_btn);
		mSendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mCurPosition != -1) {
					String addr = adapter.getService(mCurPosition).ip;
					String msgStr = "";
					try {
						msgStr = new String(mMsgEt.getText().toString()
								.getBytes(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if ((msgStr == null) || (msgStr.equals(""))) {
						Toast.makeText(getApplicationContext(), "消息不能为空",
								Toast.LENGTH_SHORT).show();
					} else {
						if (adapter.getService(mCurPosition).serviceRole == WiFiP2pService.SERVICE_CLIENT) {
							// 如果是作为客户端
							// 从保存中取出相应的链接
							Log.e(TAG, "Send as a Client.......");
							Log.e(TAG, addr);
							ClientHandler clientSocketHandler = ClientSocketContainer
									.getInstance().getClientSocket(addr);
							if (clientSocketHandler != null) {

								clientSocketHandler.getChat().write(
										MessageUtil.encodeByte(
												MessageUtil.MESSAGE_TYPE_STR,
												msgStr.getBytes()));
								ChatMessage chatMsg = new ChatMessage();
								chatMsg.setChatContent(mMsgEt.getText()
										.toString());
								chatMsg.setType(ChatMessage.Type.OUTCOME);
								chatMsg.setChatName("str");
								chatAdapter.addMessage(chatMsg);

							} else {
								Log.e(TAG, "client socket is null ......");
							}

						} else {
							// 作为服务器
							Log.e(TAG, "Send as a Server.......");
							ChatManager chatManager = ServerSocketContainer
									.getInstance().getServerSocket(addr);
							if (chatManager != null) {
								chatManager.write(MessageUtil.encodeByte(
										MessageUtil.MESSAGE_TYPE_STR,
										msgStr.getBytes()));
								// 添加到列表
								ChatMessage chatMsg = new ChatMessage();
								chatMsg.setChatContent(msgStr);
								chatMsg.setType(ChatMessage.Type.OUTCOME);
								chatMsg.setChatName("str");
								chatAdapter.addMessage(chatMsg);
							} else {
								Log.e(TAG, "Session is not exist........");
							}

						}
					}

				}

				mMsgEt.setText("");
				// 收起输入法
				mInputManager.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		mFileSelectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showFileChooser();
			}
		});

		mGroupSendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 遍历所有已经连接的设备
				String msg = mMsgEt.getText().toString();
				String msgStr = "";
				if ((msg == null) || (msg.equals(""))) {
					Toast.makeText(getApplicationContext(), "消息不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						msgStr = new String(mMsgEt.getText().toString()
								.getBytes(), "UTF-8");
					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}
					for (int i = 0; i < adapter.getCount(); i++) {
						WiFiP2pService service = adapter.getService(i);
						// 已经连接
						if (service.isConnect) {
							// 若为客户端
							if (service.serviceRole == WiFiP2pService.SERVICE_CLIENT) {
								ClientHandler clientSocketHandler = ClientSocketContainer
										.getInstance().getClientSocket(
												service.ip);
								clientSocketHandler.getChat().write(
										MessageUtil.encodeByte(
												MessageUtil.MESSAGE_TYPE_STR,
												msgStr.getBytes()));
							} else {
								// 若为服务器
								ChatManager chatManager = ServerSocketContainer
										.getInstance().getServerSocket(
												service.ip);
								chatManager.write(MessageUtil.encodeByte(
										MessageUtil.MESSAGE_TYPE_STR,
										msgStr.getBytes()));
							}
						}
					}

				}

			}
		});

		mChatLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ChatMessage message = chatAdapter.getChatMessage(position);
				if (message.getChatName().equals("file")) {
					Bitmap bitmap = BitmapFactory.decodeFile(message
							.getChatContent());
					mShowIm.setImageBitmap(bitmap);
					mShowIm.setVisibility(View.VISIBLE);
					mShowIm.setClickable(true);
				}
			}
		});
		
		mShowIm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageView im = (ImageView)v;
				im.setVisibility(View.GONE);
				im.setClickable(false);
			}
		});

		intent = new Intent(this, UdpBrocastService.class);
		intent.putExtra("nickName", mNickName);
		startService(intent);
	}

	/**
	 * 链接到指定设备
	 * 
	 * @param position
	 */
	public void doConnectToService(int position) {
		if (!adapter.getService(position).isConnect) {

			Log.e(TAG, "doConnectToService.............");
			Log.e(TAG, "service address : " + adapter.getService(position).ip);
			ClientHandler clientHandler = new ClientHandler(this,
					adapter.getService(position).ip);
			clientHandler.start();
			// 保存
			ClientSocketContainer.getInstance().addClientSocket(
					adapter.getService(position).ip, clientHandler);
			Log.e(TAG, "con : " + adapter.getService(position).ip);
			MessageContainer.getInstance().addChatList(
					adapter.getService(position).ip);
			adapter.getService(position).serviceRole = WiFiP2pService.SERVICE_CLIENT;
			adapter.getService(position).isConnect = true;
			adapter.notifyDataSetChanged();

		}
	}

	/**
	 * 显示文件选择器 发现只能选择图片
	 */
	public void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// 获取已经选择的文件路径
				Uri uri = data.getData();
				String filePath = FileUtil.getRealFilePath(this, uri);
				if (filePath != null) {
					// 直接发送
					File file = new File(filePath);
					if (mCurPosition != -1) {
						String addr = adapter.getService(mCurPosition).ip;
						if (adapter.getService(mCurPosition).serviceRole == WiFiP2pService.SERVICE_CLIENT) {
							Log.e(TAG, "Send as a Client.......");
							Log.e(TAG, addr);
							ClientHandler clientSocketHandler = ClientSocketContainer
									.getInstance().getClientSocket(addr);
							if (clientSocketHandler != null) {

								clientSocketHandler.getChat().write(
										MessageUtil.encodeByte(
												MessageUtil.MESSAGE_TYPE_FILE,
												FileUtil.getFileByte(file)));
								ChatMessage chatMsg = new ChatMessage();
								chatMsg.setChatContent(filePath);
								chatMsg.setType(ChatMessage.Type.OUTCOME);
								chatMsg.setChatName("file");
								chatAdapter.addMessage(chatMsg);

							} else {
								Log.e(TAG, "client socket is null ......");
							}
						} else {

							// 作为服务器
							Log.e(TAG, "Send as a Server.......");
							ChatManager chatManager = ServerSocketContainer
									.getInstance().getServerSocket(addr);
							if (chatManager != null) {
								chatManager.write(MessageUtil.encodeByte(
										MessageUtil.MESSAGE_TYPE_FILE,
										FileUtil.getFileByte(file)));
								// 添加到列表
								ChatMessage chatMsg = new ChatMessage();
								chatMsg.setChatContent(filePath);
								chatMsg.setType(ChatMessage.Type.OUTCOME);
								chatMsg.setChatName("file");
								chatAdapter.addMessage(chatMsg);
							} else {
								Log.e(TAG, "Session is not exist........");
							}
						}

					}

				}
				Log.w(TAG, filePath);

			}

			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		if (receiver != null) {
			registerReceiver(receiver, filter);
		}
		// startService(intent);
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		// stopService(intent);
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (manager != null && channel != null) {
			manager.removeGroup(channel, new ActionListener() {

				@Override
				public void onFailure(int reasonCode) {
					Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
				}

				@Override
				public void onSuccess() {
				}

			});
		}
		Log.e(TAG, "onStop.........");

		super.onStop();
	}

	@Override
	protected void onDestroy() {

		stopService(intent);
		super.onDestroy();
	}

	public class MsgHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 0 : 找到peer
			// 1 : 对话消息
			// 2 : 新连接
			// 3 ： 链接断开
			// 4 : 设备离开了局域网
			if (msg.arg1 == 0) {
				String ip = msg.getData().getString("address");
				String name = msg.getData().getString("name");
				if (adapter.isExistService(ip)) {
					Log.d(TAG, "new service..........");
					WiFiP2pService service = new WiFiP2pService();
					service.ip = ip;
					service.instanceName = name;
					service.isConnect = false;
					adapter.addItemData(service);
				}
			} else if (msg.arg1 == 1) {
				Log.e(TAG, "Message receiver.........");
				String message = msg.getData().getString("msg");
				String addr = msg.getData().getString("address");
				String type = msg.getData().getString("type");
				Log.e(TAG, "receiver : " + addr);
				// 加入到列表
				ChatMessage chatMsg = new ChatMessage();
				chatMsg.setChatContent(message);
				chatMsg.setType(ChatMessage.Type.INCOME);
				chatMsg.setChatName(type);
				MessageContainer.getInstance().addChatMessage(addr, chatMsg);
				// 若为当前对话人
				if (mCurPosition != -1) {
					if (adapter.getService(mCurPosition).ip.equals(addr)) {
						chatAdapter.addMessage(chatMsg);
						mChatLv.setSelection(adapter.getCount()-1);
					} else {
						return;
					}
				}

			} else if (msg.arg1 == 2) {
				Log.e(TAG, "NEW_CONNECTION");
				String addr = msg.getData().getString("address");
				Log.e(TAG, "new connect : " + addr);
				for (int i = 0; i < adapter.getCount(); i++) {
					if (addr.equals(adapter.getService(i).ip)) {
						adapter.getService(i).isConnect = true;
						MessageContainer.getInstance().addChatList(addr);
						break;
					}
				}
				// 通知列表更新
				adapter.notifyDataSetChanged();
			} else if (msg.arg1 == 3) {
				Log.e(TAG, "UN_CONNECTION");
				String addr = msg.getData().getString("address");
				// 若当前对话框为不可达设备
				if (addr.equals(adapter.getService(mCurPosition).ip)) {
					chatAdapter.clearMessage();
					mTitleTv.setText("暂无对象");
				}
				// 从列表中清除
				adapter.removeService(addr);
				Toast.makeText(getApplicationContext(),
						"设备" + addr + "不可达，已从列表中清除", Toast.LENGTH_SHORT).show();
			} else if (msg.arg1 == 4) {
				Log.e(TAG, "DELAY_SERVICE");
				String addr = msg.getData().getString("address");
				// int type = -1;
				// 检查是否为当前聊天对象
				if (mCurPosition != -1) {
					if (adapter.getService(mCurPosition).ip.equals(addr)) {
						// 清空对话框，将标题设置为“暂无对象”
						chatAdapter.clearMessage();
						mTitleTv.setText("暂无对象");
						Toast.makeText(getApplicationContext(),
								"当前聊天对象离开了局域网.....", Toast.LENGTH_SHORT).show();
					}

				}
				// 从设备列表中清除
				adapter.removeService(addr);
				// ？应该还要清除链接线程
				MessageContainer.getInstance().removeChatList(addr);
				ClientSocketContainer.getInstance().deleteClientSocket(addr);
				ServerSocketContainer.getInstance().deleteServerSocket(addr);

			}
		}
	}

}
