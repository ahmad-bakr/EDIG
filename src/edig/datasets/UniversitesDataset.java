package edig.datasets;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import edig.entites.Document;

public class UniversitesDataset extends Dataset {
		public void prepareDataset(){
			String originalPath = "/media/disk/master/Master/datasets/webkb";
			String modifiedPath = "/media/disk/master/Master/datasets/four_universites";
			File originalDir = new File(originalPath);
	    String[] originalCategories = originalDir.list();
	    for (int i = 0; i < originalCategories.length; i++) {
	    	String orginalCategory = originalCategories[i];
				String subCategoryPath = originalPath+"/"+originalCategories[i];
	    	boolean createNewDir = (new File(modifiedPath+"/"+orginalCategory)).mkdirs();
	  		String[] docsInSubCategory = new File(subCategoryPath).list();
	  		for (int j = 0; j < docsInSubCategory.length; j++) {
					super.copyfile(subCategoryPath+"/"+docsInSubCategory[i], modifiedPath+"/"+orginalCategory+"/"+docsInSubCategory[i]);
				}
	    }
		}

		@Override
		public Hashtable<String, Document> loadDocuments() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Document getDocument(String documentID) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int numberOfDocuments() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ArrayList<String> getDocumentsIDS() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNumberOfDocumentsInClass(String className) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ArrayList<String> getOriginalClasses() {
			// TODO Auto-generated method stub
			return null;
		}
}
