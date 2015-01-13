
package rdc.jim.socket.connect;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 链接服务器
 * （当前设备为客户端）
 * @author 橘子哥
 *
 */

public class ClientHandler extends Thread {

	private static final int DEFAULT_PORT = 8989;
    private static final String TAG = "ClientSocketHandler";
    //private Handler handler;
    private ChatManager chat;
    private String mAddress;
    private Context mContext;

    public ClientHandler(Context context, String address) {
        //this.handler = handler;
    	mContext = context;
        this.mAddress = address;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress,
                    DEFAULT_PORT), 10*1000);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, mContext, mAddress);
            new Thread(chat).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    public ChatManager getChat() {
        return chat;
    }

}
