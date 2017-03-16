package sample.app;

import java.io.File;

public class AddToCollection {
	public static App app = new App();

	public static void main(String[] args) {
		File imgFolder = new File("./trainImage");
		File imgId[] = imgFolder.listFiles();
		for(int i = 0; i < imgId.length; i++){
			System.out.println("\"imgId[i]\" = " + imgId[i]);
			File[] picture = imgId[i].listFiles();
			String metaFolder = String.valueOf(imgId[i]).replace("Image"+File.separator,"Metafile"+File.separator) + String.valueOf(imgId[i]).substring(String.valueOf(imgId[i]).lastIndexOf(File.separator),String.valueOf(imgId[i]).length()) + ".json";
			System.out.println("\"metaFolder\"=" + metaFolder);
			
////			extractMetafile(String.valueOf(metaFolder));
			for(int j = 0; j < picture.length; j++){
				System.out.println("\"picture[j]\" = " + picture[j]);
////			//画像ファイルの読込みメソッド
////			extractImage(String.valueOf(imgId[i]),String.valueOf(picture[j]));
				
				AddToCollectionThread addCollection = new AddToCollectionThread(String.valueOf(picture[j]),String.valueOf(metaFolder));
				Thread thread = new Thread(addCollection);
				thread.start();
				try {
					thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
}
//
////		/*	metafileの読み込みメソッド	*/
////		public static String extractMetafile(String file_path){
////			try {
////				File file = new File(file_path);
////				if(app.checkReadFile(file) == true){			
////					BufferedReader br = new BufferedReader(new FileReader(file));
////					String json_file = "";
////					String line = br.readLine();
////						/*	変数定義して取り出した1行目に値が入っているか判定し、json_fileに格納	*/
////					while(line != null){
////						json_file += line;
////						line = br.readLine();
////					}
////					br.close();
////
////					System.out.println("\"file_path\" = " + file_path);
////					System.out.println("\"json_file\" = " + json_file);
////					
////					return json_file;
////				}
////				
////			} catch (FileNotFoundException e) {
////				e.printStackTrace();
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
////			return null;
////		}	
////		/*	画像ファイルの読込みメソッド	*/
////		public  static String extractImage(String id,String img){
////			File file = new File(img);
////			
////			File mkFolder = new File("./outputImage");
////			if(mkFolder.exists() == false){
////				mkFolder.mkdirs();
////			}
////			File mkFile = new File(id.replace("train", "output"));
////			System.out.println(id.replace("train", "output"));
////			if(mkFile.exists() == false){
////				mkFile.mkdirs();
////			}			
////			File output_file = new File(img.replace("train", "output"));
////			System.out.println(img.replace("train", "output"));
////			
////			try {
////				BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
////				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(output_file));
////				byte[] buf = new byte[256];
////				int i = 0;
////				while((i = input.read(buf)) != -1){
////					output.write(buf,0,i);
////				}
////					input.close();
////					output.flush();
////					output.close();
////
//////				System.out.println(input);
////			} catch (FileNotFoundException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (IOException e) {
////				e.printStackTrace();
////			}		
////			return null;
////}
	

