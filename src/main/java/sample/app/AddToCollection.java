package sample.app;

import java.io.File;

public class AddToCollection {
	public static TrainingDefinition app = new TrainingDefinition();

	public static void main(String[] args) {
		if(args == null || args.length !=1){
			throw new IllegalArgumentException("no args...");
		}
		String trainImage = args[0];
		
		File imgFolder = new File(trainImage);
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


