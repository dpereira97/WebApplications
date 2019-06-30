<?php
require_once("ArticleRestHandler.php");
require_once("TweetsRestHandler.php");
require_once("PhotosRestHandler.php");
require_once("MetadataRestHandler.php");
		
$view = "";
if(isset($_GET["view"]))
	$view = $_GET["view"];

switch($view){

	case "topn":
		// to handle REST Url /article/
		$articleRestHandler = new ArticleRestHandler();
		$articleRestHandler->getAllArticles($_GET["disease"]);
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

	case "" :
		//404 - not found;
		break;
}
?>
