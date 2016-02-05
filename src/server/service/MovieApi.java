package server.service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import static org.testng.AssertJUnit.assertEquals;

import org.apache.cxf.rs.security.cors.CorsHeaderConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
//import server.obj.Movie;
import client.rest.HttpClient;
//import org.apache.http.HttpHeaders;
import client.rest.MLIntegrationTestConstants;

//import server.obj.*;

@Path("/data")
public class MovieApi {
	
	@Context
    HttpHeaders headers;
	HttpClient httpClient;
	private CloseableHttpResponse response;
	
	
	
	//Browser executes options call first to check whether cross origin communication is allowed
	@OPTIONS
	public Response options() {
		String origin = headers.getRequestHeader("Origin").get(0);
        if ("*".equals(origin)) {
            return Response.ok()
                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "DELETE PUT POST GET")
                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                           .build();
        } else {
            return Response.ok().build();
        }
	}
	
	//Post data from UI to backend
	@POST
	@Path("/{userId}/passData")
	@Produces("application/json")
	@Consumes("application/json")
	public Response postData(@PathParam("userId") String userId, @QueryParam("movieId") String movieId,
			@QueryParam("rate") String rate,@QueryParam("timeStamp") String timeStamp){
		
		int userID = Integer.parseInt(userId);
		int movieID = Integer.parseInt(movieId);
		int movieRate = Integer.parseInt(rate);
		
		if(userID ==0 || movieRate == 0){
			return Response.status(Response.Status.BAD_REQUEST).build();	
		}
		
		try{

			String payload = "{'event':{'metaData': {},'correlationData': {},'payloadData':{'UserID':"+ userID+ ",'MovieID':"+movieID +",'Rating':"+movieRate+",'Timestamp':"+timeStamp+"}}}";
			
			try {
	    	    CloseableHttpClient httpClient =  HttpClients.createDefault();
	            HttpPost post = new HttpPost("http://10.100.4.112:9766/endpoints/streamOneRec");
	            post.setHeader(MLIntegrationTestConstants.CONTENT_TYPE, MLIntegrationTestConstants.CONTENT_TYPE_APPLICATION_JSON);
	            post.setHeader(MLIntegrationTestConstants.AUTHORIZATION_HEADER, "Basic YWRtaW46YWRtaW4=");
	            //post.setHeader("Origin","http://10.100.4.112:9767");
	            if(payload != null) {
	            	System.out.println("Payload is not null");
	                StringEntity params = new StringEntity(payload);
	                System.out.println("Setting payload to string entity");
	                post.setEntity(params);
	                System.out.println("Setting entity");
	                httpClient.execute(post);
	                System.out.println("Post excuted");
            }
            
	            
	        } catch (Exception e) {
	        	throw new Exception("Failed to post to " + e);
	        }
			
			return Response.ok().build();  	    
		
		}catch(Exception e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	//Post data to DAS
	@POST
	@Path("/{userId}/postDas")
	@Produces("application/json")
	@Consumes("application/json")
	public Response postDas(){
		return null;
		
	}
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello(){
		return "Hello!";
	
}



}
