<?php
require_once("DiseaseRestHandler.php");
require_once("ArticleRestHandler.php");
require_once("TweetsRestHandler.php");
require_once("PhotosRestHandler.php");
require_once("MetadataRestHandler.php");
require_once("RelatedDiseaseRestHandler.php");


//identify the request method.
$requestType = $_SERVER['REQUEST_METHOD'];
 
switch ($requestType) {

      case 'POST':
      	 $articleRestHandler = new ArticleRestHandler();
	 	 $articleRestHandler->insertFeedBack($_POST["disease"],$_POST["id"]);
     	 break;

      case 'GET':
    	 $view = "";

	 if(isset($_GET["view"]))
		$view = $_GET["view"];
		switch($view){
            
            case "feedbackpos":
                 $articleRestHandler = new ArticleRestHandler();
	 	         $articleRestHandler->insertFeedBackPos($_GET["disease"],$_GET["id"]);
     	         break;
            case "feedbackneg":
                 $articleRestHandler = new ArticleRestHandler();
	 	         $articleRestHandler->insertFeedBackNeg($_GET["disease"],$_GET["id"]);
     	         break;
                
			case "names":
				$diseaseRestHandler = new DiseaseRestHandler();
				$diseaseRestHandler->getAllDiseases();
				break;

			case "info":
				$articleRestHandler = new ArticleRestHandler();
				$articleRestHandler->getAllArticlesInfo($_GET["disease"]);
				break;
                
            case "mer":
				$articleRestHandler = new ArticleRestHandler();
				$articleRestHandler->getAllArticlesMER($_GET["disease"]);
				break;
                
			case "topn":
				$articleRestHandler = new ArticleRestHandler();
				$articleRestHandler->getAllArticlesOrder($_GET["disease"]);
				break;
            				
			case "abs":
				$articleRestHandler = new ArticleRestHandler();
				$articleRestHandler->getAllArticlesAbstracts($_GET["disease"]);
				break;

			case "tweets":
				$tweetsRestHandler = new TweetsRestHandler();
				$tweetsRestHandler->getAllTweets($_GET["disease"]);
				break;

			case "photos":
				$photosRestHandler = new PhotosRestHandler();
				$photosRestHandler->getAllPhotos($_GET["disease"]);
				break;

			case "metadata":
				$photosRestHandler = new MetadataRestHandler();
				$photosRestHandler->getAllMetadata($_GET["disease"]);
				break;
                
            case "reldise":
				$relRestHandler = new RelatedDiseaseRestHandler();
				$relRestHandler->getAllRelatedDisease($_GET["disease"]);
				break;
                
			case "" :
				//404 - not found;
				break;	 
			}
}
?>
