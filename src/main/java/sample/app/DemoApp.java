package sample.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DemoApp {
	public static App app = new App();


	public static void main(String[] args) {

		if (args == null || args.length != 2){
			throw new IllegalArgumentException("no args...");
		}
		String site_url = args[0];
		/* トレーニングデータとして抽出するURLをperson_search_urlという変数で定義する */
		String person_search_url = args[1];

		/*
		 * 【原寸画像検索】というサイトのページからHTMLタグの抽出 抽出したHTMLから、さらに有名人に関する情報を抽出する
		 * 具体的に抽出する項目は、【有名人名】と【有名人の画像のURL】である そのページ全体の有名人に関する情報は、【<div
		 * class="thumbnailBox_list">】タグに格納されている また、有名人1人1人の情報は、【<div
		 * class="thumbnailBox">】タグに格納されている
		 * 2017/2/8段階では、【日本の女優一覧】というページから女優リストを取得する
		 */
		
		
		String app_html = app.getHTML(site_url);
		// System.out.println(app_html);
		/*
		 * TrimTextメソッドの実装 HTMLタグから ページ全体の有名人に関する情報は、【<div
		 * class="thumbnailBox">】タグの内容を文字列で抽出
		 */
		
///*2017/2/10追加	*/		
//		/*	現時点で未実装
//		 * 2017/2/10段階では、【日本の女優一覧】に該当するものを1~7ページまですべてまわす
//		 * categoryという変数で、女優一覧に該当するものを何ページあるか判定
//		 * 
//		 *　 
//		 */
//		String category = app.TrimText(app_html,new String[]{"pagenavi","<a"} ,"</div>" );
//		String unshaped_end_page = category.substring(0,category.lastIndexOf("<a"));
//		int end_page = Integer.parseInt(unshaped_end_page.substring(unshaped_end_page.lastIndexOf("\">")+2,unshaped_end_page.lastIndexOf("</a")));
//		
//			int num = 1;
//			while(true){
//			int site_url_num = Integer.parseInt(site_url.substring(site_url.lastIndexOf("_")+1,site_url.lastIndexOf(".")));
//			site_url = site_url.replace(String.valueOf(site_url_num), String.valueOf(num));
//			if(end_page < num){
//				break;
//			}
//			
//			System.out.println(site_url);
//			site_url_num ++;
//			num++;
//		}
///*2017/2/10追加(終了位置)	*/

		
		String person_group_info = app.TrimText(app_html, new String[] { "thumbnailBox_list", ">" }, "<span class=");
		// System.out.println(person_group_info);

		/*
		 * TrimTextNextメソッドの実装
		 * TrimTextメソッドで抽出した文字列から、有名人1人1人の情報は、【<a>】タグの内容を文字列で抽出 persons_info !=
		 * nullのとき、[0]番目の配列には該当する文字列に有名人1人に関する情報を、
		 * [1]番目の配列にはそれ以降の有名人全員に関する情報をを返す
		 * while文の中では、1回目のループでperson_group_infoから有名人1人の情報を文字列で検索
		 * 2回目以降のループで、persons_info[1]から有名人1人の情報を検索し、そのページの有名人全員に関する情報を抽出
		 * persons_info == nullのとき、そのページにある有名人に関する情報をすべて抽出できたためループを抜ける
		 */

		while (true) {
			String[] persons_info = app.TrimTextNext(person_group_info, new String[] { "thumbnailBox", ">" }, "</div>");
			if (persons_info == null) {
				break;
			}
			extractPersons(persons_info[0], person_search_url);
			System.out.println("");
			person_group_info = persons_info[1];
		}
	}

	/*
	 * 1人の有名人に関する情報をさらに細かく抽出 抽出する情報は、【有名人名】、【有名人の画像URL】、【ページのリンクURL】
	 * person_nameで、【有名人名】を定義する person_image_urlで、【有名人の画像URL】を定義する
	 * person_detail_pageで、【ページのリンクURL】を定義する
	 */
	public static String extractPersons(String str, String person_search_url) {
		String person_name = app.TrimText(str, new String[] { "</a>", ">" }, "</a>");
		String person_image_url = app.TrimText(str, new String[] { "<img", "src=\"" }, "\"");
		String person_detail_page = person_search_url + app.TrimText(str, new String[] { "<a", "href=\"" }, "\"");
/*
		shapeImg(person_name, person_image_url);
		produceText(person_name, person_image_url, person_detail_page);		
*/
		// try {
		// FileOutputStream output = new FileOutputStream("images/" +
		// person_name + ".jpg");
		// byte[] image = app.getImage(person_image_url);
		// output.write(image);
		// output.flush();
		// output.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		System.out.println(person_name + person_image_url + person_detail_page);
		return person_name + person_image_url + person_detail_page;
	}

	/*
	 * 抽出した【有名人の画像URL】を画像ファイルとして保存
	 */
	public static String shapeImg(String name, String img) {
		try {
			FileOutputStream output = new FileOutputStream("images/" + name + ".jpg");
			byte[] image = app.getImage(img);
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
	 * 抽出した【有名人名】、【有名人の画像URL】、【ページのリンクURL】を定義したmetadataファイルをjsonファイルとして作成する
	 */
	public static String produceText(String name, String img, String link) {
		try {
			FileWriter fw = new FileWriter("metadata/" + name + ".json");
			fw.write("{\n	\"name\":\"" + name + "\",\n	\"imgUrl\":\"" + img + "\",\n	\"pageLink\":\"" + link
					+ "\"\n}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
//	/*2017/2/10追加(開始位置)	*/		
//	/*	URL存在確認メソッド	*/
//    public static boolean isExistURL(String urlStr) {
//        URL url;
//        int status = 0;
//        try {
//            url = new URL(urlStr);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("HEAD");
//            conn.connect();
//            status = conn.getResponseCode();
//            conn.disconnect();
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        if (status == HttpURLConnection.HTTP_OK) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//	/*2017/2/10追加(終了位置)	*/	
	
}
