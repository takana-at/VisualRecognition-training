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
				addTrainImage(info,path);
	}
	
	/*	2017/2/22	トレーニング画像データの追加	*/
	public String addTrainImage(String img_imfo,String img_path){
		String unencoded_train_img_url = app.TrimText(img_imfo,new String[]{"img","src=\""},"\"");
		String train_img_url = "";
		String train_img_path = "";
		String train_img_name = null;

		try {
			String pre_img_url = unencoded_train_img_url.substring(0,unencoded_train_img_url.lastIndexOf("/")+1);
			String post_img_url = unencoded_train_img_url.substring(unencoded_train_img_url.lastIndexOf("/")+1,unencoded_train_img_url.length());
			train_img_url = pre_img_url + URLEncoder.encode(post_img_url,"UTF-8");
			System.out.println("\"pre_img_url\" = " + pre_img_url);
			System.out.println("\"post_img_url\" = " + post_img_url);

			train_img_name = fileForm(post_img_url);
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

	/*	2017/2/24追加	画像ファイル名の形成行う	*/
	public String fileForm(String str){
		String train_img_name = null;
		if(str.indexOf(".jpg?") != -1){
			train_img_name = str.substring(0,str.indexOf(".jpg?")+4);
		}else if(str.indexOf(".jpg") == -1 && str.indexOf(".jpeg") == -1 && str.indexOf(".png") == -1){
			train_img_name = str + ".jpg";
		}else{
			train_img_name = str;
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
		return train_img_name;
	}
	
}
