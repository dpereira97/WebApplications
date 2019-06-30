import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;

public class WebService {

	private static final String publicHtmlPath = "/home/aw003/public_html/";

	@SuppressWarnings("unchecked")
	public static void merToJson(String disease, String field) throws IOException {
		String path = publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")
				+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv";

		String fileJson = publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")
				+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.json";

		File file = new File(path);
		Scanner sc = new Scanner(file);
		String[] line = null;

		Map<String, List<String>> hm = new HashMap<>();

		if(Files.exists(Paths.get(path))) {
			while(sc.hasNextLine()) {
				line = sc.nextLine().split("\t"); //id, idxcomeca,idxacaba,nomedoenca,doid
				String id = line[0];
				if(id.trim().isEmpty()) {
					if(sc.hasNextLine()) {
						continue;
					}else {
						continue;
					}
				}
				if(!hm.containsKey(id)) {
					hm.put(id, new ArrayList<>());
					hm.get(id).add(line[3]);
					hm.get(id).add(line[1]);
					hm.get(id).add(line[2]);
				} else {
					hm.get(id).add(line[3]);
					hm.get(id).add(line[1]);
					hm.get(id).add(line[2]);
				}
			} 
		}

		sc.close();

		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(hm);
		FileWriter fw = new FileWriter(fileJson);
		fw.write(jsonObject.toString());

		fw.close();

	}
	
	public static void relatedDiseases(String disease, String field) throws IOException {
		String path = publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")
				+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "ArticlesMER.tsv";

		String pathDiseases = publicHtmlPath + "diseases.tsv";
		File fileDiseases = new File(pathDiseases);

		File file = new File(path);
		Scanner sc = new Scanner(file);
		Scanner sc2 = new Scanner(fileDiseases);
		String[] line = null;
		List<String> list = new ArrayList();

		while(sc2.hasNextLine()) {//diseases.tsv
			list.add(sc2.nextLine().toLowerCase());
		}

		String pathRelated = publicHtmlPath + field+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "")
				+"/"+disease.replaceAll("\\s+", "").replaceAll("/", "") + "RelatedDiseases.tsv";
		FileWriter fwRelated = new FileWriter(new File(pathRelated));

		List<String> list2 = new ArrayList();
		while(sc.hasNextLine()) {//mer

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
			if(list.contains(line[3]) && !list2.contains(line[3]) && !line[3].equals(disease)) {
				list2.add(line[3]);
				fwRelated.write(line[3]);
				fwRelated.write("\n");
			}
		}
		sc.close();
		sc2.close();
		fwRelated.close();
	} 
}