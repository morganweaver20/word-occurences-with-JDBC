package pkg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainFile {
	public static void main(String[] args) throws Exception {
		
		postPoem();
		getPoem();

 	} //end of main method
	
	//method connects to database
	public static Connection getConnection() throws Exception {
		
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/word_occurences";
			String username = "root";
			String password = "rootpassword";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("connected");
			return conn;
		} catch (Exception e) {System.out.println(e);}
		
		return null;
		
	} //end of getConnection method
	//method gets,formats, and posts poem to database
	public static void postPoem() {

		// parsing file using jsoup and saving it to a Document object
		Document doc = null;
 		try {
 			doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

 		} catch (IOException e) {

 			e.printStackTrace();
 		}

 		// parsing document for title, author, and poem and formatting as Strings
 		String title = doc.select("h1").text().toLowerCase();

 		String author = doc.select("h2").text().toLowerCase();

 		String poem = doc.select("p").text().replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();

 		// Concating strings together
 		String fullText = title.concat(" ").concat(author).concat(" ").concat(poem);
 		// System.out.println

 		// converting full text string to an array
 		String[] words = fullText.split(" ");

 		// creating a hashmap to store the words and their occurences as key-value pairs
 		Map<String, Integer> wordMap = new HashMap<String, Integer>();

 		// for loop to count word occurences and add to the hashmap
 		for (String word : words) {
 			if (!wordMap.containsKey(word))
 				wordMap.put(word, 1);
 			else
 				wordMap.put(word, wordMap.get(word) + 1);
 		}
 		
 		//creating connection to db
 		try {
			Connection con = getConnection();
			
	 		for(Map.Entry<String, Integer> mapElement : wordMap.entrySet()) {
	 			String key = mapElement.getKey();
	 			int value = mapElement.getValue();
	 			PreparedStatement posted = con.prepareStatement("INSERT INTO words (word, occurences) VALUES ('"+key+"', '"+value+"')");
	 			posted.executeUpdate();
	 		}
		} catch (Exception e) {

			e.printStackTrace();
		}
 		

	} //end of getPoem method
	//method to get word occurences from database and print to console
	public static void getPoem() throws Exception{
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM words ORDER BY occurences DESC");
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				String wordId = result.getString("word_id");
				String word = result.getString("word");
				String occurences = result.getString("occurences");
				System.out.println("ID: " + wordId + " WORD: " + word + " OCCURENCES: " + occurences);
			}
			System.out.println("End of records");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

