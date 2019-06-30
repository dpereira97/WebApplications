<?php
require_once("SimpleRest.php");
require_once("Disease.php");

class DiseaseRestHandler extends SimpleRest {

	function getAllDiseases() {	

		$disease = new Disease();
		$rawData = $disease->getAllnames();

		if(empty($rawData)) {
			$statusCode = 404;
			$rawData = array('error' => 'No diseases names found!');		
		} else {
			$statusCode = 200;
		}

		$requestContentType = $_SERVER['HTTP_ACCEPT'];
		$this ->setHttpHeaders($requestContentType, $statusCode);
				
		if(strpos($requestContentType,'application/json') !== false){
			$response = $this->encodeJson($rawData);
			echo $response;
		} else if(strpos($requestContentType,'text/html') !== false){
			$response = $this->encodeHtml($rawData);
			echo $response;
		} else if(strpos($requestContentType,'application/xml') !== false){
			$response = $this->encodeXml($rawData);
			echo $response;
		}
	}
	
	public function encodeHtml($responseData) {
	
		$htmlResponse = "<table border='1'>";
		foreach($responseData as $key=>$value) {
		        if (!empty($value)) {
    			   $htmlResponse .= "<tr><td>". $key. "</td><td>". $value. "</td></tr>";
			}
		}
		$htmlResponse .= "</table>";
		return "<html>".$htmlResponse."</html>";		
	}
	
	public function encodeJson($responseData) {
		$jsonResponse = json_encode($responseData);
		return $jsonResponse;		
	}
	
	public function encodeXml($responseData) {
		// creating object of SimpleXMLElement
		$xml = new SimpleXMLElement('<?xml version="1.0"?><article></article>');
		foreach($responseData as $key=>$value) {
			$xml->addChild($key, $value);
		}
		return $xml->asXML();
	}
}
?>