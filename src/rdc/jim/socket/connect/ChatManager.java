package rdc.jim.socket.connect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;
import rdc.jim.util.FileUtil;
import rdc.jim.util.MessageUtil;

/**
 * 消息发送和接收线程
 * @author 橘子哥
 *
 */
public class ChatManager implements Runnable {

	private Socket socket = null;
	private Context mContext;
	private String mIp;

	public ChatManager(Socket socket, Context context, String ip) {
		this.socket = socket;
		mContext = context;
		mIp = ip;
	}

	private InputStream iStream;
	private OutputStream oStream;
	private static final String TAG = "ChatHandler";

	@Override
	public void run() {
		try {

			iStream = socket.getInputStream();
			oStream = socket.getOutputStream();
			byte[] buffer = new byte[10240];
			int bytes;

			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						//流结束,断开连接
						
						break;
					}
					// 判断消息类型
					// 普通字符串
					String msg;
					Intent intent = new Intent();
					intent.setAction("message");
					if (buffer[0] == 0) {
						int msgLength = MessageUtil.byteToInt(MessageUtil
								.slieBytes(buffer, 1, 4));
						msg = new String(buffer, 5, msgLength,
								Charset.forName("UTF-8"));
						intent.putExtra("type", "str");

					} else {
						// 文件类型
						// (暂时只定为jpg)
						// 获得长度，拼接

						int fileLength = MessageUtil.byteToInt(MessageUtil
								.slieBytes(buffer, 1, 4));
						Log.e(TAG, "file Length : " + fileLength);
						int fileBytes = 0;
						byte[] resultByte = new byte[fileLength];
						MessageUtil.ConBytes(resultByte, 0, 5, bytes - 5,
								buffer);
						int totalLength = bytes - 5;

						Log.e(TAG, "total : " + totalLength);
						
						// 继续读取后续文件字节
						while (totalLength < fileLength) {
							fileBytes = iStream.read(buffer);

							MessageUtil.ConBytes(resultByte, totalLength, 0,
									fileBytes, buffer);
							totalLength += fileBytes;
							Log.e(TAG, "total : " + totalLength);
							
						}
						
						
						Date date = new Date();

//						msg = "接收到文件:"
//								+ FileUtil.saveFileByByte(resultByte, 0,
//										resultByte.length,
//										FileUtil.getSDDir() + "/Test",
//										date.getTime() + ".jpg").getName();
						msg = FileUtil.saveFileByByte(resultByte, 0,
								resultByte.length,
								FileUtil.getSDDir() + "/Test",
								date.getTime() + ".jpg").getPath();
						intent.putExtra("type", "file");

					}

					Log.d(TAG, "Rec:" + msg);
//					Intent intent = new Intent();
//					intent.setAction("message");
					intent.putExtra("msg", msg);
					intent.putExtra("address", mIp);
					mContext.sendBroadcast(intent);
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buffer) {
		try {
			Log.e(TAG, "output : " + new String(buffer));
			oStream.write(buffer);
		} catch (IOException e) {
			Log.e(TAG, "Exception during write", e);
		}
	}
	public OutputStream getOutputStream(){
		return oStream;
	}

}
