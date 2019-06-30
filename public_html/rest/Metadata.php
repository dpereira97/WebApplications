<?php

Class Metadata {

    private $links;

	private $disease;
	
	private function readFiles(){
		$filename = "../Pulmonology/".$this->disease."/".$this->disease."MetaData.tsv";
		$handle = fopen($filename, "r");
		$contents = fread($handle, filesize($filename));
		$this->links = explode("\n",$contents);
		fclose($handle);
	}
			
	public function getAllMetadata($d){
		$this->disease = $d;
		$this->readFiles();
		return $this->links;
	}	
}
?>