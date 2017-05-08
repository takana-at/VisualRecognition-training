# tApp
トレーニングデータの抽出から、トレーニングデータの登録まで行うアプリです。

## ファイル構成
	tApp
	 |-src/test/java
	 |-src/main/java
		|-AddToCollection.java			collectionに学習データを追加します。
		|-AddToCollectionThread.java	
		|-AddTraindata.java			特定のサイトから、学習データを抽出した場合に、その学習データの数を増やします。
		|-AddTraindataThread.java		
		|-App.java			
		|-Crawl.java				クロールして、学習データを抽出する際に使用します。現在は、特定のサイトの場合のみに使用できます。
		|-Detectfaces.java			顔写真を学習データとして登録したい場合に、全身画像から顔写真の画像の学習データを作成します。
		|-DetectfacesThread.java
	|-.gitignore
	|-pom.xml
	|-README.md


