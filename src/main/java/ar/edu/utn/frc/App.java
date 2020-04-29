package ar.edu.utn.frc;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class App {
    // **********************************************
    // *** Update or verify the following values. ***
    // **********************************************

    // Add your Computer Vision subscription key and endpoint to your environment variables.
    // After setting, close and then re-open your command shell or project for the changes to take effect.
    static String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
    static String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");


    private static final String uriBase = endpoint + "vision/v2.1/ocr";

    private static String imageToAnalyze = null; 
    		//"http://www.investigacion.frc.utn.edu.ar/computos/FotoCarnet.jpg";
    
    private static String language = "es";

    public static void main(String[] args) {
    	
    	if (verifyArgs(args)==0) return;
    	
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", getLanguage());
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            StringEntity requestEntity =
                    new StringEntity("{\"url\":\"" + getImageUrl() + "\"}");
            request.setEntity(requestEntity);

            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("REST Json:\n");
                System.out.println(json.toString(2));
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }
    
    private static int verifyArgs(String[] args) {
    	
    	if (args.length==0) {
    		printUsage();
    		return 0;
    		
    	} else {
    		
    		if ((endpoint==null) || (subscriptionKey==null)) {
    			printUsage();
    			return 0;
    		}
    		
            for (String arg: args) {
                String[] parts = arg.split("=");
                //argsMap.put(parts[0], parts[1]);

                if ("language".equalsIgnoreCase(parts[0])) {
                	setLanguage(parts[1]);
                	System.out.println("Lenguaje definido : " + getLanguage());                	
                }

                
                if ("url".equalsIgnoreCase(parts[0])) {
                	String laUrl = parts[1];                 	

                    try {
                        URIBuilder uriBuilder = new URIBuilder(laUrl);
                        URI uri = uriBuilder.build();
                        
                        System.out.println("Uri : " + uri.toString());
                        
                    	setImageUrl(laUrl);
                        System.out.println("Url de la imagen : " + getImageUrl());
                        
                    } catch (Exception e) {
                        // Display error message.
                    	System.out.println("Url de la imagen mal definida");
                    	return 0;
                    } 
                	
                }

            } 
    				
    	}
    	
    	if (getImageUrl()==null) {
    		printUsage();
    		return 0;
    	}
        
    	return 1;
	
    }
    
    private static void printUsage() {
    	System.out.println("Mini App para obtener via Azure Cognitive el texto de una imagen");
    	System.out.println("Uso del Programa: java Main url=https://www.laurldelaimagen.com/miimagen.jpg language=es");
    	System.out.println("Para su funcionamiento debe definir 2 variables en el entorno: COMPUTER_VISION_SUBSCRIPTION_KEY y COMPUTER_VISION_ENDPOINT");    		    	
    }
     
    private static String getImageUrl() {
    	return imageToAnalyze;
    }
    
    private static void setImageUrl(String url) {
    	imageToAnalyze = url;
    }
    
    private static String getLanguage() {
    	return language;
    }
    
    private static void setLanguage(String idioms) {
    	language = idioms;
    }

}