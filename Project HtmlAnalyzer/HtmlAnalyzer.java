import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <URL>");
            return;
        }
        
        String urlString = args[0];
        String htmlContent = fetchHtml(urlString);
        
        if (htmlContent == null) {
            System.out.println("URL connection error");
            return;
        }
        
        analyzeHtml(htmlContent);
    }
    
    private static String fetchHtml(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            connection.disconnect();
            
            return content.toString();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private static void analyzeHtml(String html) {
        String[] lines = html.split("\n");
        Stack<String> tagStack = new Stack<>();
        
        int currentDepth = 0;
        int maxDepth = -1;
        String deepestText = null;
        
        for (String line : lines) {
            
            String trimmed = line.trim();
            
            
            if (trimmed.isEmpty()) {
                continue;
            }
            
            
            if (trimmed.startsWith("<") && trimmed.endsWith(">") && !trimmed.startsWith("</")) {
                
                String tagName = trimmed.substring(1, trimmed.length() - 1);
                tagStack.push(tagName);
                currentDepth++;
            }
            
            else if (trimmed.startsWith("</") && trimmed.endsWith(">")) {
                
                String tagName = trimmed.substring(2, trimmed.length() - 1);
                
                
                if (tagStack.isEmpty() || !tagStack.peek().equals(tagName)) {
                    System.out.println("malformed HTML");
                    return;
                }
                
                tagStack.pop();
                currentDepth--;
            }
            
            else {
                
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestText = trimmed;
                }
            }
        }
        
        
        if (!tagStack.isEmpty()) {
            System.out.println("malformed HTML");
            return;
        }
        
        
        if (deepestText != null) {
            System.out.println(deepestText);
        }
    }
}
