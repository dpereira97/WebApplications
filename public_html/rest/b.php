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
                getArticlesids();
                getTweets();
                getMetadata();
                getRelatedDiseases();
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
                            }
                        }
                    };
                    xmlhttp.send();
                }
                setTimeout(updatePhotos,3000);
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
            function getArticlesids(){
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
                            getArticlesInfo(myIds);
							
                        }
                    };
                    xmlhttp.send();
                    
                }
            }
			
            var globalmyIds;
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
							
                            for(id in globalmyIds){
                                var i = 0;
								for(link in myLinksTitles){
									var splitted = link.split("/");
                                    if(Object.keys(myLinksTitles)[i] != null){  
									   if(splitted[splitted.length-1] == globalmyIds[id]){
                                            myLink = '<a href =' + Object.keys(myLinksTitles)[i] + ">" + Object.values(myLinksTitles)[i] + "   </a>"
                                            result = result.concat(myLink,"<br><br>");
									   }
                                    }
                                    i=i+1;
								}
							}
                        }
                        document.getElementById("links").innerHTML = result;
                    };
                    xmlhttp.send(); 
                }
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
                                myMeta = '<p>' + myArr[i] + "</p>"
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
                width: 40%;
                padding: 10px;
                height: 300px; /* Should be removed. Only for demonstration */
            }
            .columnImage {
                float: left;
                width: 20%;
                padding: 10px;
                height: 300px; /* Should be removed. Only for demonstration */
            }
             .columntwitter {
                float: right;
                width: 40%;
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
            .center {
            display: block;
            margin-left: auto;
            margin-right: auto;
           
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

        </style>
    </head>


    <body onload="fazTudo()">
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
                </form>
            </div>
        </div>
        
        <button onclick="myFunction()">Try it</button>


        <br>
        
        <div id="myDIV">
        
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
                    
                        <h4>Tweets about the disease</h4>

                        <br>

                        <div class="padingdrt" id="tweets"></div>
                   

                </div>
            
            
           
        </div>

        </div>
</div>



    </body>
</html>