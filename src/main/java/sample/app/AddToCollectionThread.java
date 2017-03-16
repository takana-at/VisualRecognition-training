package sample.app;

import java.io.File;
import java.io.FileInputStream;

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

public class AddToCollectionThread implements Runnable{
	private static String api_key = "{api_key}";
	private static String collection_id = "{collection_id}";
	private String img_path;
	private String meta_path;
	
	public AddToCollectionThread(String img_path,String meta_path){
		this.img_path = img_path;
		this.meta_path = meta_path;
	}
	
	public void run(){
		addToCollection(img_path,meta_path);
	}
	
	public static String addToCollection(String imgdatafile,String metadatafile){
		String r = null;
		
		try{
			//. API がベータだからかもしれないが、HostnameVerifier を使わないとホスト名検証でエラーになる
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			CloseableHttpClient client = HttpClients.custom().setSSLHostnameVerifier( hostnameVerifier ).build(); //HttpClients.createDefault();

			HttpPost post = new HttpPost( "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/collections/" + collection_id + "/images?api_key=" + api_key + "&version=2016-05-20" );
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			File f1 = new File( imgdatafile );
			File f2 = new File( metadatafile );
			builder.addBinaryBody( "image_file", new FileInputStream( f1 ), ContentType.APPLICATION_OCTET_STREAM, f1.getName() );
			builder.addBinaryBody( "metadata", new FileInputStream( f2 ), ContentType.APPLICATION_JSON, f2.getName() );
			HttpEntity multipart = builder.build();
			post.setEntity( multipart );

			CloseableHttpResponse response = client.execute( post );
			int sc = response.getStatusLine().getStatusCode();
			System.out.println( "sc = " + sc );

//			if( sc == 200 ){
				HttpEntity entity = response.getEntity();
			    r = EntityUtils.toString( entity, "UTF-8" );
//			}
			System.out.println(r);
			response.close();
			client.close();
		}catch( Exception e ){
			e.printStackTrace();
		}
		return r;
	}
}



