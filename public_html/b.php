<!DOCTYPE html>
<html>
    <head>
        <title>AW003</title>
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" />
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <script sync src="https://platform.twitter.com/widgets.js"></script>
        <script>
            function fazTudo(){
                    updatePhotos();
                    getMER();
                    getTweets();
                    getMetadata();
                    getRelatedDiseases();
            }
             function getDiseases() {
                str = document.getElementById("searchDisease").value;
                str2 = str.replace(/\s/g, '');
                if (str2.length == 0) { 
                    document.getElementById("latestPhotos").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/diseases/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            var myDiseases = JSON.parse(this.response);
                            var existe = false;
                            for (i in myDiseases) {
                                if(myDiseases[i]==str){
                                    existe = true;                       
                                }     
                            }
                            if(existe == false){
                                document.write("<p>Erro 404 - A doenca nao foi encontrada</p>");
                                
                            }
                            else{
                                fazTudo();
                            }
                        }
                    };
                    xmlhttp.send();
                }
            }
            
            
            
            function updatePhotos() {
                str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("latestPhotos").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/photos/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            var myArr = JSON.parse(this.response);
                            var elm = document.getElementById("latestPhotos");
                            elm.innerHTML = '';
                            for (var i = 0; i < 4; i++) {
                                myImg = '<img src="'+myArr[i]+'" alt="logo" style="height:100px" align="w3-center">  ';
                                elm.insertAdjacentHTML( 'beforeend', myImg );
                                //var btn = document.createElement("BUTTON");
                                //btn.innerHTML = str;
                                //btn.addEventListener("click", function(){document.write(str)});
                                //document.body.appendChild(btn);
                            }
                        }
                    };
                    xmlhttp.send();
                }
            }

            function getTweets() {
                str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("tweets").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/tweets/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            var myArr = JSON.parse(this.response);
                            for (var i = 0; i < 4; i++) {
                                var tweet = document.getElementById("tweets");
                                var id = myArr[i];

                                twttr.widgets.createTweet(
                                    id, tweet, 
                                    {
                                        conversation : 'none',    // or all
                                        cards        : 'hidden',  // or visible 
                                        linkColor    : '#cc0000', // default is blue
                                        theme        : 'light'    // or dark
                                    })
                                    .then (function (el) {
                                    el.contentDocument.querySelector(".footer").style.display = "none";
                                });
                            }
                        }
                    };
                    xmlhttp.send();
                }
            }
            
            function getMER(){
                str = document.getElementById("searchDisease").value;
               str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("links").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/articlesMER/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');
                    var myAbstracts;
                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            myMer = JSON.parse(this.response);                       
							getAbstracts(myMer);
                        }
                    };
                    
                    xmlhttp.send();
                    
                }
            }
            
            function getAbstracts(myMer){
                globalmyMer = myMer;
                str = document.getElementById("searchDisease").value;
               str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("links").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/articlesAbstracts/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');
                    var myAbstracts;
                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            myAbstracts = JSON.parse(this.response);                       
							getArticlesids(myAbstracts);
                        }
                    };
                    
                    xmlhttp.send();
                    
                }
            }
            
            function getArticlesids(myAbstracts){
               globalmyAbstracts = myAbstracts;
               str = document.getElementById("searchDisease").value;
               str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("links").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/articlesOrder/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            myIds = JSON.parse(this.response);
                            myIdsNew=[];
                            for(i in myIds){
                            	myIdsNew.push(myIds[i].split("\t")[0]);
                                }
                            getArticlesInfo(myIdsNew);
                            
							
                        }
                    };
                    xmlhttp.send();
                    
                }
            }
			
            var globalmyIds;
            var globalmyAbstracts;
            var globalmyMer;
            function getArticlesInfo(myIds) {
                globalmyIds = myIds;
                str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("links").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/articlesInfo/"+ str + "/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');
                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            myLinksTitles = JSON.parse(this.response);
                            var result = "";
                    		var docFrag = document.createDocumentFragment();
                            var docFrag2 = document.createDocumentFragment();
                            for(var id = 0; id < 5; id++){
                                var i = 0;
								for(link in myLinksTitles){
									var splitted = link.split("/");
                                    if(Object.keys(myLinksTitles)[i] != null){  
									   if(splitted[splitted.length-1] == globalmyIds[id]){
                                            myLink = '<a href =' + Object.keys(myLinksTitles)[i] + ">" + Object.values(myLinksTitles)[i] + "   </a>"
                                            result = result.concat(myLink,"<br>","<p class=a>"+ globalmyAbstracts[i]+ "</p>");
                                            var btnLike = document.createElement("BUTTON");
                                			btnLike.innerHTML = "Like:    " + globalmyIds[id];
                                			btnLike.addEventListener("click", function(){likeArticle(globalmyIds[id])});
                                			docFrag.appendChild(btnLike);
                                            var btnDislike = document.createElement("BUTTON");
                                			btnDislike.innerHTML = "Dislike: " +globalmyIds[id];
                                			btnDislike.addEventListener("click", function(){dislikeArticle(globalmyIds[id])});
                                			docFrag2.appendChild(btnDislike);
                                           var diseases = [];
                                           for(mer in globalmyMer){
                                               var splitted2 = globalmyMer[mer].split("\t");
                                               if(splitted2[0] == globalmyIds[id]){
                                                   diseases.push(splitted2[3]);
                                               }
                                           }
                                           diseases = diseases.filter(onlyUnique);
                                           for(d in diseases){
                                               result = result.concat('<a href = http://appserver.alunos.di.fc.ul.pt/~aw003/a.php?disease=' + diseases[d].replace(/\s/g, '') + ">" + diseases[d] + "   </a>"," · ");
                                           }
                               
                                           
									   }
                                    }
                                    i=i+1;                                   
								}
                                result = result.concat("<br>","<br>","<br>");
							}
                        }
                        document.getElementById("links").innerHTML = result;
                        
 					    document.getElementById('links').appendChild(docFrag);
                        document.getElementById('links').appendChild(docFrag2);
                        
                    };
                    xmlhttp.send(); 
                }
            }
            
            function likeArticle(id){
               /*str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
           		var xhttp = new XMLHttpRequest();
  				xhttp.onreadystatechange = function() {
    				if (this.readyState == 4 && this.status == 200) {
      					document.write(this.responseText);
                    }
                };
                xhttp.open("GET", "/~aw003/rest/articleFeedBack/"+ str + "/"+ id, true);
                xmlhttp.setRequestHeader('Accept', 'application/json');
                xhttp.send();*/
            }
            
            function dislikeArticle(id){
            //TODO
            }
            function onlyUnique(value, index, self) { 
                return self.indexOf(value) === index;
            }

            function getMetadata() {
                str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("metadatas").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/metadata/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            var myArr = JSON.parse(this.response);
                            var elm = document.getElementById("metadatas");
                            elm.innerHTML = '';
                            for (var i = 0; i < myArr.length; i++) {
                                myMeta = '<p> <a href = https://en.wikipedia.org/wiki/' + myArr[i].replace(/\s/g, '_') + ">" + myArr[i] + "   </a> </p>"
                                elm.insertAdjacentHTML( 'beforeend', myMeta );
                               
                                
                            }
                        }
                    };
                    xmlhttp.send();
                }
            }

            function getRelatedDiseases() {
                str = document.getElementById("searchDisease").value;
                str = str.replace(/\s/g, '');
                if (str.length == 0) { 
                    document.getElementById("relatedDiseases").innerHTML = "";
                } else {
                    var xmlhttp = new XMLHttpRequest(),
                        method = "GET",
                        url = "/~aw003/rest/relatedDiseases/"+ str+"/";

                    xmlhttp.open(method, url, true);
                    xmlhttp.setRequestHeader('Accept', 'application/json');

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState === XMLHttpRequest.DONE && xmlhttp.status === 200) {
                            var myArr = JSON.parse(this.response);
                            var elm = document.getElementById("relatedDiseases");
                            elm.innerHTML = '';
                            for (var i = 0; i < myArr.length; i++) {
                                myMeta = '<a href = http://appserver.alunos.di.fc.ul.pt/~aw003/a.php?disease=' + myArr[i].replace(/\s/g, '+') + ">" + myArr[i] + "   </a>"
                                elm.insertAdjacentHTML( 'beforeend', myMeta );
                            }
                        }
                    };
                    xmlhttp.send();
                }
            }


            $(document).ready(function(){
                $.ajaxSetup({ cache: false });
                $('#searchDisease').keyup(function(){
                    $('#result').html('');
                    $('#state').val('');
                    var searchField = $('#searchDisease').val();
                    var expression = new RegExp(searchField, "i");
                                                                $.getJSON('diseases.json', function(data) {
                                                                    $.each(data, function(key, value){
                                                                        if (value.disease.search(expression) != -1){
                                                                            $('#result').append('<li class="list-group-item link-class"> '+ value.disease +' <span class="text-muted">'+'</span></li>');
                                                                        }
                                                                    });   
                                                                });
                });

                $('#result').on('click', 'li', function() {
                    var click_text = $(this).text();
                    //click_text = click_text.replace(/\s/g, '');
                    $('#searchDisease').val($.trim(click_text));
                    $("#result").html('');
                });
            });
            function myFunction() {
              var x = document.getElementById("myDIV");
              if (x.style.display === "none") {
                x.style.display = "block";
              } else {
                x.style.display = "none";
              }
            }  
          
        </script>


        <style>

            
            
            
            #result {
                position: absolute;
                width: 100%;
                max-width:870px;
                cursor: pointer;
                overflow-y: auto;
                max-height: 400px;
                box-sizing: border-box;
                z-index: 1001;
            }
            .link-class:hover{
                background-color:#f1f1f1;
            }
            #tweets {
                width: 400px !important;
            }   

            #tweets iframe {
                border: none !important;
                box-shadow: none !important;
            }
            * {
                box-sizing: border-box;
            }

            /* Create two equal columns that floats next to each other */
            .column {
                float: left;
                width: 50%;
                padding: 10px;
                height: 300px; /* Should be removed. Only for demonstration */
            }
            .columnImage {
                float: left;
                width: 30%;
                padding: 10px;
                height: 300px; /* Should be removed. Only for demonstration */
            }
             .columntwitter {
                float: right;
                width: 20%;
                padding: 10px;
                height: 300px; /* Should be removed. Only for demonstration */
            }

            /* Clear floats after the columns */
            .row:after {
                content: "";
                display: table;
                clear: both;
                align-self: flex-end;
            }

            .espacamento{
                padding: 40px;
            }
            .padingImagem{
                padding: 5px;
                
            }
            .textoCentro{
                text-align: center;
            }
            
        
          
           
           
            
            .textoesquerda{
                text-align: left;
                padding: 20px;
                
            }
            .padingdrt{
                padding-right: 40px;
            }
            .borderround {
                border: 1px solid black;
                border-radius: 5px;
            }
            #myDIV {
                  width: 100%;
                  padding: 50px 0;
                
                  
                  margin-top: 20px;
                }
            .padtwitter{
                padding: 5px;
            }
            .botao{
            align-items: center;
            }
            .padding10{
                padding: 20px;
            }
            .button {
              background-color: #4CAF50; /* Green */
              border: none;
              color: white;
              padding: 15px 32px;
              text-align: center;
              text-decoration: none;
              display: inline-block;
              font-size: 16px;
              margin: 4px 2px;
              cursor: pointer;
            }
      .button2 {
                background-color: #008CBA;
            } /* Blue */
            .center {
            display: block;
            margin-left: auto;
            margin-right: auto;
           
            }

            

            

        </style>
    </head>


    <body onload="getDiseases()">
        
     
        
        
        
        <br><br>
        <!--imagem e pesquisa -->
        <div class="container" style="width:900px;">

            <img src="logo.png" alt="logo"  class="center" style="height:400px" align="w3-center">  


            <!--pesquisa -->
            <div >
                <form action = "a.php" method='get' autocomplete='off'>
                    <div >
                        <input type="text" name="disease" id="searchDisease" placeholder="Pesquise uma doença" class="form-control" value="<?php echo htmlspecialchars($_GET['disease']); ?>"/>
                    </div>
                    <!--autocomplete -->
                    <ul class="list-group" id="result"></ul>
                   <button class="center button button2" onclick="myFunction()">botao teste</button>
        
                </form>
            </div>
        </div>
        
        
        
        
        
        

      

        
        
       
        
      
        
        <div id="myDIV" style="display: none;">
             <button class="center button button2" onclick="myFunction()">botao teste</button>
        
             <br>
            
        <div align="center>" class="textoCentro">
            
            
                
                    <h4>Similiar diseases with the disease <?php echo htmlspecialchars($_GET['disease']); ?>:</h4>
                    <div id="relatedDiseases"></div>
                
            
        <br>
        
        <div class="row; espacamento">
            
                        
            
            
                <div class="column" >
                    <br>
                    <div class="borderround">
                        <h4>Abstract</h4>

                        <div class="textoesquerda" id =links></div>
                    </div>
                </div>

                <div class="columnImage" >
                    <br>
                    <div class="borderround">
                        <div align="center>" class="textoCentro">

                            <h4>People who died with the disease <?php echo htmlspecialchars($_GET['disease']); ?>:</h4>

                        </div> 
                        
                        
                        <br>

                        <div class="textoesquerda" id="metadatas"></div>
                        
                    </div>
                    
                    <br>

                    <br>

                    <div class="borderround">
                        
                         <h4>Photos about the disease <?php echo htmlspecialchars($_GET['disease']); ?>:</h4>
                        
                         <br>
                        
                        <?php echo '<p><span id="latestPhotos"></span></p>';?>
                        
                    </div>

                </div>

                <div class="columntwitter" >
                    <br>
                    
                        

                        <div class="padingdrt" id="tweets"></div>
                   

                </div>
            
            
           
        </div>

        </div>
</div>



    </body>
</html>