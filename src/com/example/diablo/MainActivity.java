package com.example.diablo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.diablo.MainActivity;
import com.utils.HttpUtils;
import com.example.diablo.R;

public class MainActivity extends Activity implements OnClickListener {

	private EditText tagname;
	private EditText tagcode;
	private Button bt_serch;
	private TextView textView;
	private final int SUCCESS = 1;
	private final int FAILURE = 0;
	private final int ERRORCODE = 2;
	protected String GameResult;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				/**
				 * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
				 */
				JSONAnalysis(msg.obj.toString());
				Toast.makeText(MainActivity.this, "获取数据成功", Toast.LENGTH_SHORT)
				.show();
				break;

			case FAILURE:
				Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
						.show();
				break;

			case ERRORCODE:
				Toast.makeText(MainActivity.this, "获取的CODE码不为200！",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	/**
	 * JSON解析方法
	 */
	protected void JSONAnalysis(String string) {
		try {
			JSONObject ObjectInfo = new JSONObject(string);
			String battleTag = ObjectInfo.getString("battleTag");
			String paragonLevel = ObjectInfo.getString("paragonLevel");
			GameResult = "Tag：" + battleTag + "\n巅峰等级：" + paragonLevel + "\n英雄列表：\nID:";
			JSONArray heroList =ObjectInfo.getJSONArray("heroes");
			for(int i=0; i<heroList.length(); i++)
			{
				String name=heroList.getJSONObject(i).getString("name");
				String level=heroList.getJSONObject(i).getString("level");
				String hero=heroList.getJSONObject(i).getString("class");
				GameResult = GameResult+name+"\n等级："+level+"\n职业："+hero+"\nID:";		
			}
			textView.setText(GameResult);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	private void init() {
		tagname = (EditText) findViewById(R.id.tagname);
		tagcode = (EditText) findViewById(R.id.tagcode);
		bt_serch = (Button) findViewById(R.id.bt_serch);
		textView = (TextView) findViewById(R.id.textView);
		bt_serch.setOnClickListener(this);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_serch:
			/**
			 * 点击按钮事件，在主线程中开启一个子线程进行网络请求
			 * （因为在4.0只有不支持主线程进行网络请求，所以一般情况下，建议另开启子线程进行网络请求等耗时操作）。
			 */
			new Thread() {
				public void run() {
					int code;
					try {
						String path = "http://tw.battle.net/api/d3/profile/"+tagname.getText().toString()+"-"
								+tagcode.getText().toString()+"/";
						//String path="http://m.weather.com.cn/data/101010100.html";
						URL url = new URL(path);

						/**
						 * 这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。
						 */
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setRequestMethod("GET");//使用GET方法获取
						conn.setConnectTimeout(5000);
						code = conn.getResponseCode();
						if (code == 200) {
							/**
							 * 如果获取的code为200，则证明数据获取是正确的。
							 */
							InputStream is = conn.getInputStream();
							String result = HttpUtils.readMyInputStream(is);
							
							/**
							 * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
							 */
							Message msg = new Message();
							msg.obj = result;
							msg.what = SUCCESS;
							handler.sendMessage(msg);

						} else {
						
							Message msg = new Message();
							msg.what = ERRORCODE;
							handler.sendMessage(msg);
						}
					} catch (Exception e) {

						e.printStackTrace();
						/**
						 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
						 */
						Message msg = new Message();
						msg.what = FAILURE;
						handler.sendMessage(msg);
					}
				};
			}.start();
			break;

		default:
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
