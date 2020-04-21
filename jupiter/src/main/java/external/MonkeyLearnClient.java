package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;



public class MonkeyLearnClient {
	
	
	private static final String API_KEY = "289a33524baebc53cc81b2ea17d7090e3418870d";// 
    
	
	public static void main(String[] args) {
		
		String[] textList = {" I love sports very much and I am a student",
							"I love music very much and I am a student"};
				
		List<List<String>> keywords = extractKeywords(textList);
		for(List<String> wlist:keywords) {
			for(String w :wlist) {
				System.out.println(w);
			}
				System.out.println();
			
		}
		
	
	}
	
	//将其response 的json array, 转换list<list<String>>
	public static List<List<String>> extractKeywords(String[] text){
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}

		MonkeyLearn ml = new MonkeyLearn(API_KEY);
		
		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse res;
		
		try {
			res = ml.extractors.extract("ex_YCya9nrn", text, extraParams);
			List<List<String>> keywords = getKeywords(res.arrayResult);
			
			return keywords;
		} catch (MonkeyLearnException e) {// it’s likely to have an exception
			e.printStackTrace();
		}
		return new ArrayList<>();
        
	}
	
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		// Iterate the result array and convert it to our format.
		for (int i = 0; i < mlResultArray.size(); ++i) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) mlResultArray.get(i);
			for (int j = 0; j < keywordsArray.size(); ++j) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
				// We just need the keyword, excluding other fields.
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);

			}
			topKeywords.add(keywords);
		}
		return topKeywords;
	}


	

}
















