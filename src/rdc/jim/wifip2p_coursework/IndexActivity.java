package rdc.jim.wifip2p_coursework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IndexActivity extends Activity {

	private EditText mNickNameEt;
	private Button mSubmitBtn;
	private ConnectivityManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_activity);
		manager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		mNickNameEt = (EditText) findViewById(R.id.index_input_et);
		mSubmitBtn = (Button) findViewById(R.id.index_submit_btn);

		mSubmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 检测是否已经连接上了wifi
				NetworkInfo info = manager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (info.isConnected()) {
					Intent intent = new Intent(IndexActivity.this,
							MainActivity.class);
					intent.putExtra("nickName", mNickNameEt.getText()
							.toString());
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "wifi还没连接,请连接wifi",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

}
