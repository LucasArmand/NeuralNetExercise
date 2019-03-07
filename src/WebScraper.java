import java.net.*;
import java.io.*;

public class WebScraper {

    public static void main(String[] args) throws Exception {
    	String name = "GOOG";
    	double init = System.currentTimeMillis();
    	double timer = init;
    	URL site = new URL("https://www.cnbc.com/quotes/?symbol=" + name);
    	BufferedReader in = new BufferedReader(
		        new InputStreamReader(site.openStream()));;
    	BufferedWriter out = new BufferedWriter(new FileWriter("C:/users/Lucas/desktop/stock-history-" + name + ".txt",true));
    	double mins = 120;
    	while(System.currentTimeMillis() < init + mins * 60000){
    		if(timer < System.currentTimeMillis() - 20000) {
    			timer = System.currentTimeMillis();
		        
		        in = new BufferedReader(
		        new InputStreamReader(site.openStream()));
		        //<span class="last original ng-binding" ng-bind="quoteData['AAPL'].lastOutputoriginal | filter:processStripCondition('AAPL','last','original')">167.1599</span>
		        String inputLine;
		        while ((inputLine = in.readLine()) != null)
		        	if(inputLine.contains("lastOutputoriginal")) {
		        		int start = inputLine.indexOf(">");
		        		int end = inputLine.indexOf("</");
		        		System.out.println(inputLine.substring(start + 1, end));
		        		out.append(name + " : " + System.currentTimeMillis() + " = " + inputLine.substring(start + 1, end) + "\n");
		        		
		        		
		        	}
		        //
		      in.close();
    		}
    	}
    	out.close();
}	
}