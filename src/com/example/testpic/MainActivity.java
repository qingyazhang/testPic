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
import java.util.UUID;

import com.example.testpic.service.UpLoadService;
import com.example.testpic.utils.SpUtil;
import com.example.testpic.utils.StreamUtil;


import android.R.color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if(msg.what==11){
				iv_pic.setImageBitmap((Bitmap)msg.obj);
				return;
			}else if(msg.what==12){
				proDialog.dismiss();
			String mresult = (String) msg.obj;
			openDialog(mresult);}
			else if(msg.what==13){
				proDialog.dismiss();
				showFieldDialog();
			}else if(msg.what==14){
				proDialog.dismiss();
				showFieldDialog2();
			}
		};
	};
	 private static final String TAG = "uploadFile";  
	    private static final int TIME_OUT = 10 * 10000000; // 超时时间  
	    private static final String CHARSET = "utf-8"; // 设置编码  
	    public static final String SUCCESS = "1";  
	    public static final String FAILURE = "0";
	private ImageView bt_paizhao;
	private ImageView bt_zhuce;
	private File file;

	private EditText edit;
	private ImageView iv_pic;
	private AlertDialog dialog;
	private static String ip;
	private ProgressDialog proDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("MainActivity 启动了！！！！！！！！！！！！！！！！！！！！！！！！！ ");
		
		initIP();
		initView();
		initAnim();
		initListener();
	}



//	@Override
//	private void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		initView();
//		initListener();
//		//initSliderMenu();
//	}

//	private void initSliderMenu() {
//		// TODO Auto-generated method stub
//		setBehindContentView(R.layout.activity_slider);
//		SlidingMenu slidingMenu = getSlidingMenu();
//		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏触摸
//		slidingMenu.setBehindOffset(266);//屏幕预留200像素宽度
//	}

	private void initAnim() {
		// TODO Auto-generated method stub
	
				RelativeLayout rl_zhuce = (RelativeLayout) findViewById(R.id.rl_zhuce);
				RelativeLayout rl_paizhao = (RelativeLayout) findViewById(R.id.rl_paizhao);
				 ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rl_zhuce, "alpha", 0f, 1f,1f);        
			     objectAnimator.setDuration(3000).start(); 
			 
			     ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(rl_paizhao, "alpha", 0f, 1f,1f);        
			   
			     objectAnimator2.setDuration(4182).start();

	
	}



	protected void showFieldDialog2() {
		// TODO Auto-generated method stub
		  AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器  
	        builder.setTitle("严重错误！！"); //设置标题  
	        builder.setMessage("严重错误！！\n服务器端出现了严重错误！！ "); //设置内容  
	     
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.dismiss(); //关闭dialog  
	            
	            }  
	        });  
	        builder.show(); 
	}



	private void initIP() {
		// TODO Auto-generated method stub
		SpUtil.putString(getApplicationContext(), "IP", "47.93.87.89");
		//SpUtil.putString(getApplicationContext(), "IP", "192.168.42.144");
	}



	/**
	 * 返回服务器端数据后显示内容
	 * @param mresult
	 */
	protected void openDialog(String mresult) {
		// TODO Auto-generated method stub
		System.out.println("mresult is"+mresult);
		 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	        View view = View
	                .inflate(getApplicationContext(), R.layout.activity_resultdialog, null);
	        builder.setView(view);
	        builder.setCancelable(true);
	      iv_pic = (ImageView) view.findViewById(R.id.iv_rpic);
	      TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
	      TextView tv_id= (TextView) view.findViewById(R.id.tv_id);
	      TextView tv_age= (TextView) view.findViewById(R.id.tv_age);
	      TextView tv_jianjie= (TextView) view.findViewById(R.id.tv_jianjie);
	      
	      String[] str = mresult.split("&");
	      for(int i=0;i<str.length;i++){
	    	  System.out.println(str[i]);
	      }
	      
	      tv_id.setText(str[0]);
	      tv_name.setText(str[2]);
	      tv_age.setText(str[3]);
	      tv_jianjie.setText(str[4]);
	      showPic(iv_pic,str[0]);
	        //取消或确定按钮监听事件处理
	        AlertDialog dialog = builder.create();
	        dialog.show(); 
	}



	/**
	 * 展示图片
	 * @param iv_pic
	 * @param string
	 */
	private void showPic(ImageView iv_pic,final String string) {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
			String ip = SpUtil.getString(getApplicationContext(), "IP", "");
				
				String path = "http://"+ip+":8080/day12-upload/pictures/"+string+".jpg";
				
				System.out.println("path:"+path);
				try {
					URL url = new URL(path);
					HttpURLConnection connection =(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					connection.connect();
					InputStream inputStream = connection.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				 //创建一个新的 message 对象
					 Message msg = new Message();
					msg.what = 11;
					//将 bitmap 对象赋值给 msg 的 obj 属性
					msg.obj = bitmap;
					
					 handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = RESULT_CANCELED;
					handler.sendMessage(msg);
					
				}
			};
		}.start();
		
	}

	private void initListener() {
		// TODO Auto-generated method stub
		bt_paizhao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			takePic();
//				Intent intent = new Intent(getApplicationContext(), CaneraActivity.class);
//				startActivity(intent);
			}
		});
		bt_zhuce.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),ZhuceActivity.class));
			}
		});

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
		bt_paizhao = (ImageView) findViewById(R.id.bt_paizhao);
		bt_zhuce = (ImageView) findViewById(R.id.bt_zhuce);
		
	}
	 public void decodePic(){
		  BitmapFactory.Options options = new BitmapFactory.Options();  
	      options.inJustDecodeBounds = true;  
	      // 获取这个图片的宽和高  
	      Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options); //此时返回bm为空  
	      options.inJustDecodeBounds = false;  
	       //计算缩放比  
	      int be = (int)(options.outHeight / (float)240);  
	      if (be <= 0)  
	          be = 1;  
	      options.inSampleSize = be;  
	      //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦  
	      bitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),options);  
	      int w = bitmap.getWidth();  
	      int h = bitmap.getHeight();  
	      System.out.println(w+"   "+h);  
	    //  ImageView iv=new ImageView(this);  
	     // iv_pic.setImageBitmap(bitmap);  
	      Toast.makeText(getApplicationContext(), "上传成功", 0).show();
	      //------------------------
	      File file=new File("/sdcard/my.jpg"); 
	      try { 
	          FileOutputStream out=new FileOutputStream(file); 
	          bitmap = MainActivity.centerSquareScaleBitmap(bitmap, 224);
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
		
		ip = SpUtil.getString(getApplicationContext(), "IP", "");
		if(ip.equals("")){
			Toast.makeText(getApplicationContext(), "ip不能为空", 0).show();
			return;
		}
		if(!file.exists()){
			Toast.makeText(getApplicationContext(), "咦，没有检测到你拍的照片哦^_^", 0).show();
			return;
		}
		if(file.exists()){
			showProgressDialog();
			decodePic();
			Toast.makeText(getApplicationContext(), "开始上传", 0).show();
			new Thread(){
				

				public void run() {
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"my.jpg");
					String result = uploadFile(file);
					boolean tag = true;
					while(tag){
					if(result!=null&&result.length()>3){
					
						if(result.equals("wrong")){
							Message msg = Message.obtain();
							System.out.println();
							msg.what = 14;
							handler.sendMessage(msg);
							
						break;
						}
						if(result.equals("null")){
							Message msg = Message.obtain();
							msg.what = 13;
							handler.sendMessage(msg);
							
						break;
						}else{
						Message msg = Message.obtain();
						msg.obj = result;
						msg.what=12;
						handler.sendMessage(msg);
						tag = false;
						break;
						}
					}else{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				};
			}.start();
		}
	}
	  private void showProgressDialog() {
		proDialog = new ProgressDialog(this);
		proDialog.setTitle("提示");
		proDialog.setMessage("正在检测人脸，请稍候...");
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		proDialog.show();
		
	}



	protected void showFieldDialog() {
		// TODO Auto-generated method stub
		  AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器  
	        builder.setTitle("未检测到人脸"); //设置标题  
	        builder.setMessage("可能的原因有\n1.光线不足\n2.您尚未注册\n3.服务器内部错误\n请您重新尝试拍照"); //设置内容  
	     
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.dismiss(); //关闭dialog  
	            
	            }  
	        });  
	      
	  
	      
	        //参数都设置完成了，创建并显示出来  
	        builder.show();  
	}

	public static String uploadFile(File file) {  
	        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成  
	        String PREFIX = "--", LINE_END = "\r\n";  
	        String CONTENT_TYPE = "multipart/form-data"; // 内容类型  
	       
	        String RequestURL = "http://"+ip+":8080/day12-upload/PaizhaoServlet";  
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
	                file.delete();
	                /** 
	                 * 获取响应码 200=成功 当响应成功，获取响应的流 
	                 */  
	                int res = conn.getResponseCode();  
	            	InputStream iss = conn.getInputStream();
	                String result = StreamUtil.streamToString(iss);
	                return result;
//	                int res = conn.getResponseCode();  
//	                if (res == 200) {  
//	                    return SUCCESS;  
//	                }  
	            }  
	        } catch (MalformedURLException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return FAILURE;  
	    }  
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	
		startService(new Intent(this, UpLoadService.class));
		finish();
	}
	/**
    
	   * @param bitmap      原图
	   * @param edgeLength  希望得到的正方形部分的边长
	   * @return  缩放截取正中部分后的位图。
	   */
	  public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
	  {
	   if(null == bitmap || edgeLength <= 0)
	   {
	    return  null;
	   }
	                                                                                
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	                                                                                
	   if(widthOrg > edgeLength && heightOrg > edgeLength)
	   {
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    Bitmap scaledBitmap;
	                                                                                 
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                      
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                    
	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
	        scaledBitmap.recycle();
	       }
	       catch(Exception e){
	        return null;
	       }       
	   }
	                                                                                     
	   return result;
	  }
}
