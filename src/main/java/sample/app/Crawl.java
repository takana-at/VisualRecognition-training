package sample.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Crawl {
	public static TrainingDefinition app = new TrainingDefinition();

	public static void main(String[] args) {

		if (args == null || args.length != 2){
			throw new IllegalArgumentException("no args...");
		}
		
		/* トレーニングデータとして抽出するURLを定義する */
		String site_url = args[0];
		app.host_url = args[1];

		String app_html = app.getHTML(site_url);

		/*2017/2/10追加			
		 * categoryという変数で、有名人の検索結果は何ページあるか判定し、検索結果のページ数をend_pageという変数におき、検索結果すべての内容を抽出する
		 */
		String category = app.TrimText(app_html,new String[]{"pagenavi","<a"} ,"</div>" );
		String unshaped_end_page = category.substring(0,category.lastIndexOf("<a"));
		int end_page = Integer.parseInt(unshaped_end_page.substring(unshaped_end_page.lastIndexOf("\">")+2,unshaped_end_page.lastIndexOf("</a")));
//		System.out.println(end_page);
			int num = 1;
			while(true){
				int site_url_num = Integer.parseInt(site_url.substring(site_url.lastIndexOf("_")+1,site_url.lastIndexOf(".")));
				site_url = site_url.replace(String.valueOf(site_url_num), String.valueOf(num));
				app_html = app.getHTML(site_url);
				retrievePersons(app_html);
//				System.out.println(site_url);
				site_url_num ++;
				num++;
				if(end_page < num){
					break;
				}
			}
	}
	
	/* HTMLタグの抽出 抽出したHTMLの文字列から、person_group_infoで有名人1人1人の情報を抽出	 */
		public static String retrievePersons(String html){
			String person_group_info = app.TrimText(html, new String[] { "thumbnailBox_list", ">" }, "<span class=");
//			 System.out.println(person_group_info);
			while (true) {
				String[] persons_info = app.TrimTextNext(person_group_info, new String[] { "thumbnailBox", ">" }, "</div>");
				if (persons_info == null) {
					break;
				}
				extractPersons(persons_info[0]);
				System.out.println("");
				person_group_info = persons_info[1];
			}
			return html;
		}

	/*
	 * person_nameで【有名人名】、 person_image_urlで【有名人の画像URL】、 person_detail_pageで【ページのリンクURL】を定義する
	 * person_image_nameで、【有名人の画像のファイル名】の抽出(2017/02/13変数として追加)
	 */
	public static String extractPersons(String str) {
		
		try{
		String person_name = app.TrimText(str, new String[] { "</a>", ">" }, "</a>");
		String person_image_url = app.TrimText(str, new String[] { "<img", "src=\"" }, "\"");
		String person_detail_page = app.host_url + app.TrimText(str, new String[] { "<a", "href=\"" }, "\"");
		String person_image_name = person_image_url.substring(person_image_url.lastIndexOf("/")+1,person_image_url.length()-4);
		
		shapeImg(person_image_name, person_image_url);
		produceText(person_name,person_image_name, person_image_url, person_detail_page);		

		System.out.println(person_name + person_image_url + person_detail_page + person_image_name);
		return person_name + person_image_url + person_detail_page + person_image_name;

		}catch(Exception e){
			return null;
		}
		
	}

	/*
	 * 抽出した【有名人の画像URL】を画像ファイルとして保存
	 * 2017/02/13作成する画像ファイルのファイル名変更
	 */
	public static String shapeImg(String img_name, String img_url) {
	/*	2017/2/20変更	trainImageというフォルダを作成し、有名人ごとにフォルダ作成してから画像ファイル作成	*/	
		File file = new File("./trainImage");
		if(file.exists() == false){
			file.mkdirs();
		}
		File person_file = new File(file + "/" + img_name);
		if(person_file.exists() == false){
			person_file.mkdirs();
		}

		String person_img_path = person_file + "/" + img_name + ".jpg";
		app.addImage(img_url, person_img_path);
		return null;
	}

	/*
	 * 抽出した【有名人名】、【有名人の画像のファイル名】、【有名人の画像URL】、【ページのリンクURL】を定義したmetadataファイルをjsonファイルとして作成する
	 * 2017/02/13	作成するmetadataファイルのファイル名変更/jsonファイルに定義する情報として、【有名人の画像のファイル名】を追加
	 * 2017/2/20変更	trainMetafileいうフォルダを作成し、有名人ごとにフォルダを作成してからmetafile作成
	 */
	public static String produceText(String name,String img_name, String img_url, String link) {
	
		File file = new File("./trainMetafile");
		if(file.exists() == false){
			file.mkdirs();
		}
		File person_file = new File(file + "/" + img_name);
		if(person_file.exists() == false){
			person_file.mkdirs();
		}

		try {
			FileWriter fw = new FileWriter(person_file + "/" + img_name + ".json");
			fw.write("{\"name\":\""+ name + "\",\"img_name\":\"" +  img_name + "\",\"img_url\":\"" + img_url + "\",\"pageLink\":\"" + link
					+ "\"}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
