package edig.document.parser;

public class ParserFactory {

	public static ParserIF getInstance(String className){
		ParserIF parser = null;
		if (className.equalsIgnoreCase("lingpipe")){
			parser = new LingpipeParser();
		}
		return parser;
	}
}
