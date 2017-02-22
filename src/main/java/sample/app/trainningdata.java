package sample.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
		String person_search_url = args[1];
	
//		site_url = site_url.substring(0,site_url.lastIndexOf("/")+1);
		extractMetafile("./trainMetafile/2753570/2753570.json",site_url,person_search_url)	;
//		System.out.println(site_url);		
/*		
		String app_html = app.getHTML(site_url);
		retrievePersons(app_html,person_search_url);
*/
	}
	
	/*	2017/2/21追加	metafileの読み込みメソッド	*/
	public static String extractMetafile(String file_path,String site_url,String person_search_url){
		try {
			File file = new File(file_path);
			if(checkReadFile(file) == true){			
				BufferedReader br = new BufferedReader(new FileReader(file));
				String json_file = "";
				String line = br.readLine();
					/*	変数定義して取り出した1行目に値が入っているか判定し、json_fileに格納	*/
				while(line != null){
					json_file += line;
					line = br.readLine();
				}
				br.close();

				System.out.println(json_file);
				extractJSON(json_file,site_url,person_search_url);
				
				return json_file;
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
	public static String extractJSON(String file,String site_url,String person_search_url){
		JSONParser json_parser = new JSONParser();
		JSONObject json_object = null;
		try { 
			json_object = (JSONObject) json_parser.parse(file);
			String meta_name = (String) json_object.get("name");
			String meta_img_name = (String) json_object.get("img_name");
			String meta_img_url = (String) json_object.get("img_url");
			String meta_pageLink = (String) json_object.get("pageLink");
				/*	表示用の画像URLの抽出	*/
			String show_url = site_url + meta_name;
//			System.out.println(show_url);
			
			String app_html = app.getHTML(show_url);
			retrievePersons(app_html,person_search_url,meta_img_name,meta_img_url);
			
			System.out.println(meta_name + meta_img_name + meta_img_url + meta_pageLink);
			return meta_name + meta_img_name + meta_img_url + meta_pageLink;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
}

	/*
	 * HTMLタグの抽出 抽出したHTMLの文字列から、有名人1人1人の情報は、【<a>】タグの内容を文字列で抽出
	 *  persons_info !=nullのとき、[0]番目の配列には該当する文字列に有名人1人に関する情報を、 [1]番目の配列にはそれ以降の有名人全員に関する情報をを返す
	 * while文の中では、1回目のループでperson_group_infoから有名人1人の情報を文字列で検索
	 * 2回目以降のループで、persons_info[1]から有名人1人の情報を検索し、そのページの有名人全員に関する情報を抽出
	 * persons_info == nullのとき、そのページにある有名人に関する情報をすべて抽出できたためループを抜ける
	 */
		public static String retrievePersons(String html,String search_url,String show_img_name,String show_img_url){
			String person_group_info = app.TrimText(html, new String[] {"</head>","</div>" ,"firstHeading","mw-content-text",">"}, "<p><b>");
//			 System.out.println(person_group_info);
				extractPersons(person_group_info, search_url,show_img_name,show_img_url);
				System.out.println("");
			return html + search_url;
		}

	/*
	 * 1人の有名人に関する情報をさらに細かく抽出 抽出する情報は、【有名人名】、【有名人の画像URL】、【ページのリンクURL】
	 * person_nameで、【有名人名】を定義する person_image_urlで、【有名人の画像URL】を定義する
	 * person_detail_pageで、【ページのリンクURL】を定義する
	 * person_image_nameで、【有名人の画像のファイル名】の抽出(2017/02/13変数として追加)
	 */
	public static String extractPersons(String str, String person_search_url,String show_img_name,String show_img_url) {
		String person_name = app.TrimText(str, new String[] { "</span><br />" }, "</th>");
//		String person_birth_year = app.TrimText(str, new String[] { "%E5%B9%B4","title=\"" ,">"}, "<");
//		String person_birth_monthday = app.TrimText(str, new String[] { "%E6%97%A5","title=\"" ,">"}, "<");
//		String person_occupation = app.TrimText(str, new String[] { "%E8%A1%80%E6%B6%B2%E5%9E%8B","itemprop=", "itemprop=","title=\"",">"}, "<");
		String person_image_url = null;
		String person_image_name = null;
			/*	表示用URLに画像が掲載されている場合は掲載されている画像を、掲載されていない場合はトレーニングデータの画像を表示する	*/
		if(str.indexOf("img") != -1){
		person_image_url = "https:" + app.TrimText(str, new String[] { "colspan=","itemprop=" ,"img","srcset=\""}, " ");
		person_image_name = person_image_url.substring(person_image_url.lastIndexOf("/")+1,person_image_url.length()-4);
		}else{
			person_image_url = show_img_url;
			person_image_name = show_img_name;
			
		}
		
///*		String unshaped_person_occupation = app.TrimText(str, new String[] { "%E8%A1%80%E6%B6%B2%E5%9E%8B","itemprop=", "itemprop="}, "</td>");
//		System.out.println(unshaped_person_occupation);
//		String person_occupation[];
//		String[] occupations;
//		occupations = new String[2];
//		while (true) {
//			int i = 0 ;
//			person_occupation = new String[i+1];
//
//			occupations = app.TrimTextNext(unshaped_person_occupation, new String[] {"title=\"",">"}, "<");
//			if (occupations == null) {
//				break;
//			}
//				person_occupation[i] = occupations[0];
//				System.out.println(person_occupation[i]);
//				System.out.println(i);
//				System.out.println("");
//				
//			if(occupations[1].indexOf("title") == -1){
//				break;
//			}
//			unshaped_person_occupation = occupations[1];
//			i++;
//		}
//		System.out.println(person_name + person_image_url + person_birth_year + person_birth_monthday + person_image_name + person_occupation);
//		return person_name + person_image_url + person_birth_year + person_birth_monthday + person_image_name + person_occupation;
//*/
/*
		shapeImg(person_image_name, person_image_url);
 		produceText(person_name,person_image_name, person_image_url);		
*/
		 
		System.out.println(person_name + person_image_url  + person_image_name);
		return person_name + person_image_url  + person_image_name ;
		
	}
	/*
	 * 抽出した【有名人の画像URL】を画像ファイルとして保存
	 * 2017/02/13作成する画像ファイルのファイル名変更
	 */
	public static String shapeImg(String img_name, String img_url) {
		File file = new File("./image");
		if(file.exists() == false){
			file.mkdirs();
		}
		File person_file = new File(file + "/" + img_name);
		if(person_file.exists() == false){
			person_file.mkdirs();
		}
		try {
			FileOutputStream output = new FileOutputStream(person_file + "/" + img_name + ".jpg");
			byte[] image = app.getImage(img_url);
			output.write(image);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return null;
	}
	/*
	 * 抽出した【有名人名】、【有名人の画像のファイル名】、【有名人の画像URL】、【ページのリンクURL】を定義したmetadataファイルをjsonファイルとして作成する
	 * 2017/02/13作成するmetadataファイルのファイル名変更/jsonファイルに定義する情報として、【有名人の画像のファイル名】を追加
	 */
	public static String produceText(String name,String img_name, String img_url) {
		File file = new File("./metafile");
		if(file.exists() == false){
			file.mkdirs();
		}
		File person_file = new File(file + "/" + img_name);
		if(person_file.exists() == false){
			person_file.mkdirs();
		}
		
		try {
			FileWriter fw = new FileWriter(person_file + "/" + img_name + ".json");
			fw.write("{\"name\":\""+ name + "\",\"img_name\":\"" +  img_name + "\",\"img_url\":\"" + img_url + "\"}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		/*	2017/2/21追加	fileの読み込み可能か判別	*/
	public static boolean checkReadFile(File file){
		if(file.exists()){
			
		}if(file.isFile() && file.canRead()){
			return true;
		}
		return false;
	}
}
