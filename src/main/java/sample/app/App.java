package sample.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Hello world!
 *
 */
public class App {
	/*
	 * TrimTextメソッドでは、取得したHTML全体の文字列をもとに、取得したい情報を取得する
	 * 第一引数srcには、取得したHTML全体の文字列を定義する 第二引数s1と第三引数s2で、HTMLから取得したい文字列の範囲を定義する
	 * まず、第二引数s1で配列を定義し、抽出したい文字列の開始位置を定義する
	 * (具体的に、配列には、どの文字列を順次検索して、開始位置の絞込みをするか記載する) 次に、第三引数s2で、抽出したい文字列の終了位置を定義する
	 * 
	 */
	public String TrimText(String src, String[] s1, String s2) {
		String r = src;

		try {
			int m = 0;
			int l = s1.length;
			for (int i = 0; i < l && m >= 0; i++) {
				int n = src.indexOf(s1[i], m);
				if (n < 0) {
					m = n;
				} else {
					m = n + s1[i].length();
				}
			}
			if (m > 0) {
				int n = src.indexOf(s2, m);
				if (n > m) {
					r = src.substring(m, n);
				} else {
					r = "";
				}
			} else {
				r = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			r = "";
		}

		return r;
	}

	/*
	 * TrimTextNextメソッドでは、TrimTextメソッドで抽出した文字列から、取得したい情報を配列として取得する
	 * 第一引数srcには、TrimTextメソッドで抽出した文字列を定義する
	 * 第二引数s1と第三引数s2で、TrimTextメソッドで抽出した文字列から、配列として取得したい文字列の範囲を定義する
	 * まず、第二引数s1で配列を定義し、抽出したい文字列の開始位置を定義する
	 * (具体的に、配列には、どの文字列を順次検索して、開始位置の絞込みをするか記載する) 次に、第三引数s2で、抽出したい文字列の終了位置を定義する
	 * // * 開始位置が第二引数s1、終了位置が第三引数s2であるものをすべて取り出す
	 */
	public String[] TrimTextNext(String src, String[] s1, String s2) {

		String[] r = null;

		try {
			int m = 0;
			int l = s1.length;
			for (int i = 0; i < l && m >= 0; i++) {
				int n = src.indexOf(s1[i], m);
				if (n < 0) {
					m = n;
				} else {
					m = n + s1[i].length();
				}
			}
			if (m > 0) {
				int n = src.indexOf(s2, m);
				if (n >= m) {
					r = new String[2];
					r[0] = src.substring(m, n);
					r[1] = src.substring(n + s2.length());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}

	/* HTMLタグの抽出 */
	public String getHTML(String url) {
		String html = "";

		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			/*
			 * if (host != null && host.length() > 0) { get.addHeader("Referer",
			 * host); }
			 */
			CloseableHttpResponse response = client.execute(get);
			int sc = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			html = EntityUtils.toString(entity, "UTF-8");
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return html;
	}

	/* 画像出力 */
	public byte[] getImage(String url) {
		byte[] img = null;

		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			CloseableHttpResponse response = client.execute(get);
			int sc = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			img = EntityUtils.toByteArray(entity);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return img;
	}

	/*	2017/2/23追加	URLの存在確認メソッド(mainクラスからの移行)	*/
    public static boolean isExistURL(String urlStr) {
        URL url;
        int status = 0;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            status = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }

        if (status == HttpURLConnection.HTTP_OK) {
            return true;
        } else {
            return false;
        }
    }

	/* 2017/2/23追加		画像fileの保存メソッド(mainクラスからの移行)	 */
	public String addImage(String img_url,String img_path){
		if(isExistURL(img_url) == true){
			try {
				FileOutputStream output = new FileOutputStream(img_path);
				byte[] image = getImage(img_url);
				output.write(image);
				output.flush();
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		return null;
	}	
	
	
}
