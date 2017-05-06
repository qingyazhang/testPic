package com.example.testpic;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.testpic.utils.SpUtil;
import com.example.testpic.utils.StreamUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ZhuceActivity extends Activity {
	 private static final String TAG = "uploadFile";  
	    private static final int TIME_OUT = 10 * 10000000; // 超时时间  
	    private static final String CHARSET = "utf-8"; // 设置编码  
	    public static final String SUCCESS = "1";  
	    public static final String FAILURE = "0";
	private ImageView iv_pic;
	private EditText et_name;
	private EditText et_age;
	private EditText et_jianjie;
	private TextView tv_ok;
	private TextView tv_cancle;
	private String picUrl = null;
	private boolean tag2;
	private static String ip;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhuce);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		iv_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePic();
			}
		});
		tv_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_ok.setOnClickListener(new OnClickListener() {
			
			

			@Override
			public void onClick(View v) {
				ip = SpUtil.getString(getApplicationContext(), "IP", "");
				if(ip==""){
					Toast.makeText(getApplicationContext(), "请先设置IP", 0).show();
					return;
				}
				initUpLoad();
			}
		});
	}
	private void initUpLoad() {
		// TODO Auto-generated method stub
		
	
	
	//	SpUtil.putString(getApplicationContext(),ConstantValue.VISIT_STATE , "1");
		//tag = SpUtil.getString(getApplicationContext(), ConstantValue.VISIT_STATE, "0");
	
		Toast.makeText(getApplicationContext(), "init", 0).show();
		new Thread(){
		

			public void run() {
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"my.jpg");
				picUrl = uploadFile(file);
			};
		}.start();
		tag2 = true;
	new Thread(){
		public void run() {
			while(tag2){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(picUrl!=null&&(!picUrl.isEmpty())&&(!picUrl.equals(""))){
					tag2 = false;
					String path = "http://"+ip+":8080/day12-upload/zhuce";
					String name = et_name.getText().toString().trim();
					String age = et_age.getText().toString().trim();
					String jianjie = et_jianjie.getText().toString().trim();
				 try {
					// 创建一个 httpClient 对象
				 HttpClient client = new DefaultHttpClient();
					// 创建一个 post 请求方式
			HttpPost request = new HttpPost(path);
					//创建集合用于添加请求的数据
				 List<BasicNameValuePair> parameters =
			new ArrayList<BasicNameValuePair>();
			 //将请求数据添加到集合中
				parameters.add(new BasicNameValuePair("name", name));
				 parameters.add(new BasicNameValuePair("age", age));
				 parameters.add(new BasicNameValuePair("jianjie", jianjie));
				 parameters.add(new BasicNameValuePair("id", picUrl));
				 System.out.println("picUrl is!!!!!!!"+picUrl);
				 /*
					33. * 创建请求实体对象
					34. * UrlEncodedFormEntity 是 HttpEntity 的子类，
					35. 该类专门用于封装字符串类型的参数
					36. * 指定数据的编码格式
					37. */
				 HttpEntity requestEntity =
				new UrlEncodedFormEntity(parameters, "utf-8");
				//设置请求体
				request.setEntity(requestEntity);
			// 执行操作
			 HttpResponse response = client.execute(request);
				 // 获取放回状态对象
			 StatusLine statusLine = response.getStatusLine();
					// 获取状态码
					 int statusCode = statusLine.getStatusCode();
					 if (200 == statusCode) {
					// 获取服务器返回的对象
					 HttpEntity entity = response.getEntity();
				// 获取输入流
				 InputStream inputStream = entity.getContent();
				// 将输入流转化为字符串
					String data = StreamUtil.streamToString(inputStream);
					 handler.obtainMessage(RESULT_OK, data).sendToTarget();
					 } else {
					handler.obtainMessage(RESULT_CANCELED, statusCode).sendToTarget();
					}
					
					 } catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(RESULT_CANCELED, e).sendToTarget();
					 }
					 }
			
				}
			}
//		};
	}.start();
	
	}
	public static String uploadFile(File file) {  
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成  
        String PREFIX = "--", LINE_END = "\r\n";  
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型  
        String RequestURL = "http://"+ip+":8080/day12-upload/AServlet";  
        try {  
            URL url = new URL(RequestURL);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIME_OUT);  
            conn.setConnectTimeout(TIME_OUT);  
            conn.setDoInput(true); // 允许输入流  
            conn.setDoOutput(true); // 允许输出流  
            conn.setUseCaches(false); // 不允许使用缓存  
            conn.setRequestMethod("POST"); // 请求方式  
            conn.setRequestProperty("Charset", CHARSET); // 设置编码  
            conn.setRequestProperty("connection", "keep-alive");  
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="  
                    + BOUNDARY);  
            if (file != null) {  
                /** 
                 * 当文件不为空，把文件包装并且上传 
                 */  
                OutputStream outputSteam = conn.getOutputStream();  
  
                DataOutputStream dos = new DataOutputStream(outputSteam);  
                StringBuffer sb = new StringBuffer();  
                sb.append(PREFIX);  
                sb.append(BOUNDARY);  
                sb.append(LINE_END);  
                /** 
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 
                 * filename是文件的名字，包含后缀名的 比如:abc.png 
                 */  
  
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""  
                        + file.getName() + "\"" + LINE_END);  
                sb.append("Content-Type: application/octet-stream; charset="  
                        + CHARSET + LINE_END);  
                sb.append(LINE_END);  
                dos.write(sb.toString().getBytes());  
                InputStream is = new FileInputStream(file);  
                byte[] bytes = new byte[1024];  
                int len = 0;  
                while ((len = is.read(bytes)) != -1) {  
                    dos.write(bytes, 0, len);  
                }  
                is.close();  
                dos.write(LINE_END.getBytes());  
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)  
                        .getBytes();  
                dos.write(end_data);  
                dos.flush();  
                /** 
                 * 获取响应码 200=成功 当响应成功，获取响应的流 
                 */  
                int res = conn.getResponseCode();  
            	InputStream iss = conn.getInputStream();
                String result = StreamUtil.streamToString(iss);
                return result;
//                int res = conn.getResponseCode();  
//                if (res == 200) {  
//                    return SUCCESS;  
//                }  
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return FAILURE;  
    }  
	protected void takePic() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
	//设置 Action
	intent.setAction("android.media.action.IMAGE_CAPTURE");
		 file = new
		 File(Environment.getExternalStorageDirectory().getAbsolutePath(),"my.jpg");
		//创建 uri 对象
		Uri uri = Uri.fromFile(file);
		//设置图片的输出路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("android.intent.extras.CAMERA_FACING", 1); // 调用前置摄像头
		intent.putExtra("autofocus", true); // 自动对焦
		//开启 Activity
		startActivityForResult(intent, 1);
		
	}
	private void initView() {
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		et_name = (EditText) findViewById(R.id.et_name);
		et_age = (EditText) findViewById(R.id.et_age);
		et_jianjie = (EditText) findViewById(R.id.et_jianjie);
		tv_ok = (TextView) findViewById(R.id.tv_ok);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);
	}
  public void decodePic(){
	  BitmapFactory.Options options = new BitmapFactory.Options();  
      options.inJustDecodeBounds = true;  
      // 获取这个图片的宽和高  
      Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options); //此时返回bm为空  
      options.inJustDecodeBounds = false;  
       //计算缩放比  
      int be = (int)(options.outHeight / (float)150);  
      if (be <= 0)  
          be = 1;  
      options.inSampleSize = be;  
      //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦  
      bitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),options);  
      int w = bitmap.getWidth();  
      int h = bitmap.getHeight();  
      System.out.println(w+"   "+h);  
    //  ImageView iv=new ImageView(this);  
      iv_pic.setImageBitmap(bitmap);  
      //------------------------
      File file=new File("/sdcard/my.jpg"); 
      try { 
          FileOutputStream out=new FileOutputStream(file); 
          WindowManager wm = this.getWindowManager();
          
          int width = wm.getDefaultDisplay().getWidth();
          bitmap = MainActivity.centerSquareScaleBitmap(bitmap, 100);
          if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){ 
              out.flush(); 
              out.close(); 
          } 
      } catch (FileNotFoundException e) { 
          // TODO Auto-generated catch block 
          e.printStackTrace(); 
      } catch (IOException e) { 
          // TODO Auto-generated catch block 
          e.printStackTrace(); 
      }
  }
  @Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(file.exists()){
		decodePic();
		
	}
}
}
