<?php

Class RelatedDisease {

    private $relatedDiseases;

	private $disease;
	
	private function readFiles(){
		$filename = "../Pulmonology/".$this->disease."/".$this->disease."RelatedDiseases.tsv";
		$handle = fopen($filename, "r");
		$contents = fread($handle, filesize($filename));
		$this->relatedDiseases = explode("\n",$contents);
		fclose($handle);
	}
			
	public function getAllRelatedDisease($d){
		$this->disease = $d;
		$this->readFiles();
		return $this->relatedDiseases;
	}	
}
?>