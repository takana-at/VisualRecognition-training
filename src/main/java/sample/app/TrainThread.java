package sample.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TrainThread implements Runnable {
	
	private String info;
	private String path;
	App app = new App();
	
	public TrainThread(String info,String path){
		this.info = info;
		this.path = path;
	}
	
	public void run(){
//		for(int i = 10; i >= 0; i--){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
				addTrainImage(info,path);
//			
//			}
//		}
	}
	
	/*	2017/2/22	トレーニング画像データの追加	*/
	public String addTrainImage(String img_imfo,String img_path){
		String unencoded_train_img_url = app.TrimText(img_imfo,new String[]{"img","src=\""},"\"");
		String train_img_url = "";
		String train_img_path = "";
		try {
			String pre_img_url = unencoded_train_img_url.substring(0,unencoded_train_img_url.lastIndexOf("/")+1);
			String post_img_url = unencoded_train_img_url.substring(unencoded_train_img_url.lastIndexOf("/")+1,unencoded_train_img_url.length());
			train_img_url = pre_img_url + URLEncoder.encode(post_img_url,"UTF-8");
			
			System.out.println("\"pre_img_url\" = " + pre_img_url);
			System.out.println("\"post_img_url\" = " + post_img_url);
			String train_img_name = null;
			if(post_img_url.indexOf(".jpg?") != -1){
				train_img_name = post_img_url.substring(0,post_img_url.indexOf(".jpg?")+4);
			}else if(post_img_url.indexOf(".jpg") == -1 && post_img_url.indexOf(".jpeg") == -1 && post_img_url.indexOf(".png") == -1){
				train_img_name = post_img_url + ".jpg";
			}else{
				train_img_name = post_img_url;
			}
					
			String[] ng_name = {"?","\\","<",">","\\",":","*","\"","|"};
			for(int i = 0; i < ng_name.length; i++){
				if(train_img_name.indexOf(ng_name[i]) > -1){
					train_img_name = train_img_name.replace(ng_name[i], "");
				}
			}
	/*
			char[] ng_str = train_img_name.toCharArray();
			for(int i = 0; i < ng_str.length; i++){
				if(String.valueOf(ng_str[i]).getBytes().length > 1 ){
					System.out.println(ng_str[i]);
					System.out.println(i);
					train_img_name = train_img_name.replace(String.valueOf(ng_str[i]),String.valueOf(i));
				}
			}
	*/					
			System.out.println("\"train_img_url\" = " + train_img_url);
//			System.out.println("\"train_img_name\" = " + train_img_name);
			train_img_path = "./trainImage/" + img_path + "/" + train_img_name;
//			System.out.println("\"train_img_path\" = " + train_img_path);
			app.addImage(train_img_url,train_img_path);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return train_img_url + train_img_path;
	}

}
