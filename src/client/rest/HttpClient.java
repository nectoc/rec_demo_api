package client.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpClient {
	
	  public CloseableHttpResponse doHttpPost(String serverUrl, String resourcePath, String parametersJson) throws Exception {
	    	try {
	    	    CloseableHttpClient httpClient =  HttpClients.createDefault();
	            HttpPost post = new HttpPost(serverUrl + resourcePath);
	            post.setHeader(MLIntegrationTestConstants.CONTENT_TYPE, MLIntegrationTestConstants.CONTENT_TYPE_APPLICATION_JSON);
	            post.setHeader(MLIntegrationTestConstants.AUTHORIZATION_HEADER, getBasicAuthKey());
	            post.setHeader("Origin","http://10.100.4.112:9767");
	            if(parametersJson != null) {
	                StringEntity params = new StringEntity(parametersJson);
	                post.setEntity(params);
	            }
	            return httpClient.execute(post);
	            
	        } catch (Exception e) {
	            throw new Exception("Failed to post to " + resourcePath, e);
	        }
	    }
	  
	
	public String getBasicAuthKey() {
        String token = "admin" + ":" + "admin";
        byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
        String encodedToken = new String(Base64.encodeBase64(tokenBytes), StandardCharsets.UTF_8);
        return (MLIntegrationTestConstants.BASIC + encodedToken);
    }
	
	 /**
     * @param response {@link CloseableHttpResponse}
     * @return null if response is invalid. Json as string, if it is a valid response.
     * @throws MLHttpClientException
     */
    public String getResponseAsString(CloseableHttpResponse response) throws Exception {
        if (response == null || response.getEntity() == null) {
            return null;
        }
        String reply = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            try {
                JSONObject responseJson = new JSONObject(line);
                reply = responseJson.toString();
            } catch (JSONException e) {
                JSONArray responseArray = new JSONArray(line);
                reply = responseArray.toString();
            }
            bufferedReader.close();
            response.close();
            return reply;
        } catch (Exception e) {
            throw new Exception("Failed to extract the response body.", e);
        }
    }
    
    public CloseableHttpResponse doHttpGet(String serverUrl,String resourcePath) throws Exception {
        CloseableHttpClient httpClient =  HttpClients.createDefault();
        HttpGet get = null;
        try {
            get = new HttpGet(serverUrl + resourcePath);
            get.setHeader(MLIntegrationTestConstants.CONTENT_TYPE, MLIntegrationTestConstants.CONTENT_TYPE_APPLICATION_JSON);
            get.setHeader(MLIntegrationTestConstants.AUTHORIZATION_HEADER, getBasicAuthKey());
            return httpClient.execute(get);
        } catch (Exception e) {
            throw new Exception("Failed to get " + resourcePath, e);
        }
    	
    }
    
    
	

}
