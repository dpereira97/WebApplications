import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;


public class AnnotationHandler {

	private static final String initialPath = "/home/aw003/repos/proj-aw003/";
	private static final String publicHtmlPath = "/home/aw003/public_html/";

	public static void getArticlesMER(String disease, String field) throws IOException, InterruptedException {
		File fid = new File(publicHtmlPath + field + "/" + disease.replaceAll("\\s+", "").replaceAll("/", "")
				+ "/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + "Doid.tsv");
		FileWriter frid = new FileWriter(fid);
		//preparacao
		String url = "http://labs.fc.ul.pt/mer/api.php";
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		String lexicon = "doid";
		String text = disease;

		String query = String.format("lexicon=%s&text=%s", URLEncoder.encode(lexicon, charset),
				URLEncoder.encode(text, charset));

		//obter id da doenca
		URLConnection connection = new URL(url + "?" + query).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		StringBuilder sb = new StringBuilder();
		try (Scanner scanner = new Scanner(response)) {
			String[] split = scanner.useDelimiter("\\A").next().split("\t");
			String diseaseDoId = split[split.length-1];
			frid.write(diseaseDoId);
		}
		frid.close();
		// ver os abstracts
		File f = new File(publicHtmlPath + field + "/" + disease.replaceAll("\\s+", "").replaceAll("/", "")
				+ "/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + "TitlesAndAbstracts.tsv");

		BufferedReader br = new BufferedReader(new FileReader(f));
		String abs = "";
		// para cada abstract
		File fileNer = new File(
				publicHtmlPath + field + "/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + "/"
						+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileNer));

		while ((abs = br.readLine()) != null) {



			String[] idTitleAbstract = abs.split("\t");
			String id = idTitleAbstract[0];

			text = abs.substring(abs.indexOf("\t")+1).trim();

			query = String.format("lexicon=%s&text=%s", URLEncoder.encode(lexicon, charset),
					URLEncoder.encode(text, charset));
			// disparar
			connection = new URL(url + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			response = connection.getInputStream();
			// obter resposta
			try (Scanner scanner = new Scanner(response)) {
				if (scanner.useDelimiter("\\A").hasNext()) {
					String responseBody = scanner.useDelimiter("\\A").next();
					String[] responseline = responseBody.split("\n");
					sb = new StringBuilder();
					for(String str : responseline) {
						sb.append(id + "\t" + str + "\n");
					}
					bw.write(sb.toString().toLowerCase());
				}
				bw.newLine();
			}
		}
		bw.close();
		br.close();
	}

	public static void getArticlesNERPubTator(String disease, String field) throws IOException, InterruptedException {
		File f = new File(publicHtmlPath + field + "/" + disease.replaceAll("\\s+", "").replaceAll("/", "")
				+ "/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv");

		BufferedReader br = new BufferedReader(new FileReader(f));
		String article = "";
		StringBuilder sb = new StringBuilder();
		while ((article = br.readLine()) != null) {
			sb.append(article + ",");
		}
		sb.setLength(sb.length() - 1);
		String pmids = sb.toString();
		URL url_Submit;
		System.out.println("A obter NERS's (PubTator) dos artigos: " + pmids);
		url_Submit = new URL("https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/Disease" + "/" + pmids
				+ "/" + "BioC/");
		HttpURLConnection conn_Submit = (HttpURLConnection) url_Submit.openConnection();
		conn_Submit.setDoOutput(true);
		BufferedReader br_Submit = new BufferedReader(new InputStreamReader(conn_Submit.getInputStream()));
		String line = null;
		File fileNer = new File(
				publicHtmlPath + field + "/" + disease.replaceAll("\\s+", "").replaceAll("/", "") + "/"
						+ disease.replaceAll("\\s+", "").replaceAll("/", "") + "AbstractsNERPubTator.xml");

		BufferedWriter bw = new BufferedWriter(new FileWriter(fileNer));
		while ((line = br_Submit.readLine()) != null) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
		conn_Submit.disconnect();

		br.close();

	}

	//////////////////////// TFIDF ////////////////////////

	// tf
	private static double getTermFrequency(String disease, String pathMer, int docId) throws IOException {

		double diseaseFrequency = getDiseaseFrequency(disease, pathMer, String.valueOf(docId));

		double numberReferences = getTotalReferences(new File(pathMer), String.valueOf(docId));

		if(numberReferences != 0) {
			return diseaseFrequency/numberReferences;
		}
		return 0;
	}


	/**
	 * @param disease - doenca
	 * @param pathMer - caminho do ficheiro mer
	 * @param docId - String com o id do documento requirido
	 * @return numero de ocorrencias da disease no documento com id docId
	 * @throws IOException 
	 */
	/*Numerador do TF*/
	private static int getDiseaseFrequency(String disease, String pathMer, String docId) throws IOException {

		int count = 0;

		File file = new File(pathMer);

		Scanner sc = new Scanner(file);

		String[] line = null;

		while(sc.hasNextLine()) {
			line = sc.nextLine().split("\t");
			if(line[0].equals(docId) && line[3].equalsIgnoreCase(disease)) {
				count++;
			}
		}

		sc.close();
		System.out.println("NUMERADOR TF: " + count);
		return count;
	}

	/**
	 * @param file - Ficheiro MER
	 * @param docId - o id do documento
	 * @return numero de referencias existentes no documento com id docId
	 * @throws IOException 
	 */
	/*Denominador do TF*/
	private static int getTotalReferences(File file, String docId) throws IOException {
		int count = 0;
		String[] line = null;
		String id = null;

		Scanner sc = new Scanner(file);

		while(sc.hasNextLine()) {
			line = sc.nextLine().split("\t");
			id = line[0];		
			if(!id.trim().isEmpty() && line[0].equals(docId)) {
				count++;
			}
		}

		sc.close();
		System.out.println("DENOMINADOR TF: " + count);
		return count;
	}

	//--------
	// idf
	private static double getInverseDocumentFrequency(String disease, String pathIds, String pathMer) throws IOException {

		//Numerador do IDF
		int totalDocuments = getTotalDocuments(new File(pathIds));

		// Denominador do IDF  
		int numberAbstsWhereDiseaseOcurrs = getDocumentsMentioningDisease(disease, new File(pathMer));

		if(numberAbstsWhereDiseaseOcurrs > 0) {
			return Math.log((totalDocuments / numberAbstsWhereDiseaseOcurrs));
		}else {
			return 0;
		}
	}


	// Numerador do IDF
	private static int getTotalDocuments(File pathIds) throws IOException {
		int numeroCorpus = 0; 
		BufferedReader br = new BufferedReader(new FileReader(pathIds)); // ficheiro ids
		while((br.readLine()) != null) {
			numeroCorpus++;
		}

		br.close();
		System.out.println("NUMERADOR IDF: " + numeroCorpus);
		return numeroCorpus;
	}

	// Denominador do IDF
	private static int getDocumentsMentioningDisease(String disease, File file) throws IOException {

		boolean b = true;

		String[] line = null;
		String verificaVazia = null;
		int count = 0;

		Scanner sc = new Scanner(file);

		while(sc.hasNextLine()) {
			line = sc.nextLine().split("\t");
			verificaVazia = line[0];
			if(verificaVazia.trim().isEmpty()) 
				b = true;
			if(!verificaVazia.trim().isEmpty() && line[3].equalsIgnoreCase(disease) && b) { 
				count++;
				b = false;
			}
		}

		sc.close();
		System.out.println("DENOMINADOR IDF: " + count);
		return count;
	}
	////////////////////////////////////////////////


	////// INDICE INVERTIDO + IDFTF + COLOCAR NO FICHEIRO 

	/**
	 * Vai construir o indice invertido como esta nos slides
	 * colocando o nome da doenca, os numeros dos indices inicio e fim das ocorrencias
	 * @throws IOException 
	 */
	public static void makeInvertedIndex(String disease, String field) throws IOException {

		String pathMer = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv";

		File file = new File(pathMer);

		Scanner sc = new Scanner(file);

		HashMap<String, List<List<String>>> invertedIndex = new HashMap<>();

		String[] line = null;

		while(sc.hasNextLine()) {
			line = sc.nextLine().split("\t");
			String id = line[0];
			if(id.trim().isEmpty()) {
				if(sc.hasNextLine()) {
					line = sc.nextLine().split("\t");
					continue;
				}else {
					continue;
				}
			}
			if(!invertedIndex.containsKey(line[3])) { // se a entrada daquela doenca ainda nao existe no hashmap
				invertedIndex.put(line[3], new ArrayList<>());
				invertedIndex.get(line[3]).add(new ArrayList<>()); 
				// vamos adicionar o no unico arraylist logo index = 0
				// usei um pair pois o primeiro vai ter o id para depois comparar
				invertedIndex.get(line[3]).get(0).add(line[0]);
				invertedIndex.get(line[3]).get(0).add("/");
				invertedIndex.get(line[3]).get(0).add(line[1]);
				invertedIndex.get(line[3]).get(0).add("-");
				invertedIndex.get(line[3]).get(0).add(line[2]);
			} else { // se a entrada daquela doenca ja existe no hashmap 
				boolean b = true;

				List<List<String>> lists = invertedIndex.get(line[3]);

				for (List<String> list : lists) {
					if(list.get(0).equals(line[0])) {
						list.add("+");
						list.add(line[1]);
						list.add("-");
						list.add(line[2]);
						b = false;
						break;
					}
				}

				if (b) {
					lists.add(new ArrayList<>());
					lists.get(lists.size() - 1).add(line[0]);
					lists.get(lists.size() - 1).add("/");
					lists.get(lists.size() - 1).add(line[1]);
					lists.get(lists.size() - 1).add("-");
					lists.get(lists.size() - 1).add(line[2]);
				}

			}
		}

		sc.close();

		addtfIdfToInvertedIndex(field, disease, invertedIndex);
	}	

	/**
	 * Vai adicionar o tf idf ao hashmap do indice invertido
	 * @param field - field da doenca
	 * @param disease - doenca
	 * @param pathMer - caminho do ficheiro MER
	 * @param pathIds- caminho do ficheiro dos ids 
	 * @param invertedIndex - HashMap do Indice Invertido
	 * @throws IOException
	 */
	private static void addtfIdfToInvertedIndex(String field, String disease, 
			HashMap<String, List<List<String>>> invertedIndex) throws IOException {

		String pathMer = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv";

		String pathIds =publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv";	

		//IDF
		double idf = getInverseDocumentFrequency(disease, pathIds, pathMer); //tem de ser chamado uma vez

		for (Entry<String, List<List<String>>> elem : invertedIndex.entrySet()) {	
			for (List<String> list : elem.getValue()) { // lista de string que esta no value do hashmap (lista pequena)
				double tf = getTermFrequency(disease, pathMer, Integer.parseInt(list.get(0)));
				double tfidf = tf * idf;
				System.out.println("TF IDF: " + tfidf);
				System.out.println("TF: " + tf);
				System.out.println("IDF: " + idf);
				list.add(String.valueOf("/"));
				list.add(String.valueOf(tfidf));
				list.add(String.valueOf(", "));
			}
		}
		// Passar o indice invertido com tfidf para o ficheiro de texto
		invertedIndexToFile(field, disease, invertedIndex); 
	}

	/**
	 * Coloca o Hashmap do indice invertido no ficheiro
	 * @param field - campo da doenca
	 * @param disease - doenca
	 * @param invertedIndex - hashmap do indice invertido
	 */
	private static void invertedIndexToFile(String field, String disease,
			HashMap<String, List<List<String>>> invertedIndex) {

		String pathInvertedIndex = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesInvertedIndex.tsv";

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(pathInvertedIndex));

			for (Entry<String, List<List<String>>> elem : invertedIndex.entrySet()) {
				bw.write(elem.getKey());
				bw.write("\t");

				for (List<String> listaPequena : elem.getValue()) {
					for (int i = 0; i < listaPequena.size(); i++) { // Lista de Listas 
						bw.write(listaPequena.get(i));
					}
				}
				bw.newLine();
			}

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		tfIdfOrganizado(field, disease, invertedIndex);
	}



	private static void tfIdfOrganizado(String field, String disease,
			HashMap<String, List<List<String>>> invertedIndex) {

		String pathTfidfOrganizado = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "tfidf.tsv";

		HashMap<String,List<String>> hm = new HashMap<>();

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(pathTfidfOrganizado));


			for (Entry<String, List<List<String>>> elem : invertedIndex.entrySet()) {
				for (List<String> listaPequena : elem.getValue()) {

					if(hm.containsKey(listaPequena.get(0))) {
						hm.get(listaPequena.get(0)).add(listaPequena.get(listaPequena.size() - 2));
					} else {
						hm.put(listaPequena.get(0), new ArrayList<>());
						hm.get(listaPequena.get(0)).add(listaPequena.get(listaPequena.size() - 2));
					}
				}
			} // hashmap hm fica preenchido e pronto

			int total;
			double soma = 0;
			for (Entry<String, List<String>> elem : hm.entrySet()) {
				total = elem.getValue().size();		

				for (List<String> listaPequena : hm.values()) {
					for (int j = 0; j < listaPequena.size(); j++) {
						soma += Double.parseDouble(listaPequena.get(j));
					}
				}
				bw.write(elem.getKey());
				bw.write("\t");
				bw.write(String.valueOf(soma / total));
				bw.newLine();
				soma = 0;
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////


	////////////////////////DISHLN////////////////////////

	/**
	 * Metodo calcula os valor dishln para os varios documentos
	 * @param disease - doenca procurada
	 * @param mer - ficheiro MER
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void dishln(String disease, String field) throws IOException, InterruptedException {

		String pathMer = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv";

		String pathDoid = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "Doid.tsv";

		String path = publicHtmlPath + "doid.txt";

		File file = new File(pathMer);
		File fileDoid = new File(pathDoid);

		Scanner sc = new Scanner(file);
		Scanner scDoid = new Scanner(fileDoid);
		HashMap<String, List<String>> dishlnMap = new HashMap<>();
		String[] line, url, urlDoid = null;
		String result;
		urlDoid = scDoid.nextLine().split("/");//http://purl.obolibrary.org/obo/DOID_2841


		//// doid.txt
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		while(sc.hasNextLine()) {
			line = sc.nextLine().split("\t");
			String id = line[0];
			if(id.trim().isEmpty()) {
				if(sc.hasNextLine()) {
					line = sc.nextLine().split("\t");
					continue;
				}else {
					continue;
				}
			}
			bw.write(line[3]);
			bw.write(System.getProperty("line.separator"));
		}
		bw.close();

		String[] cmd = { "sh", "scriptDB.sh", publicHtmlPath};
		System.out.println("Creating semantic base...");
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();

		////		

		Scanner sc2 = new Scanner(file);
		while(sc2.hasNextLine()) {
			line = sc2.nextLine().split("\t");
			String id = line[0];
			if(id.trim().isEmpty()) {
				if(sc2.hasNextLine()) {
					line = sc2.nextLine().split("\t");
					continue;
				}else {
					continue;
				}
			}else {
				url = line[4].split("/");
				result = dishlnPython(urlDoid[4], url[4]);

				if(dishlnMap.containsKey(id)) {
					dishlnMap.get(id).add(result);
				}
				else {
					dishlnMap.put(id, new ArrayList<>());
					dishlnMap.get(id).add(result);
				}
			}
		}
		sc.close();
		sc2.close();
		scDoid.close();

		calculaDishln(field, disease, dishlnMap);		
	}

	/**
	 * Corre a funcao python com as duas doencas e retorna a similaridade entre elas
	 * @param disease - doenca pesquisada
	 * @param ref - doenca referenciada encontrada
	 * @return - A similaridade entre disease e ref 
	 * @throws IOException
	 */
	private static String dishlnPython(String doidDisease, String doidRef) throws IOException {

		String caminho = publicHtmlPath + "dishin.py";
		String[] cmd = new String [5];
		cmd[0] = "python"; 
		cmd[1] = caminho;
		cmd[2] = publicHtmlPath + "doid.db";
		cmd[3] = doidDisease;
		cmd[4] = doidRef;
		System.out.println("A comparar a similaridade entre " + doidDisease + " e " + doidRef + "...");
		// create runtime to execute external command
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);

		ArrayList<String> list = new ArrayList<String>(); 
		// retrieve output from python script
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		while((line = bfr.readLine()) != null) {
			// display each output line form python script
			list.add(line);
		}
		if(!list.get(0).equals("Error: entry unknown")) {
			String[] linePython = list.get(2).split("\t");
			return linePython[3]; 
		} else {
			return "0";
		}

	}

	/**
	 * Percorre o map, calcula a media e escreve num ficheiro o resultado final da similaridade entre a doenca procurada e as encontradas
	 * @param dishlnMap - HashMap em que a key eh o id do docunto e tem associado um arraylist que sao as similaridades das doencas encontradas
	 * @return - a media das similaridades calculadas
	 * @throws IOException 
	 */
	private static void calculaDishln(String field, String disease, HashMap<String, List<String>> dishlnMap) throws IOException {

		String path = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "Dishln.tsv";

		//		String path1 = "/home/aw003/public_html/" + field + "/" + 
		//				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
		//				disease.replaceAll("\\s+", "").replaceAll("/", "") + "RelevDate.tsv";

		BufferedWriter bw = new BufferedWriter(new FileWriter(path));

		for (Entry<String, List<String>> elem : dishlnMap.entrySet()) {			
			bw.write(elem.getKey());
			bw.write("\t");
			bw.write(calculaMedia(elem.getValue()));
			bw.write(System.getProperty("line.separator"));
		}
		bw.close();


		//		BufferedWriter bw2 = new BufferedWriter(new FileWriter(path1));
		//
		//		for (Entry<String, List<String>> elem : dishlnMap.entrySet()) {			
		//			bw2.write(elem.getKey());
		//			bw2.write("\t");
		//			bw2.write(calculaMedia(elem.getValue()));
		//			bw2.write(System.getProperty("line.separator"));
		//		}
		//		bw2.close();
	}

	/**
	 * Calcula a media da lista
	 * @param lista
	 * @return
	 * @throws IOException
	 */
	private static String calculaMedia(List<String> lista) throws IOException {

		int total = lista.size();
		double soma = 0;

		for (String value : lista) {
			soma += Double.parseDouble(value);
		}

		return String.valueOf(soma/total);
	}

	//////////////////////////Data/////////////////////////////////////////
	public static void getDateArticles(String disease, String field) throws IOException, InterruptedException {

		String pathIds = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv"; //ids

		String pathPesos = publicHtmlPath + field + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				disease.replaceAll("\\s+", "").replaceAll("/", "") + "RelevDate.tsv"; // id peso

		BufferedReader br = new BufferedReader(new FileReader(pathIds));
//		List<String> ids = new ArrayList<>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(pathPesos));
		
		String s;
//		int index = 0;
		while((s = br.readLine()) != null) { // ler o ficheiro dos ids
			bw.write(s + "\t" + 1.0);
			bw.newLine();
		}
		br.close();
		bw.close();

//		String s;
//		int index = 0;
//		while((s = br.readLine()) != null) { // ler o ficheiro dos ids
//			int id = Integer.parseInt(s);
//			ids.add(s);
//			//			correr um comando que vai criar um ficheiro temporario chamado 
//			//			id.xml
//
//			ArrayList<String> list = new ArrayList<String>();			
//			list.add("sh");
//			list.add("-c");
//			list.add(initialPath + "scripts/curlDates.sh  " + id + " > " + publicHtmlPath + "id.xml");
//			ProcessBuilder build = new ProcessBuilder(list);
//			Process p = build.start();
//			p.waitFor();
//
//			String pathDateFile = publicHtmlPath + "date.txt"; // Ficheiro com a data do artigo
//			BufferedWriter bwDate = new BufferedWriter(new FileWriter(pathDateFile));
//			ArrayList<String> list2 = new ArrayList<String>();
//			list2.add("sh");
//			list2.add("-c");
//			list2.add(initialPath + "scripts/getYear.sh " + publicHtmlPath + "id.xml" + " " + pathDateFile);
//			ProcessBuilder build2 = new ProcessBuilder(list2);
//			Process p2 = build2.start();
//			p2.waitFor();
//			bwDate.newLine();
//			
//			ArrayList<String> list3 = new ArrayList<String>();
//			list3.add("sh");
//			list3.add("-c");
//			list3.add(initialPath + "scripts/getMonth.sh " + publicHtmlPath + "id.xml" + " " + pathDateFile);
//			ProcessBuilder build3 = new ProcessBuilder(list3);
//			Process p3 = build3.start();
//			p3.waitFor();
//			bwDate.newLine();
//			
//			ArrayList<String> list4 = new ArrayList<String>();
//			list4.add("sh");
//			list4.add("-c");
//			list4.add(initialPath + "scripts/getDay.sh " + publicHtmlPath + "id.xml" + " " + pathDateFile);
//			ProcessBuilder build4 = new ProcessBuilder(list4);
//			Process p4 = build4.start();
//			p4.waitFor();
//			bwDate.close();
//			String str = null;
//			List<Integer> data = new ArrayList<>(); //ano, mes, dia
//			BufferedReader brr = new BufferedReader(new FileReader(pathDateFile));
//			while((str = brr.readLine()) != null) { // ler o ficheiro com a data do artigo
//				data.add(Integer.parseInt(str));
//			}
//			brr.close();
//
//			Date date = new Date();
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//			String[] dataHojeArray = formatter.format(date).split("-");  // 2019 05 15
//
//			int dataPublicacaoInt = (data.get(0)*365)+(data.get(1)*30)+(data.get(2));
//
//			int hoje = (Integer.parseInt(dataHojeArray[0])*365)+(Integer.parseInt(dataHojeArray[1])*30)+(Integer.parseInt(dataHojeArray[2]));
//
//			ArrayList<Double> pesos = new ArrayList<>();
//
//			if (dataPublicacaoInt == hoje) {
//				pesos.add(1.0);
//			}
//			else {
//				pesos.add((double) (hoje/dataPublicacaoInt));
//			}
//
//			bw.write(s + "\t" + pesos.get(index));
//
//			index++;


		} // fim da leitura do ficheiro dos ids
//		bw.close();
//		br.close();
//	}
	///////////////////////////////////////////////////////////////////////



	//////////////////////////Relevance/////////////////////////////////////////

	public static void getArticlesImplicitFeedback(String str, String field) throws IOException  {
		String path = publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv";

		BufferedReader br = new BufferedReader(new FileReader(path));
		File implicitPath = new File(publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "Implicit.tsv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(implicitPath,true));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine())!= null) {
			sb.append(line + "\t" + Math.random() + "\n");			
		}
		bw.write(sb.toString());
		bw.close();
		br.close();

	}

	public static void getArticlesExplicitFeedback(String str, String field) throws IOException {
		// TODO Auto-generated method stub
		String path = publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + ".tsv";

		BufferedReader br = new BufferedReader(new FileReader(path));
		File explicitPath = new File(publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "Explicit.tsv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(explicitPath,true));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine())!= null) {
			sb.append(line + "\t" + Math.random() + "\n");			
		}
		bw.write(sb.toString());
		bw.close();
		br.close();
	}

	public static void getArticlesAveragedFeedback(String str, String field) throws IOException  {
		File implicitPath = new File(publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "Implicit.tsv");
		File explicitPath = new File(publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "Explicit.tsv");
		File result = new File(publicHtmlPath + field + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "/" + 
				str.replaceAll("\\s+", "").replaceAll("/", "") + "AvgFeedback.tsv");

		BufferedReader br1 = new BufferedReader(new FileReader(implicitPath));
		BufferedReader br2 = new BufferedReader(new FileReader(explicitPath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));

		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = br1.readLine())!= null) {
			String[] split1 = line.split("\t");
			String[] split2 = br2.readLine().split("\t");
			sb.append(split1[0] + "\t");
			double avg = (Double.parseDouble(split1[1]) + Double.parseDouble(split2[1])) / 2;
			sb.append(avg + "\n");
		}
		bw.write(sb.toString());
		br1.close();
		br2.close();
		bw.close();
	}
	public static void weightedAverage(String disease, String field) throws IOException, InterruptedException {
		String caminho = initialPath + "Python/weightaverage.py";
		String[] cmd = new String [4];
		cmd[0] = "python"; 
		cmd[1] = caminho;
		cmd[2] = field;
		cmd[3] = disease;
		System.out.println("A calcular a media com os pesos...");
		// create runtime to execute external command
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);

		// retrieve output from python script
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		while((line = bfr.readLine()) != null) {
			// display each output line form python script
			System.out.println(line);
		}
	}
}