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

public class AddTraindata {
	public static TrainingDefinition app = new TrainingDefinition();
	public static String picture_path;

	public static void main(String[] args) {

		if (args == null || args.length != 4){
			throw new IllegalArgumentException("no args...");
		}
		
		/* トレーニングデータとして抽出するURLを定義する */
		String site_url = args[0];
		app.host_url = args[1];
		String meta_path = args[2];
		picture_path = args[3];

		File folder = new File(meta_path);
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

				System.out.println("\"file_path\" = " + file_path);
				String img_path = file_path.substring(file_path.indexOf("file" + File.separator)+5,file_path.lastIndexOf(File.separator));
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
			String[] train_img_info = app.TrimTextNext(train_info, new String[] {"class=\"imagebox\"","href=","target=",">"  },"<p" );
			if (train_img_info == null) {
				break;
			}
//		System.out.println(train_img_info[0]);
//		System.out.println();
			
			/*	マルチスレッド実装	*/
			AddTraindataThread train = new AddTraindataThread(train_img_info[0],img_path);
			Thread thread = new Thread(train);
			thread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			addTrainImage(train_img_imfo[0],img_path);
			train_info = train_img_info[1];
		}
		return null;
	}
		
}
