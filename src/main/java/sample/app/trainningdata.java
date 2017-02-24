package sample.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class trainningdata {
	public static App app = new App();

	public static void main(String[] args) {

		if (args == null || args.length != 2){
			throw new IllegalArgumentException("no args...");
		}
		
		/* トレーニングデータとして抽出するURLを定義する */
		String site_url = args[0];
		app.host_url = args[1];

		File folder = new File("./trainMetafile");
		File files[] = folder.listFiles();
		for(int i = 0; i < files.length; i++){
//			System.out.println("\"files[i]\" = " + files[i]);
			File[] file_path = files[i].listFiles();
			for(int j = 0; j < file_path.length; j++){
//				System.out.println("\"file_path[j]\" = " + file_path[j]);
				extractMetafile(String.valueOf(file_path[j]),site_url);
			}
		}

//		extractMetafile("./trainMetafile/593836/593836.json",site_url)	;
	}
	
	/*	2017/2/21追加	metafileの読み込みメソッド	*/
	public static String extractMetafile(String file_path,String site_url){
		try {
			File file = new File(file_path);
			if(app.checkReadFile(file) == true){			
				BufferedReader br = new BufferedReader(new FileReader(file));
				String json_file = "";
				String line = br.readLine();
					/*	変数定義して取り出した1行目に値が入っているか判定し、json_fileに格納	*/
				while(line != null){
					json_file += line;
					line = br.readLine();
				}
				br.close();

//				System.out.println("\"json_file\" = " + json_file);
				String img_path = file_path.substring(file_path.indexOf("file\\")+5,file_path.lastIndexOf("\\"));
//				System.out.println("\"img_path\" = " + img_path);
				extractJSON(json_file,site_url,img_path);
				
				return json_file + img_path;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}	
	/*	2017/2/21追加	metafileから抽出した文字列をJSON形式に変換
	 * 	JSONから必要な情報を抽出し、変数に置く
	*/
	public static String extractJSON(String file,String site_url,String img_path){
		JSONParser json_parser = new JSONParser();
		JSONObject json_object = null;
		try { 
			json_object = (JSONObject) json_parser.parse(file);
			String meta_name = (String) json_object.get("name");
			String meta_img_name = (String) json_object.get("img_name");
			String meta_img_url = (String) json_object.get("img_url");
			String meta_pageLink = (String) json_object.get("pageLink");
//				/*	2017/2/22追加	表示用の画像URLを抽出するサイトの定義	*/
//			String show_url = site_url + meta_name;
//			String show_html = app.getHTML(show_url);
//			retrievePersons(show_html,meta_img_name,meta_img_url);

			extractTrainUrl(meta_pageLink,img_path);
//			System.out.println(meta_name + meta_img_name + meta_img_url + meta_pageLink);
			return meta_name + meta_img_name + meta_img_url + meta_pageLink;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
}

	/*	2017/2/22	トレーニング画像データの追加用URLの定義	*/
	public static String extractTrainUrl(String url,String img_path){
		String train_html = app.getHTML(url);
		String train_info = app.TrimText(train_html, new String[] {"<body","<div","id=\"navigation\"","id=\"pan\"","<img src="," /></a></div>"}, " class=\"recommend\">");
//		System.out.println(train_info);
		while (true) {
			String[] train_img_imfo = app.TrimTextNext(train_info, new String[] {"class=\"imagebox\"","href=","target=",">"  },"<p" );
			if (train_img_imfo == null) {
				break;
			}
//		System.out.println(train_img_imfo[0]);
//		System.out.println();
			
			/*	マルチスレッド実装	*/
			TrainThread train = new TrainThread(train_img_imfo[0],img_path);
			Thread thread = new Thread(train);
			thread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			addTrainImage(train_img_imfo[0],img_path);
			train_info = train_img_imfo[1];
		}
		return null;
	}
	
//	/*	2017/2/22	トレーニング画像データの追加	*/
//	public static String addTrainImage(String img_imfo,String img_path){
//		String unencoded_train_img_url = app.TrimText(img_imfo,new String[]{"img","src=\""},"\"");
//		String train_img_url = "";
//		String train_img_path = "";
//		try {
//			String pre_img_url = unencoded_train_img_url.substring(0,unencoded_train_img_url.lastIndexOf("/")+1);
//			String post_img_url = unencoded_train_img_url.substring(unencoded_train_img_url.lastIndexOf("/")+1,unencoded_train_img_url.length());
//			train_img_url = pre_img_url + URLEncoder.encode(post_img_url,"UTF-8");
//			
//			System.out.println("\"pre_img_url\" = " + pre_img_url);
//			System.out.println("\"post_img_url\" = " + post_img_url);
//			String train_img_name = null;
//			if(post_img_url.indexOf(".jpg?") != -1){
//				train_img_name = post_img_url.substring(0,post_img_url.indexOf(".jpg?")+4);
//			}else if(post_img_url.indexOf(".jpg") == -1 && post_img_url.indexOf(".jpeg") == -1 && post_img_url.indexOf(".png") == -1){
//				train_img_name = post_img_url + ".jpg";
//			}else{
//				train_img_name = post_img_url;
//			}
//					
//			String[] ng_name = {"?","\\","<",">","\\",":","*","\"","|"};
//			for(int i = 0; i < ng_name.length; i++){
//				if(train_img_name.indexOf(ng_name[i]) > -1){
//					train_img_name = train_img_name.replace(ng_name[i], "");
//				}
//			}
//	/*
//			char[] ng_str = train_img_name.toCharArray();
//			for(int i = 0; i < ng_str.length; i++){
//				if(String.valueOf(ng_str[i]).getBytes().length > 1 ){
//					System.out.println(ng_str[i]);
//					System.out.println(i);
//					train_img_name = train_img_name.replace(String.valueOf(ng_str[i]),String.valueOf(i));
//				}
//			}
//	*/					
//			System.out.println("\"train_img_url\" = " + train_img_url);
////			System.out.println("\"train_img_name\" = " + train_img_name);
//			train_img_path = "./trainImage/" + img_path + "/" + train_img_name;
////			System.out.println("\"train_img_path\" = " + train_img_path);
//			app.addImage(train_img_url,train_img_path);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return train_img_url + train_img_path;
//	}
	
	

	
	
	
	
	
	
	
//	/*
//	 * HTMLタグの抽出 抽出したHTMLの文字列から、有名人1人1人の情報は、【<a>】タグの内容を文字列で抽出
//	 */
//		public static String retrievePersons(String html,String show_img_name,String show_img_url){
////			String person_group_info = app.TrimText(html, new String[] {"</head>","</div>" ,"firstHeading","mw-content-text",">"}, "<p><b>");
//			String person_group_info = app.TrimText(html, new String[] {"</head>","</div>" ,"firstHeading","mw-content-text","<table"}, "<p><b>");			
////			 System.out.println(person_group_info);
////				extractPersons(person_group_info,show_img_name,show_img_url);
//				System.out.println("");
//			return html;
//		}
//
//	/*
//	 * 1人の有名人に関する情報をさらに細かく抽出 抽出する情報は、【有名人名】、【有名人の表示用画像URL】
//	 * person_nameで、【有名人名】を定義する person_image_urlで、【有名人の表示用画像URL】を定義する
//	 * 2017/2/22追加	person_img_folder_nameで、、【有名人の表示用画像】を格納するフォルダー名を定義
//	 * must!! 2017/2/22時点で、スクレイピングのコーディングに不備あり	
//	 */
//	public static String extractPersons(String str,String show_img_name,String show_img_url) {
//		String person_name = app.TrimText(str, new String[] { "</span><br />" }, "</th>");
//		String person_image_url = null;
//		String person_image_name = null;
//			/*	2017/2/22追加	類似画像検索後の表示用画像URLの抽出	
//			 * 表示用URLに画像が掲載されている場合は掲載されている画像を、掲載されていない場合はトレーニングデータの画像を表示する	*/
//		String unshaped_url = app.TrimText(str, new String[] { person_name,"text-align:center","colspan=","text-align:center","itemprop="},"scope");
////		System.out.println(unshaped_url);
//		
//		if(unshaped_url.indexOf("img") != -1){		
//			person_image_url = "https:" + app.TrimText(unshaped_url, new String[] { "img","srcset=\""}, " ");
////			person_image_url = "https:" + app.TrimText(str, new String[] { person_name,"colspan=","itemprop=" ,"img","srcset=\""}, " ");
//			person_image_name = person_image_url.substring(person_image_url.lastIndexOf("/")+1,person_image_url.length()-4);
//		}else{
//			person_image_url = show_img_url;
//			person_image_name = show_img_name;
//		}
//		String img_folder_name = show_img_name;
///*		
//		shapeImg(person_image_name, person_image_url,img_folder_name);
// 		produceText(person_name,person_image_name, person_image_url);		
//*/
//		 
//		System.out.println(person_name + " " +  person_image_url  + " " + person_image_name	/* + img_folder_name	*/);
//		return person_name + person_image_url  + person_image_name;
//		
//	}
//	/*
//	 * 抽出した【有名人の画像URL】を画像ファイルとして保存
//	 * 2017/02/13作成する画像ファイルのファイル名変更
//	 */
//	public static String shapeImg(String img_name, String img_url,String img_folder_name) {
//		File file = new File("./showImage");
//		if(file.exists() == false){
//			file.mkdirs();
//		}
//		File person_file = new File(file + "/" + img_folder_name);
//		if(person_file.exists() == false){
//			person_file.mkdirs();
//		}
//		try {
//			FileOutputStream output = new FileOutputStream(person_file + "/" + img_name + ".jpg");
//			byte[] image = app.getImage(img_url);
//			output.write(image);
//			output.flush();
//			output.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		return null;
//	}
//	/*
//	 * 抽出した【有名人名】、【有名人の画像のファイル名】、【有名人の画像URL】、【ページのリンクURL】を定義したmetadataファイルをjsonファイルとして作成する
//	 * 2017/02/13作成するmetadataファイルのファイル名変更/jsonファイルに定義する情報として、【有名人の画像のファイル名】を追加
//	 */
//	public static String produceText(String name,String img_name, String img_url) {
//		File file = new File("./metafile");
//		if(file.exists() == false){
//			file.mkdirs();
//		}
//		File person_file = new File(file + "/" + img_name);
//		if(person_file.exists() == false){
//			person_file.mkdirs();
//		}
//		
//		try {
//			FileWriter fw = new FileWriter(person_file + "/" + img_name + ".json");
//			fw.write("{\"name\":\""+ name + "\",\"img_name\":\"" +  img_name + "\",\"img_url\":\"" + img_url + "\"}");
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
}
