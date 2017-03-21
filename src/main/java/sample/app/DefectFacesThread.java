package sample.app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DefectFacesThread implements Runnable{
	private static String api_key = "{api_key}"; 
	private String picture;
	
	public DefectFacesThread(String picture){
		this.picture = picture;
	}
	public void run(){
		detectFace(picture);
	}
	/*	Visual Recognitionのdetect face機能実装	*/
	public static String detectFace(String imgfile){
		String r = null;
		
		try{
			//. API がベータだからかもしれないが、HostnameVerifier を使わないとホスト名検証でエラーになる
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			CloseableHttpClient client = HttpClients.custom().setSSLHostnameVerifier( hostnameVerifier ).build(); //HttpClients.createDefault();
	
			HttpPost post = new HttpPost( "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/detect_faces?api_key=" + api_key + "&version=2016-05-20" );
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			File f1 = new File( imgfile );
			builder.addBinaryBody( "images_file", new FileInputStream( f1 ), ContentType.APPLICATION_OCTET_STREAM, f1.getName() );
			HttpEntity multipart = builder.build();
			post.setEntity( multipart );
	
			CloseableHttpResponse response = client.execute( post );
			int sc = response.getStatusLine().getStatusCode();
			System.out.println( "sc = " + sc );
	
				HttpEntity entity = response.getEntity();
			    r = EntityUtils.toString( entity, "UTF-8" );
			    System.out.println(r);
			    extractJSON(r,imgfile);
			response.close();
			client.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
		return r;
	}
	/*	JSON形式のresponseの内容の抽出メソッド	*/
	public static long extractJSON(String res,String path){
		long json_height = 0;
		long json_left = 0;
		long json_top = 0;
		long json_width = 0;
		
	    try{
			JSONParser json_parser = new JSONParser();
			JSONObject json_object = (JSONObject) json_parser.parse(res);
			
			JSONArray json_images = ( JSONArray )json_object.get( "images" );
			JSONObject json_image = ( JSONObject )json_images.get( 0 );
			JSONArray json_faces = ( JSONArray )json_image.get( "faces" );
			for( int i = 0; i < json_faces.size(); i ++ ){
				JSONObject json_face = ( JSONObject )json_faces.get( i );
//				JSONObject json_age = (JSONObject)json_face.get("age");
//				long json_max = (Long)json_age.get("max");
//				System.out.println("json_max=" + json_max);
				JSONObject json_location = (JSONObject)json_face.get("face_location");
				json_height = (Long) json_location.get("height");
				json_left = (Long)json_location.get("left");
				json_top = (Long)json_location.get("top");
				json_width = (Long)json_location.get("width");
				System.out.println("\"json_height\"=" + json_height + "\"json_left\"" + json_left + "\"json_top\"" + json_top + "\"json_width\"" + json_width);
				JSONObject json_gender = (JSONObject)json_face.get("gender");
				String gender = String.valueOf(json_gender.get("gender"));
				System.out.println("\"gender\"=" + gender);	
				cutImage((int)json_width,(int)json_height,(int)json_left,(int)json_top,path);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}			
		return json_height + json_left + json_top + json_width;
	}
	
	/*	画像の切り取り	*/
	public static int cutImage(int w,int h,int x,int y,String path){
		BufferedImage brImg;
//		int open_height = 0;
//		int open_width = 0;
		try {
			File open_file = new File(path);
			brImg = ImageIO.read(open_file);
//			open_height = brImg.getHeight();
//			open_width = brImg.getWidth();
//			System.out.println("\"open_height\"=" + open_height + "\"open_width\"" + open_width);
			BufferedImage cut_img = null; 
			cut_img = brImg.getSubimage(x,y,w,h);
			
			File mkFolder = new File("./faceImage");
			if(mkFolder.exists() == false){
				mkFolder.mkdirs();
			}
			String face_path = path.substring(0, path.lastIndexOf(File.separator));
			File mkId = new File(face_path.replace("trainImage","faceImage"));
			if(mkId.exists() == false){
				mkId.mkdirs();
			}			
			FileOutputStream output = new FileOutputStream(path.replace("trainImage","faceImage") + "_face.jpg");
			ImageIO.write(cut_img,"jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
		
	}

}
