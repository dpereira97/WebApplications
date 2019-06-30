xmllint --xpath '/PubmedArticleSet/PubmedArticle/MedlineCitation/Article/ArticleDate/Year/text()' /home/aw003/public_html/id.xml > /home/aw003/public_html/date.txt
echo -e "\r\n" >> $2