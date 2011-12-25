package edig.document.parser;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import edig.entites.Sentence;
import edig.entites.Word;
/**
 * Sentence Extractor parse based on Lingpipe sentence detection model, the returned sentences are stemmed using snowball algorithm
 * also stop words are removed too
 * @author ahmad
 *
 */
public class LingpipeParser implements ParserIF{
  static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
  static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();
  static final Hashtable<String, Boolean> STOPWORDS = loadStopWords("english_stop_words.txt"); 

  @Override
	public ArrayList<Sentence> parseText(String text) throws Exception {
 	  Class stemClass = Class.forName("org.tartarus.snowball.ext." + "english" + "Stemmer");
    SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
		ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
		List<String> tokenList = new ArrayList<String>();
  	List<String> whiteList = new ArrayList<String>();
  	Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),0,text.length());
  	tokenizer.tokenize(tokenList,whiteList);
  	String[] tokens = new String[tokenList.size()];
  	String[] whites = new String[whiteList.size()];
  	tokenList.toArray(tokens);
  	whiteList.toArray(whites);
  	int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);
  	int sentStartTok = 0;
  	int sentEndTok = 0;
  	for (int i = 0; i < sentenceBoundaries.length; ++i) {
  	    sentEndTok = sentenceBoundaries[i];
  	    Sentence sentence = new Sentence();
  	    for (int j=sentStartTok; j<=sentEndTok; j++) {  	    	
  	    	if (!STOPWORDS.containsKey(tokens[j])){
    	    	stemmer.setCurrent(tokens[j]);
    		  	stemmer.stem();
    		  	Word w = new Word(stemmer.getCurrent());
    		  	if (w.getContent().length() >= 3) sentence.addWord(w) ;   	    		
  	    	}

  	    }
  	    sentenceList.add(sentence);
  	    sentStartTok = sentEndTok+1;
  	}
		return sentenceList;
	}
	
	private static Hashtable<String, Boolean> loadStopWords(String filePath){
		Hashtable<String, Boolean> stopWords = new Hashtable<String, Boolean>();
		try{
			  FileInputStream fstream = new FileInputStream(filePath);
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  while ((strLine = br.readLine()) != null)   {
			  	stopWords.put(strLine.trim().replace("\n", ""), true) ;
			  }
			  in.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		return stopWords;
	}
	
	public static void main(String[] args) throws Exception {
		LingpipeParser parser = new LingpipeParser();
		ArrayList<Sentence> sentences = parser.parseText("Hello, this is test for and about extension");
		for (int i = 0; i < sentences.size(); i++) {
			for (int j = 0; j < sentences.get(i).getWords().size(); j++) {
				System.out.print(sentences.get(i).getWords().get(j).getContent()+ " ");
			} 
			System.out.println("");
		}
	}

}
