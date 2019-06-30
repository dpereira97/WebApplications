<?php

Class Disease {

    private $names;
	
	private function readFiles(){
		$filename = "../diseases.tsv";
		$handle = fopen($filename, "r");
		$contents = fread($handle, filesize($filename));
		$this->names = explode("\n",$contents);
		fclose($handle);
	}
			
	public function getAllnames(){
		$this->readFiles();
		return $this->names;
	}	
}
?>