import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class SparqlTests {

	private static final String publicHtmlPath = "/home/aw003/public_html/";

	public static void main(String[] args) throws InterruptedException, IOException {

		Scanner sc = new Scanner(System.in);
		System.out.println("Insert the field of the diseases: ");
		String field = sc.nextLine();

		System.out.println("Insert the number of diseases: ");
		int limitSparql = sc.nextInt();

		Files.createDirectories(Paths.get(publicHtmlPath+field)); // cria pasta para o ramo

		sc.close();
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
		ParameterizedSparqlString fstQuery = new ParameterizedSparqlString( "" +
				"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n" +
				"prefix dbo:    <http://dbpedia.org/ontology/> \n" +
				"prefix dbp:    <http://dbpedia.org/property/> \n" +
				"prefix dbr:    <http://dbpedia.org/resource/> \n" +
				"prefix foaf: <http://xmlns.com/foaf/0.1/> \n" +

				"SELECT ?uri ?name where {\n" +
				"?uri a dbo:Disease . \n" +
				"?uri dbp:field dbr:"+field+ ". \n" +
				"?uri foaf:name ?name\n" +
				"}\n" +
				"LIMIT " + limitSparql);

		QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", fstQuery.asQuery());

		ResultSet results = exec.execSelect();

		ArrayList<String> listDiseases = new ArrayList<>();
		FileWriter writer = new FileWriter(publicHtmlPath + "diseases.json");
		FileWriter writerTSV = new FileWriter(publicHtmlPath + "diseases.tsv"); 
		while(results.hasNext()) {
			QuerySolution qs = results.nextSolution();
			listDiseases.add((String) qs.getLiteral("name").getValue());

		}

		File f = new File(publicHtmlPath + "Relatorio.txt");
		if(f.exists())		
			f.delete();
		writer.write("[");
		writer.write("\n");
		int size = listDiseases.size(), i = 0;
		for (String str : listDiseases) {
			String diseaseLower = str.toLowerCase();
			i++;
			if(i == size) {
				writer.write("{\"disease\":" + "\"" + diseaseLower + "\"" + "}");
				writer.write("\n");
			} else {
				writer.write("{\"disease\":" +"\"" + diseaseLower + "\"" + "},");
				writer.write("\n");
			}
			writerTSV.write(diseaseLower);
			writerTSV.write("\n");
			Files.createDirectories(Paths.get(publicHtmlPath+field+"/"+diseaseLower.replaceAll("\\s+", "").replaceAll("/", "")));
			ScriptHandler.getDiseaseArticles(diseaseLower, field);
			ScriptHandler.mergeTitlesAndAbstracts(diseaseLower, field);
			ScriptHandler.getFlickrPhotos(diseaseLower, field);
			ScriptHandler.getDiseaseTweets(diseaseLower, field);
			ScriptHandler.getMetaData(diseaseLower, field);
			ScriptHandler.getRelatorio(diseaseLower, field);
			AnnotationHandler.getArticlesMER(diseaseLower, field);
			AnnotationHandler.getArticlesNERPubTator(diseaseLower, field);
			AnnotationHandler.makeInvertedIndex(diseaseLower, field);
			AnnotationHandler.dishln(diseaseLower, field);
			AnnotationHandler.getArticlesImplicitFeedback(diseaseLower, field);
			AnnotationHandler.getArticlesExplicitFeedback(diseaseLower, field);		
			AnnotationHandler.getArticlesAveragedFeedback(diseaseLower, field);
			AnnotationHandler.getDateArticles(diseaseLower, field);
			AnnotationHandler.weightedAverage(diseaseLower, field);
		}
		writer.write("]");
		writer.close();
		writerTSV.close();

		for (String str : listDiseases) {
			String diseaseLower = str.toLowerCase();
			WebService.relatedDiseases(diseaseLower, field);
		}
	}
}