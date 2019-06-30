
<?php
require_once("ArticleRestHandler.php");
		
$view = "";
if(isset($_GET["view"]))
	$view = $_GET["view"];

switch($view){

	case "all":
		// to handle REST Url /article/
		$articleRestHandler = new ArticleRestHandler();
		$articleRestHandler->getAllArticles();
		break;
		
	case "single":
		// to handle REST Url /article/<id>/
		$articleRestHandler = new ArticleRestHandler();
		$articleRestHandler->getArticle($_GET["id"]);
		break;

	case "" :
		//404 - not found;
		break;
}
?>