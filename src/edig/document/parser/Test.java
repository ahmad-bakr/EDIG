package edig.document.parser;

import java.util.ArrayList;
import java.util.List;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

public class Test {
  static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
  static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();
  public static void main(String[] args) {
  	String text = "Hello, This is a test. My name is Ahmad (Bakr) and I am Software Engineer";
  	System.out.println("INPUT TEXT: ");
  	System.out.println(text);
  	List<String> tokenList = new ArrayList<String>();
  	List<String> whiteList = new ArrayList<String>();
  	Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),0,text.length());
  	tokenizer.tokenize(tokenList,whiteList);

  	System.out.println(tokenList.size() + " TOKENS");
  	System.out.println(whiteList.size() + " WHITESPACES");

  	String[] tokens = new String[tokenList.size()];
  	String[] whites = new String[whiteList.size()];
  	tokenList.toArray(tokens);
  	whiteList.toArray(whites);
  	int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);

  	System.out.println(sentenceBoundaries.length 
  			   + " SENTENCE END TOKEN OFFSETS");
  	int sentStartTok = 0;
  	int sentEndTok = 0;
  	for (int i = 0; i < sentenceBoundaries.length; ++i) {
  	    sentEndTok = sentenceBoundaries[i];
  	    System.out.println("SENTENCE "+(i+1)+": ");
  	    for (int j=sentStartTok; j<=sentEndTok; j++) {
  		System.out.print(tokens[j]+whites[j+1]);
  	    }
  	    System.out.println();
  	    sentStartTok = sentEndTok+1;
  	}
   
	}
}
