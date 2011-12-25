package edig.document.parser;

import java.util.ArrayList;

import edig.entites.Sentence;


public interface ParserIF {
	public ArrayList<Sentence> parseText(String text) throws Exception;
}
