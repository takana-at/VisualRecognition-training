package sample.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class DemoApp {
	public static App app = new App();

	public static void main(String[] args) {

		if (args == null || args.length != 2){
			throw new IllegalArgumentException("no args...");
		}
		
		/* トレーニングデータとして抽出するURLを定義する */
		String site_url = args[0];
		String person_search_url = args[1];

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
				retrievePersons(app_html,person_search_url);
//				System.out.println(site_url);
				site_url_num ++;
				num++;
				if(end_page < num){
					break;
				}
			}
	}
	
	/*
	 * HTMLタグの抽出 抽出したHTMLの文字列から、有名人1人1人の情報は、【<a>】タグの内容を文字列で抽出
	 *  persons_info !=nullのとき、[0]番目の配列には該当する文字列に有名人1人に関する情報を、 [1]番目の配列にはそれ以降の有名人全員に関する情報をを返す
	 * while文の中では、1回目のループでperson_group_infoから有名人1人の情報を文字列で検索
	 * 2回目以降のループで、persons_info[1]から有名人1人の情報を検索し、そのページの有名人全員に関する情報を抽出
	 * persons_info == nullのとき、そのページにある有名人に関する情報をすべて抽出できたためループを抜ける
	 */
		public static String retrievePersons(String html,String search_url){
			String person_group_info = app.TrimText(html, new String[] { "thumbnailBox_list", ">" }, "<span class=");
//			 System.out.println(person_group_info);
			while (true) {
				String[] persons_info = app.TrimTextNext(person_group_info, new String[] { "thumbnailBox", ">" }, "</div>");
				if (persons_info == null) {
					break;
				}
				extractPersons(persons_info[0], search_url);
				System.out.println("");
				person_group_info = persons_info[1];
			}
			return html + search_url;
		}

	/*
	 * 1人の有名人に関する情報をさらに細かく抽出 抽出する情報は、【有名人名】、【有名人の画像URL】、【ページのリンクURL】
	 * person_nameで、【有名人名】を定義する person_image_urlで、【有名人の画像URL】を定義する
	 * person_detail_pageで、【ページのリンクURL】を定義する
	 * person_image_nameで、【有名人の画像のファイル名】の抽出(2017/02/13変数として追加)
	 */
	public static String extractPersons(String str, String person_search_url) {
		String person_name = app.TrimText(str, new String[] { "</a>", ">" }, "</a>");
		String person_image_url = app.TrimText(str, new String[] { "<img", "src=\"" }, "\"");
		String person_detail_page = person_search_url + app.TrimText(str, new String[] { "<a", "href=\"" }, "\"");
		String person_image_name = person_image_url.substring(person_image_url.lastIndexOf("/")+1,person_image_url.length()-4);

		shapeImg(person_image_name, person_image_url);
		produceText(person_name,person_image_name, person_image_url, person_detail_page);		

		System.out.println(person_name + person_image_url + person_detail_page + person_image_name);
		return person_name + person_image_url + person_detail_page + person_image_name;
	}

	/*
	 * 抽出した【有名人の画像URL】を画像ファイルとして保存
	 * 2017/02/13作成する画像ファイルのファイル名変更
	 */
	public static String shapeImg(String img_name, String img_url) {
		try {
			FileOutputStream output = new FileOutputStream("images/" + img_name + ".jpg");
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
	public static String produceText(String name,String img_name, String img_url, String link) {
		try {
			FileWriter fw = new FileWriter("metadata/" + img_name + ".json");
			fw.write("{\n	\"name\":\""+ name + "\",\n	\"img_name\":\"" +  img_name + "\",\n	\"img_url\":\"" + img_url + "\",\n	\"pageLink\":\"" + link
					+ "\"\n}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
