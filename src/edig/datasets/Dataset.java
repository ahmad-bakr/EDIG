package edig.datasets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Dataset implements DatasetLoader{
	
	protected void createDataset(String originalDatasetPath, String modifiedDatasetPath , int numberOfDocsPerCat){
		File originalDir = new File(originalDatasetPath);
    String[] originalCategories = originalDir.list();
    for (int i = 0; i < originalCategories.length; i++) {
    	String categoryName = originalCategories[i];
    	boolean createNewDir = (new File(modifiedDatasetPath+"/"+categoryName)).mkdirs();
  		String[] docsInCategory = new File(originalDatasetPath+"/"+categoryName).list();
    	for (int j = 0; j < numberOfDocsPerCat; j++) {
    		String documentName = docsInCategory[j];
    		copyfile(originalDatasetPath+"/"+categoryName+"/"+documentName, modifiedDatasetPath+"/"+categoryName+"/"+documentName);
    	}
    }
	}
	
	protected void copyfile(String srFile, String dtFile){
		try{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
			
			//For Append the file.
			//OutputStream out = new FileOutputStream(f2,true);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		}
		catch(FileNotFoundException ex){
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		}
		catch(IOException e){
			System.out.println(e.getMessage());			
		}
	}


}
