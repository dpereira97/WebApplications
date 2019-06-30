xmllint --xpath '/PubmedArticleSet/PubmedArticle/MedlineCitation/Article/ArticleDate/Month/text()' $1 >> $2
echo -e "\r\n" >> $2