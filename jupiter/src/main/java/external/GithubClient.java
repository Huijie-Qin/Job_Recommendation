package external;

import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class GithubClient {	//% 意味着 要填充的
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	
	public List<Item> search(double lat, double lon, String keyword) {
		// prepare HTTP request parameter.
		//corner case 
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		//对于 keyword要进行 parsing,转码.
		//remove space.-->可以自己写，也可以调包
		
		try {
			keyword = URLEncoder.encode(keyword,"UTF-8");// Rick Sun -->Rick+Sun
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		String url = String.format(URL_TEMPLATE, keyword,lat,lon);
		
		// send HTTP request.
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {	// 这边调用httpClient.execute这个方法发送 HTTP request.
			
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			
			if(response.getStatusLine().getStatusCode() != 200 ) {
				return new ArrayList<>();
			}
			HttpEntity entity = response.getEntity();
			
			if(entity == null) {
				return new ArrayList<>();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();
			//reader.readLine();//每次读一行
			String line = null;
			while((line = reader.readLine()) != null) {
				responseBody.append(line); //每读一行，加到responseBody中。
			}
			
			JSONArray array = new JSONArray(responseBody.toString());
			return getItemList(array);
			
 		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// get HTTP response body
		
		
		
		
	
		
		return new ArrayList<>();
			
	}
	
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		
		List<String> descriptionList = new ArrayList<>();
		
		for (int i = 0; i < array.length(); i++) {
			// We need to extract keywords from description since GitHub API
			// doesn't return keywords.
			
			//put all description for each item into a descriptionList.
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}	
		}

		String[] strings = descriptionList.toArray(new String[descriptionList.size()]);
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(strings);
		
		for (int i = 0; i < array.length(); ++i) {
			JSONObject object = array.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			
			Set<String> set = new HashSet<>(keywords.get(i));
			builder.setKeywords(set);
			
			Item item = builder.build();
			itemList.add(item);
		
		}
		
		return itemList;
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

	
	
}
