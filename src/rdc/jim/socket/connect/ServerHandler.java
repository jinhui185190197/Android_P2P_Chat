
package rdc.jim.socket.connect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rdc.jim.wifip2p_coursework.ServerSocketContainer;

/**
 * 服务器线程，等待客户端接入
 * （当前设备为服务端）
 * @author 橘子哥
 *
 */
public class ServerHandler extends Thread {

	public static final String NEW_CONNECTION = "new_connect";
	private static final int DEFAULT_PORT = 8989;
    ServerSocket socket = null;
    private final int THREAD_COUNT = 10;
    private volatile boolean flag;
    private Context mContext;
    private static final String TAG = "ServerHandler";

    public ServerHandler(Context context) throws IOException {
        try {
        	flag = true;
        	mContext = context;
            socket = new ServerSocket(DEFAULT_PORT);
            Log.d("ServerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
    	Log.w(TAG, "Server Thread start..........");
        while (flag) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
            	Socket clientSocket = socket.accept();
            	String address = clientSocket.getInetAddress().getHostAddress();
            	Log.e(TAG, "address : " + address);
            	ChatManager manager = new ChatManager(clientSocket, mContext, address);
            	ServerSocketContainer.getInstance().addServerSocket(address, manager);
                //发送广播通知主线程
            	Intent intent = new Intent();
            	intent.setAction(NEW_CONNECTION);
            	intent.putExtra("address", address);
            	mContext.sendBroadcast(intent);
            	//启动消息对话线程
            	pool.execute(manager);
                Log.d(TAG, "Launching the I/O handler");

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
            
        }
        Log.e(TAG, "ServerThread stop!...........");
    }
    
    /**
     * 关闭线程
     */
    public void stopThread(){
    	flag = false;
    	if(socket != null){
    		try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

}
