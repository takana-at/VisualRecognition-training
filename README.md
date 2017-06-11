# tApp
トレーニングデータの抽出から、トレーニングデータの登録まで行うアプリです。

## 実装手順
1. Bluemixのカタログ画面から、Virtual Recognitionサービスを作成し、サービスの{api_key}をメモしておきます。  
2. Virtual Recognitionサービス上にcollection を作成し、{collection_id}をメモしておきます。  
　今回は、Watson API Explorerを用いて、collectionを作成します。  
  ①下記のURLから、Watson API Explorerを開きます。  
    https://watson-api-explorer.mybluemix.net/apis/visual-recognition-v3#/  
  ②URLを開いたら、下記をクリックします。  
    post /v3/collections　(Create a new collection - beta)  
  ③{api_key} に、先ほどメモした{api_key} を入力し、{name}に任意の名前を入力し、{Try it out!}をクリックします。  
  ④responseで返ってきた{collection_id}をメモしておきます。  
3. githubのtAppのコードをダウンロードします。  
4. コードをインストール後、collectionに、類似画像検索のトレーニングデータの画像を登録します。    
  	①tAppの直下にtrainImageフォルダと、trainMetafileフォルダを作成します。  
  	②trainImageフォルダに画像のIDをふったフォルダを作成し、そのフォルダに画像を登録します。  
   	たとえば、下記のように{tora}とIDをふったフォルダに、トラの画像を格納します。


 	 tApp
	   |-trainImage
	 	-tora  
		　　 |-tora1.png  
		　　 |-tora2.png  

	③traiinMetafileフォルダに画像のIDをふったフォルダを作成し、そのフォルダにtrainImageで格納した画像ファイルに関するメタ情報をjsonファイルとして登録します。  
   	たとえば、下記のように{tora}とIDをふったフォルダに、tora.jsonファイルで定義します。 
 
 	 tApp
	  |-trainMetafile
	 	-tora
		  -tora.json
  
  	④AddToCollectionThread.javaファイルを開き、{api_key} と {collection_id}を、先ほどメモした{api_key} と {collection_id}に変更します。   
  	⑤AddToCollectio.javaファイルの引数に、{./trainImage}と入力し、AddToCollectio.javaを実行させます。  
5. 学習データを登録したことを確認します。  
	①Watson API Explorerから、下記をクリックし、{api_key}を入力し、{Try it out!}をクリックします。  
   		get /v3/collections	(List all custom collections - beta)  
  	②先ほど作成したcollection_idの"images"の数が、trainImageに格納されている画像の数と一致すれば学習データを登録できました。  
6. 登録した学習データを使って類似画像検索を行う際は、下記のsatokotaproject/vrAppをご覧ください。  
	https://github.com/satokotaproject/vrApp/


## ファイル構成
	tApp
	 |-src/test/java
	 |-src/main/java
		|-AddToCollection.java  (collectionに学習データを追加します。)
		|-AddToCollectionThread.java	
		|-AddTraindata.java  (特定のサイトから、学習データを抽出した場合に、その学習データの数を増やします。)
		|-AddTraindataThread.java		
		|-App.java			
		|-Crawl.java  (クロールして、学習データを抽出する際に使用します。現在は、特定のサイトの場合のみに使用できます。)
		|-Detectfaces.java  (顔写真を学習データとして登録したい場合に、全身画像から顔写真の画像の学習データを作成します。)
		|-DetectfacesThread.java
	|-.gitignore
	|-pom.xml
	|-README.md


