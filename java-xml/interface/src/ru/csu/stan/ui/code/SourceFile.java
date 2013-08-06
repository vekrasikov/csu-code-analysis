package ru.csu.stan.ui.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс, определяющий файл исходного кода, в котором необходимо расставить якоря для навигации.
 * 
 * @author mz
 *
 */
public class SourceFile {
	
	private String filename;
	private List<Anchor> anchorList = new LinkedList<Anchor>();
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private SourceFile(String filename) {
		this.filename = filename;
	}
	
	static SourceFile getInstance(String filename){
		return new SourceFile(filename);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public List<Anchor> getAnchorList() {
		return anchorList;
	}
	
	public void addAnchor(Anchor anchor) {
		this.anchorList.add(anchor);
	}
	
	public void createAnchor(int line, int col, String name, String id){
		addAnchor(Anchor.createInstance(line, col, name, id));
	}
	
	public void processFile(File src, File dst) throws FileNotFoundException, IOException{
		if (anchorList.isEmpty())
			return;
		Collections.sort(anchorList, new AnchorComparator());
		BufferedReader reader = new BufferedReader(new FileReader(src));
		BufferedWriter writer = new BufferedWriter(new FileWriter(dst));
		String line;
		int lineNumber = 1;
		Iterator<Anchor> it = anchorList.iterator();
		Anchor anchor = it.next();
		while ((line = reader.readLine()) != null){
			while (anchor != null && anchor.getLine() == lineNumber){
				line = anchor.processLine(line);
				if (it.hasNext())
					anchor = it.next();
				else
					anchor = null;
			}
			writer.write(line);
			writer.write(LINE_SEPARATOR);
			lineNumber++;
		}
		reader.close();
		writer.close();
	}
	
}
