<?php
/* 
A domain Class to demonstrate RESTful web services
*/
Class Article {

    private $titles;
    private $links;
    private $links_titles;
    private $disease;
    private $mer;
    private $idsordered;
    private $abstracts;
    private $oldorder;

    private function readFiles(){

        $filename = "../Pulmonology/".$this->disease."/".$this->disease."Links.tsv";
        $handle = fopen($filename, "r");
        $contents = fread($handle, filesize($filename));
        $this->links = explode("\n",$contents);
        fclose($handle);

        $filename = "../Pulmonology/".$this->disease."/".$this->disease."Titles.tsv";
        $handle = fopen($filename, "r");
        $contents = fread($handle, filesize($filename));
        $this->titles = explode("\n",$contents);
        fclose($handle);

        $filename = "../Pulmonology/".$this->disease."/".$this->disease."ArticlesMER.tsv";
        $handle = fopen($filename, "r");
        $contents = fread($handle, filesize($filename));
        $this->mer = explode("\n",$contents);
        fclose($handle);

        $filename = "../Pulmonology/".$this->disease."/".$this->disease."Abstracts.tsv";
        $handle = fopen($filename, "r");
        $contents = fread($handle, filesize($filename));
        $this->abstracts = explode("\n",$contents);
        fclose($handle);

        $filename = "../Pulmonology/".$this->disease."/".$this->disease."Output.tsv";
        $handle = fopen($filename, "r");
        $contents = fread($handle, filesize($filename));
        $this->idsordered = explode("\n",$contents);
        fclose($handle);

        $this->links_titles =array_combine($this->links,$this->titles);
    }
    private function fazFeedBackPos(){
        chmod("../Pulmonology/", 777);
        $myFile = "../Pulmonology/".$this->disease."/".$this->disease."Output.tsv";
        $handle = fopen($myFile, "r");
        $contents = fread($handle, filesize($myFile));
        $this->oldorder = explode("\n",$contents);
        fclose($handle);
        $this->oldorder = array_values($this->oldorder);

        $handle2 = fopen($myFile, "w");
        fwrite($handle2,$this->id . "\t" . "1.00" ."\r" . "\n");
        foreach($this->oldorder as $value){
            $value = split("\t", $value);
            if($value[0] != $this->id){ 
                fwrite($handle2,$value[0] . "\t" . $value[1] . "\n");
            }         
        }
        fclose($handle);
        $lines = file($myFile);
        $last = sizeOf($lines) - 1;
        unset($lines[$last]);
        
        $fp = fopen($myFile, "w");
        fwrite($fp,implode("",$lines));
        fclose($fp);
    }

    private function fazFeedBackNeg(){
        chmod("../Pulmonology/", 777);
        $myFile = "../Pulmonology/".$this->disease."/".$this->disease."Output.tsv";
        $handle = fopen($myFile, "r");
        $contents = fread($handle, filesize($myFile));
        $this->oldorder = explode("\n",$contents);
        fclose($handle);
        $this->oldorder = array_values($this->oldorder);

        $handle2 = fopen($myFile, "w");
        
        foreach($this->oldorder as $value){
            $value = split("\t", $value);
            if($value[0] != $this->id){ 
                fwrite($handle2,$value[0] . "\t" . $value[1] . "\n");
            }         
        }
        $lines = file($myFile);
        $last = sizeOf($lines) - 1;
        unset($lines[$last]);
        
        $fp = fopen($myFile, "w");
        fwrite($fp,implode("",$lines));
        fclose($fp);
        
  
        fwrite($handle2,$this->id . "\t" . "0.00" ."\r" . "\n");
        fclose($handle2);
        
    }


    public function getAllArticle($d){
        $this->disease = $d;
        $this->readFiles();
        return $this->links_titles;
    }
    public function getArticlesOrdered($d){
        $this->disease = $d;
        $this->readFiles();
        return $this->idsordered;
    }   
    public function getArticlesAbstracts($d){
        $this->disease = $d;
        $this->readFiles();
        return $this->abstracts;
    }
    public function getArticlesMER($d){
        $this->disease = $d;
        $this->readFiles();
        return $this->mer;
    }

    public function insertFeedBackPos($d,$id){
        $this->disease = $d;
        $this->id = $id;
        $this->fazFeedBackPos();
        return $this->$id;
    }

    public function insertFeedBackNeg($d,$id){
        $this->disease = $d;
        $this->id = $id;
        $this->fazFeedBackNeg();
        return $this->$id;
    }





    public function getArticle($d, $id){
        $this->disease = $d;
        $this->readFiles();
        return array($id => $this->titles[$id]);
    }	
}
?>
