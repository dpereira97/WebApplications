import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ScriptHandler {

	private static final String initialPath = "/home/aw003/repos/proj-aw003/";
	private static final String publicHtmlPath = "/home/aw003/public_html/";
	
	/**
	 * Method that gets IDs, links and titles for a given disease
	 * @param disease
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getDiseaseArticles(String disease, String field) throws IOException, InterruptedException {
		getDiseaseIds(disease, field);
		getDiseaseLinks(disease, field);
		getDiseaseTitles(disease, field);
		getDiseaseAbstracts(disease, field);
	}

	/////////////////////RELATORIO/////////////////////
	public static void getRelatorio(String disease, String field) throws IOException, InterruptedException {
		getNumberTweets(disease, field);
		getNumberLinks(disease, field);
		getNumberPhotos(disease, field);
		getNumberDisease();
		getNumberMetadata(disease, field);
	}

	/**
	 * 
	 * @param disease
	 * @param field
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getNumberTweets(String disease, String field) throws IOException, InterruptedException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/ntweets.sh" + " " + field + " " + disease.replaceAll("\\s+", "").replaceAll("/", "") + " >> "  + publicHtmlPath + "Relatorio.txt");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}

	/**
	 * Escreve o numero de links sobre a doenca no relatorio
	 * @param disease
	 * @param field
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getNumberLinks(String disease, String field) throws IOException, InterruptedException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/nLinks.sh" + " " + field + " " + disease.replaceAll("\\s+", "").replaceAll("/", "") + " >> " + publicHtmlPath + "Relatorio.txt");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}


	/**
	 * Escreve o numero de fotos sobre a doenca no relatorio
	 * @param disease
	 * @param field
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getNumberPhotos(String disease, String field) throws IOException, InterruptedException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/nPhotos.sh" + " " + field + " " + disease.replaceAll("\\s+", "").replaceAll("/", "") + " >> "  + publicHtmlPath + "Relatorio.txt");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getNumberDisease() throws IOException, InterruptedException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/nDiseases.sh >> " + publicHtmlPath + "Relatorio.txt");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}
	
	/**
	 * 
	 * @param disease
	 * @param field
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getNumberMetadata(String disease, String field) throws IOException, InterruptedException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/nMetaData.sh" + " " + field + " " + disease.replaceAll("\\s+", "").replaceAll("/", "") + " >> "  + publicHtmlPath + "Relatorio.txt");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}
	/////////////////////RELATORIO/////////////////////

	
	/////////////////////TWEETS/////////////////////
	
	/**
	 * Vai buscar os tweets mais recentes da disease e escreve num tsv o tweet, a data e o link
	 * @param disease
	 * @param field
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getDiseaseTweets(String disease, String field) throws IOException, InterruptedException {

		String path = publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "");
		String url = "https://twitter.com/statuses/";
		
		JSONParser parser = new JSONParser();
		File f = new File(path + "Tweets.json");

		if(Files.exists(Paths.get(path + "Tweets.tsv"))) {
			updateTweets(disease, path);
		} 
		else {  // ficheiro JSON com tweets nao existe
			try {
				tweetsToJson(disease,path + "Tweets.json"); // cria ficheiro JSON
				Object obj = parser.parse(new FileReader(f));
				JSONObject jsonObject = (JSONObject) obj; 
				JSONArray array = (JSONArray) jsonObject.get("statuses");
				Iterator<?> iterator = array.iterator();
				File f2 = new File(path +"Tweets.tsv"); //cria ficheiro output com Tweets
				BufferedWriter writer = new BufferedWriter(new FileWriter(f2));		

				while(iterator.hasNext()) {

					JSONObject obj2 = (JSONObject) iterator.next();
					String data = (String) obj2.get("created_at");
					String text = (String) obj2.get("full_text");
					String idTweet = (String) obj2.get("id_str"); //https://twitter.com/statuses/552767187694661632
					//String urlTweet = url + idTweet;
					//writer.write(convertTwitterDate(data) + "\t");
					//writer.write("\"" + text.replace("\n", "") + "\" ");
					//writer.write("\t" + urlTweet + "\n");
					writer.write(idTweet + "\n");
				}
				writer.close();
				
				JSONObject metadados = (JSONObject) jsonObject.get("search_metadata");
				
				File nexts = new File(path + "NextTweetsUpdate.tsv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(nexts));
				
			
				String nextResults = (String) metadados.get("next_results");
				if(nextResults != null) {
					String id = nextResults.substring(nextResults.lastIndexOf("?max_id") + 8,nextResults.lastIndexOf("&q"));
					String doenca = nextResults.substring(nextResults.lastIndexOf("&q=") + 3,nextResults.lastIndexOf("&lang"));
					bw.write(id+":"+doenca);  // diseaseNextTweetsUpdate.tsv fica com o id e doenca separados por dois pontos
					bw.close(); 
				}
				f.delete();

			} catch (FileNotFoundException e) { e.printStackTrace();}
			catch (IOException e) { e.printStackTrace();}
			catch (Exception e) { e.printStackTrace();}
		}
	}
	
	/**
	 * Corre o script que vai buscar os tweets e guarda num ficheiro json
	 */
	private static void tweetsToJson(String disease, String path) throws IOException, InterruptedException {
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getTweets.sh " + disease + " > " + path);
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}
	
	/**
	 * Vai buscar os tweets mais recentes, tendo em conta o ultimo mais recente, e faz update aos novos tweets
	 */
	private static void updateTweets(String disease, String path) throws IOException, InterruptedException {
		
		String url = "https://twitter.com/statuses/";
		File nextTweetsUpdate = new File(path+"NextTweetsUpdate.tsv");
		BufferedReader br = new BufferedReader(new FileReader(nextTweetsUpdate));
		//String comando = br.readLine();
		String[] cmd = br.readLine().split(":");
		br.close();
		
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getTweetsUpdates.sh " + cmd[0] + " " + cmd[1] + " > " + path + "NewTweets.json"); //diseaseNewTweets.json
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
		
		File tweets = new File(path +"Tweets.tsv");  //ficheiro com tweets
		File newTweets = new File(path + "NewTweets.json"); //novos tweets
		
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(newTweets));
			JSONObject jsonObject = (JSONObject) obj; 
			JSONArray array = (JSONArray) jsonObject.get("statuses");
			Iterator<?> iterator = array.iterator();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(tweets,true)); //vai acrescentar ao ficheiro dos tweets
			
			while(iterator.hasNext()) {
				JSONObject obj2 = (JSONObject) iterator.next();
				String data = (String) obj2.get("created_at");
				String text = (String) obj2.get("text");
				String idTweet = (String) obj2.get("id_str"); //https://twitter.com/statuses/552767187694661632
				String urlTweet = url + idTweet;
				
				writer.write(convertTwitterDate(data) + "\t");
				writer.write("\"" + text.replace("\n", "") + "\" ");
				writer.write("\"" + urlTweet + "\" " + "\n");
			}
			writer.close();
			
			JSONObject metadados = (JSONObject) jsonObject.get("search_metadata");
			
			File nexts = new File(path + "NextTweetsUpdate.tsv");
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(nexts));
			
		
			String nextResults = (String) metadados.get("next_results");
			String id = nextResults.substring(nextResults.lastIndexOf("?max_id") + 8,nextResults.lastIndexOf("&q") - 1);
			String doenca = nextResults.substring(nextResults.lastIndexOf("&q=") + 1,nextResults.lastIndexOf("&lang"));
			bw2.write(id+":"+doenca);  
			bw2.close(); 
			
			newTweets.delete();
			
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converte o formato da data que recebemos do twitter para um formato mais legivel
	 */
	private static String convertTwitterDate(String old_date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		SimpleDateFormat old = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy",Locale.ENGLISH);
		old.setLenient(true);

		Date date = null;
		try {
			date = old.parse(old_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return sdf.format(date);    
	}
	
	//////////////////////////////////////

	/**
	 * Executes the getPubMedIds bash script
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private static void getDiseaseIds(String disease, String field) throws IOException, InterruptedException {
		File f = new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		//Se jah existir, update
		if(f.exists()){
			updateIds(disease, field);
			return;
		}
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getPubMedIds.sh " + disease + " > " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "")+".tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();

		//System.out.println("Comando do script dos IDs: " + build.command().toString());
	}

	private static void updateIds(String disease, String field) throws IOException, InterruptedException{
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getPubMedIds.sh " + disease + " > " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "Temp.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
		File newUniqueIds = new File (publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "New.tsv");
		File oldIds = new File (publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		File tempIds = new File (publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Temp.tsv");
		FileWriter fw = new FileWriter(newUniqueIds);
		FileWriter addIds = new FileWriter(oldIds, true);
		BufferedReader b_old = new BufferedReader(new FileReader(oldIds));
		BufferedReader b_temp = new BufferedReader(new FileReader(tempIds));
		String lineTemp;
		String lineOld;
		// para cada um dos ids obtidos
		while((lineTemp = b_temp.readLine()) != null){
			boolean found = false;
			// para cada um dos antigos
			while((lineOld = b_old.readLine()) != null){
				if(lineTemp.equals(lineOld)) {
					found = true;
					break;
				}
			}
			b_old = new BufferedReader(new FileReader(oldIds));
			if(!found) {
				fw.write(lineTemp);
				addIds.write(lineTemp);
				addIds.write(System.getProperty("line.separator"));
			}
		}
		if (newUniqueIds.length() == 0) {
			newUniqueIds.delete();
		}
		b_temp.close();
		tempIds.delete();
		fw.close();
		addIds.close();
		b_old.close();

	}

	/**
	 * Executes the convertPubMedIds bash script
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private static void getDiseaseLinks(String disease, String field) throws IOException, InterruptedException {
		
		File f = new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		if(!f.exists())
			return;

		File temp = new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Links.tsv");
		if(temp.exists()){
			updateLinks(disease, field);
			return;
		}
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/convertPubMedIds.sh " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") +" > " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Links.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();

		//System.out.println("Comando do script dos Links: " + build.command().toString());
	}

	private static void updateLinks(String disease, String field) throws IOException, InterruptedException {
		File newIds = new File (publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "New.tsv");
		if(!newIds.exists())
			return;
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/convertPubMedIds.sh " + disease.replaceAll("\\s+", "").replaceAll("/", "") + "New" + " >> " + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Links.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
	}

	/**
	 * Executes the getPubMedTitles bash script
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private static void getDiseaseTitles(String disease, String field) throws IOException, InterruptedException {
		File f = new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		if(!f.exists())
			return;

		File temp = new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Titles.tsv");
		if(temp.exists()){
			updateTitles(disease,field);
			return;
		}
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getPubMedTitles.sh " + "$(tr '\n' ',' < " + 
			publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv)" + " > " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Titles.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
		
	    FileReader fr = new FileReader(f);
	    BufferedReader ids = new BufferedReader(fr);
	    FileReader fr2 = new FileReader(new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Titles.tsv"));
	    BufferedReader titles = new BufferedReader(fr2);
	    String line;
	    StringBuilder sb = new StringBuilder();
	    while ((line = titles.readLine()) != null) {
	    	sb.append(ids.readLine());
	    	sb.append("\t" + line);
	    	sb.append("\n");
	    }
	    FileWriter fw = new FileWriter(new File(publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Titles.tsv"));
		fw.write(sb.toString());
		ids.close();
		titles.close();
		fw.close();
	    //System.out.println("Comando do script dos titulos: " + build.command().toString());
	}

	private static void updateTitles(String disease, String field) throws IOException, InterruptedException {
		File newIds = new File (publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "New.tsv");
		if(!newIds.exists())
			return;
		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getPubMedTitles.sh " + "$(tr '\n' ',' < " +
				field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "New" + " >> " + publicHtmlPath + field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Titles.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();

	}


	//////////////////////////////////////
	/**
	 * Executes the getFlickrPhotos bash script
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void getFlickrPhotos(String disease, String field) throws IOException, InterruptedException {
		File f = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		if(!f.exists())
			return;

		File temp = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") +"Photos.tsv");
		if(temp.exists()) {
			getFlickrPhotosUpdate(disease,field);
			return;
		}

		File file_temp = new File(publicHtmlPath+field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + "PhotoTime.tsv");
		FileWriter fw_temp = new FileWriter(file_temp);

		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getFlickrPhotos.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + " > " + publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") +"Photos.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();
		File file = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Photos.tsv");   
		BufferedReader br = new BufferedReader(new FileReader(file));  
		FileWriter fw = new FileWriter(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") 
				+ "PhotosInfo.tsv");
		String line; 
		int max = 0;
		int count = 0;
		while ((line = br.readLine()) != null) {
			count ++;
			String [] array1 = line.split("\\.");
			String [] array2 = array1[2].split("\\/");
			String [] array3 = array2[2].split("\\_");
			List<String> list2 = new ArrayList<>();
			list2.add("sh");
			list2.add("-c");
			list2.add(initialPath + "scripts/getPhotoLabel.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
					array3[0] + " " + array3[1]);
			ProcessBuilder build2 = new ProcessBuilder(list2);
			Process p2 = build2.start();
			p2.waitFor();
			String label = IOUtils.toString(p2.getInputStream(),StandardCharsets.UTF_8);

			List<String> list3 = new ArrayList<>();
			list3.add("sh");
			list3.add("-c");
			list3.add(initialPath + "scripts/getPhotoDate.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
					array3[0] + " " + array3[1]);
			ProcessBuilder build3 = new ProcessBuilder(list3);
			Process p3 = build3.start();
			p3.waitFor();
			String date = IOUtils.toString(p3.getInputStream(),StandardCharsets.UTF_8);
			fw.write(array3[0] + "\t" + label + "\t" + date + "\n");

			if(Integer.parseInt(date.split("\"")[1])>max)
				max = Integer.parseInt(date.split("\"")[1]);
		}
		fw_temp.write(max + "\n" + count);
		br.close();
		fw.close();
		fw_temp.close();
		//"Comando do script das fotos do flicker: " + build.command().toString());
	}

	private static void getFlickrPhotosUpdate(String disease, String field) throws IOException, InterruptedException {

		FileReader fr = new FileReader(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "PhotoTime.tsv");
		BufferedReader br_temp = new BufferedReader(fr);
		int max = Integer.parseInt(br_temp.readLine());
		int nlinhas = Integer.parseInt(br_temp.readLine());
		br_temp.close();

		List<String> list = new ArrayList<>();
		list.add("sh");
		list.add("-c");
		list.add(initialPath + "scripts/getFlickrPhotosUpdate.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + " " + max + " >> " + publicHtmlPath +"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") +"Photos.tsv");
		ProcessBuilder build = new ProcessBuilder(list);
		Process p = build.start();
		p.waitFor();

		File file = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Photos.tsv");   
		BufferedReader br = new BufferedReader(new FileReader(file));

		int nlinhas2 = 0;


		while (br.readLine() != null) {
			nlinhas2++;
		}
		br.close();
		if(nlinhas2 - nlinhas == 0)
			return;

		File file_temp = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "PhotoTime.tsv");
		FileWriter fw_temp = new FileWriter(file_temp, false);

		FileWriter fw = new FileWriter(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") 
				+ "PhotosInfo.tsv",true);
		br = new BufferedReader(new FileReader(file));
		while (nlinhas>0) {
			br.readLine();
			nlinhas--;
		}
		String line;
		int maxNovo = max;
		int countNovo = nlinhas2;
		while ((line = br.readLine()) != null) {
			String [] array1 = line.split("\\.");
			String [] array2 = array1[2].split("\\/");
			String [] array3 = array2[2].split("\\_");
			List<String> list2 = new ArrayList<>();
			list2.add("sh");
			list2.add("-c");
			list2.add(initialPath + "scripts/getPhotoLabel.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
					array3[0] + " " + array3[1]);
			ProcessBuilder build2 = new ProcessBuilder(list2);
			Process p2 = build2.start();
			p2.waitFor();
			String label = IOUtils.toString(p2.getInputStream(),StandardCharsets.UTF_8);

			List<String> list3 = new ArrayList<>();
			list3.add("sh");
			list3.add("-c");
			list3.add(initialPath + "scripts/getPhotoDate.sh " + "4dd01f7247889460dac2abb7ba8dd8c3 " + 
					array3[0] + " " + array3[1]);
			ProcessBuilder build3 = new ProcessBuilder(list3);
			Process p3 = build3.start();
			p3.waitFor();
			String date = IOUtils.toString(p3.getInputStream(),StandardCharsets.UTF_8);
			fw.write(array3[0] + "\t" + label + "\t" + date + "\n");
			if(Integer.parseInt(date.split("\"")[1])>max)
				maxNovo = Integer.parseInt(date.split("\"")[1]);
		}
		fw_temp.write(maxNovo + "\n" + countNovo);
		fw.close();
		fw_temp.close();
		//System.out.println("Comando do script das fotos do flicker: " + build.command().toString());
	}

	/**
	 * Executes the getAbstracts bash script
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private static void getDiseaseAbstracts(String disease, String field) throws IOException, InterruptedException {
		File f = new File(publicHtmlPath +field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +".tsv");
		if(!f.exists())
			return;

		File f2 = new File(publicHtmlPath+field + "/"+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") +"Abstracts.tsv");
		if(f2.exists()) {
			System.out.println("abstracts ja existe");
			updateAbstracts(disease,field);
			return;
		}
		FileReader fr = new FileReader(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv");

		try (BufferedReader br = new BufferedReader(fr)) {
			String id;
			
			while ((id = br.readLine()) != null) {

				List<String> list = new ArrayList<>();
				list.add("sh");
				list.add("-c");
				list.add(initialPath + "scripts/getAbstracts.sh " + id + 
						" >> " + publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Abstracts.tsv");
				ProcessBuilder build = new ProcessBuilder(list);
				Process p = build.start();
				p.waitFor();
				FileWriter fw2 = new FileWriter(publicHtmlPath + field + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "")+"Abstracts.tsv",true);
				BufferedWriter bf = new BufferedWriter(fw2);
				bf.write(System.getProperty("line.separator"));
				bf.close();
				//System.out.println("Comando do script do abstract: " + build.command().toString());	
			}

			br.close();
		}
		

	}

	private static void updateAbstracts(String disease, String field) throws IOException, InterruptedException {
		File newIds = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "New.tsv");
		if(!newIds.exists())
			return;
		FileReader fr = new FileReader(newIds);
		try (BufferedReader br = new BufferedReader(fr)) {
			String id;
			while ((id = br.readLine()) != null) {
				List<String> list = new ArrayList<>();
				list.add("sh");
				list.add("-c");
				list.add(initialPath + "scripts/getAbstracts.sh " + id + 
						" >> " + publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Abstracts.tsv");
				ProcessBuilder build = new ProcessBuilder(list);
				Process p = build.start();
				p.waitFor();
				
				FileWriter fw2 = new FileWriter(publicHtmlPath + field + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" +disease.replaceAll("\\s+", "").replaceAll("/", "")+"Abstracts.tsv",true);
				BufferedWriter bf = new BufferedWriter(fw2);
				bf.write(System.getProperty("line.separator"));
				bf.close();
				//"Comando do script do abstract: " + build.command().toString());
			}

			br.close();
		}


		newIds.delete();
	}
	
	public static void mergeTitlesAndAbstracts(String disease, String field) throws IOException {
		
		
		File titles = new File(publicHtmlPath + field +"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Titles.tsv");
		File abstracts = new File(publicHtmlPath + field +"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "Abstracts.tsv");		
		File f = new File(publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "TitlesAndAbstracts.tsv"); 
		
		FileWriter fw = new FileWriter(f);
		
		
		BufferedReader frTitles = new BufferedReader(new FileReader(titles));
		BufferedReader frAbstracts = new BufferedReader(new FileReader(abstracts));
		
		BufferedWriter fileWriter = new BufferedWriter(fw);
		
		String line = null;
		
		while((line = frTitles.readLine()) != null) {
			fileWriter.write(line + "\t" + frAbstracts.readLine());
			fileWriter.write(System.getProperty("line.separator"));
		}
		
		frAbstracts.close();
		frTitles.close();
		fileWriter.close();	
		
	}
	
	@SuppressWarnings("unchecked")
	public static void getMetaData(String str, String field) throws IOException, InterruptedException{
		String diseaseUpperFirstChar = str.substring(0, 1).toUpperCase() + str.substring(1);
		ParameterizedSparqlString query = new ParameterizedSparqlString(
				"prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n" +
						"prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
						"prefix dbo:    <http://dbpedia.org/ontology/> \n" +
						"prefix dbp:    <http://dbpedia.org/property/> \n" +
						"prefix dbr:    <http://dbpedia.org/resource/> \n" +
						"prefix foaf: <http://xmlns.com/foaf/0.1/> \n" +

				"SELECT ?diseasename ?artistname where {\n" +
				"?disease a dbo:Disease ." +
				"?person dbo:deathCause ?disease .\n" +
				"?person rdfs:label ?artistname FILTER (lang(?artistname) = \"en\").\n" +
				"?disease rdfs:label ?diseasename FILTER (lang(?diseasename) = \"en\").\n" +
				"?disease foaf:name "+"\""+diseaseUpperFirstChar+"\""+"@en" +"\n" +
				"}\n" +
				"LIMIT " + 10);

		QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query.asQuery());

		ResultSet results = exec.execSelect();

		FileWriter fw = new FileWriter(publicHtmlPath + field + "/" +str.replaceAll("\\s+", "").replaceAll("/", "") + "/" +str.replaceAll("\\s+", "").replaceAll("/", "")+"MetaData.tsv");
		//JSONArray ja = new JSONArray();
		//JSONObject jo = new JSONObject();
		
		while(results.hasNext()) {
			QuerySolution qs = results.nextSolution();
			//ja.add((String) qs.getLiteral("artistname").getValue());
			fw.write(qs.getLiteral("artistname").getValue() + "\n");
		}
		
		//jo.put("People", ja);
		//fw.write(jo.toString());
		fw.close();
	}	
}