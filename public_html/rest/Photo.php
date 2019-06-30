<?php

Class Photo {

    private $links;

	private $disease;
	
	private function readFiles(){
		//$filename = "../Pulmonology/".$this->disease."/".$this->disease."AvgFeedback.tsv";
		$filename = "../Pulmonology/".$this->disease."/".$this->disease."Photos.tsv";
		$handle = fopen($filename, "r");
		$contents = fread($handle, filesize($filename));
		$this->links = explode("\n",$contents);
		fclose($handle);
	}
			
	public function getAllPhotos($d){
		$this->disease = $d;
		$this->readFiles();
		return $this->links;
	}	
}
?>