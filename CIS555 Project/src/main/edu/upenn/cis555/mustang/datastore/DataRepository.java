package edu.upenn.cis555.mustang.datastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis555.mustang.crawl.Document;
import edu.upenn.cis555.mustang.index.HitList;
import edu.upenn.cis555.mustang.index.HitListEntry;

public class DataRepository {
	private String path;
	private DataEnvironment env;
	private DataAccessor accessor;
	
	public DataRepository(String path) {
		this.path = path;
		env = new DataEnvironment();
		setup();
	}
	
	void setup() {
		env.setup(path);
		accessor = new DataAccessor(env.getEntityStore());
	}
	
	void shutdown() {
		env.shutdown();
	}

	public void createDocumentIndex(String docId, Document document) {
		DocumentIndex documentIndex = accessor.documentById.get(docId);
		if (documentIndex == null) {
			documentIndex = new DocumentIndex();
		}
		documentIndex.setDocId(docId);
		documentIndex.setContent(document.getPage());
		documentIndex.setUrl(document.getUrl());
		documentIndex.setSize(document.getSize());
        accessor.documentById.put(documentIndex);
		accessor.forwardIndexByDocId.delete(docId);		
	}

    public void updateDocumentIndex(String docId, double pageRank) {
        DocumentIndex documentIndex = accessor.documentById.get(docId);
        if (documentIndex == null) {
            return; // If PageRank is for a document we didn't index, do nothing.
        }
        documentIndex.setPageRank(pageRank);
        accessor.documentById.put(documentIndex);    
    }
	
	public void createLexiconEntry(int wordId, String word) {
		LexiconEntry lexiconEntry = accessor.lexiconByWordId.get(wordId);
		if (lexiconEntry == null) {
			lexiconEntry = new LexiconEntry();
			lexiconEntry.setWordId(wordId);
			lexiconEntry.setWordName(word);
			accessor.lexiconByWordId.put(lexiconEntry);
		}
	}
	
	public void createForwardIndex(String docId, int wordId, HitListEntry hitList) {
		ForwardIndex forwardIndex = accessor.forwardIndexByDocId.get(docId);
		if (forwardIndex == null) {
			forwardIndex = new ForwardIndex();
		}
		forwardIndex.setDocId(docId);
		forwardIndex.occur(wordId, hitList);
        accessor.forwardIndexByDocId.put(forwardIndex);
	}
/*
	public void createInvertedIndex(int wordId, String docId, HitListEntry hitList) {
		InvertedIndex invertedIndex = accessor.invertedIndexByWordId.get(wordId);
		if (invertedIndex == null) {
			invertedIndex = new InvertedIndex();
		} else {
			if (invertedIndex.getPositionHitList().containsKey(docId)) {
				accessor.invertedIndexByWordId.delete(wordId);
			}
		}
		invertedIndex.setWordId(wordId);
		if (hitList != null) {
			invertedIndex.occur(docId, hitList);
		} else {
			invertedIndex.reset(docId);
		}
        accessor.invertedIndexByWordId.put(invertedIndex);
	}
*/
	public void createInvertedIndex(int wordId, String docId, HitList hitList) {
		InvertedIndex invertedIndex = accessor.invertedIndexByWordId.get(wordId);
		if (invertedIndex == null) {
			invertedIndex = new InvertedIndex();
		} else if (invertedIndex.getPositionHitList().containsKey(docId)) {
			accessor.invertedIndexByWordId.delete(wordId);
		}
		invertedIndex.setWordId(wordId);
		invertedIndex.occur(docId, hitList);
        accessor.invertedIndexByWordId.put(invertedIndex);
	}
	
	List<DocumentIndex> getDocumentIndices() {
		List<DocumentIndex> indices = new ArrayList<DocumentIndex>();
		EntityCursor<DocumentIndex> entities = accessor.documentById.entities();
		for (DocumentIndex index : entities) {
			indices.add(index);
		}
		entities.close();
		return indices;
	}

	public DocumentIndex getDocumentIndex(String docId) {
	    DocumentIndex index = accessor.documentById.get(docId);
	    return index;
	}
	
	public List<InvertedIndex> getInvertedIndices() {
		List<InvertedIndex> indices = new ArrayList<InvertedIndex>();
		EntityCursor<InvertedIndex> entities = accessor.invertedIndexByWordId.entities();
		for (InvertedIndex index : entities) {
			indices.add(index);
		}
		entities.close();
		return indices;
	}

	public InvertedIndex getInvertedIndex(int wordId) {
		InvertedIndex index = accessor.invertedIndexByWordId.get(new Integer(wordId));
		return index;
	}
	
	public List<ForwardIndex> getForwardIndices() {
		List<ForwardIndex> indices = new ArrayList<ForwardIndex>();
		EntityCursor<ForwardIndex> entities = accessor.forwardIndexByDocId.entities();
		for (ForwardIndex index : entities) {
			indices.add(index);
		}
		entities.close();
		return indices;
	}

	public ForwardIndex getForwardIndex(String docId) {
		ForwardIndex index = accessor.forwardIndexByDocId.get(docId);
		return index;
	}
	
	public List<LexiconEntry> getLexiconEntries() {
		List<LexiconEntry> entries = new ArrayList<LexiconEntry>();
		EntityCursor<LexiconEntry> entities = accessor.lexiconByWordId.entities();
		for (LexiconEntry index : entities) {
			entries.add(index);
		}
		entities.close();
		return entries;
	}
	
	public int getDocumentCount() {
		int count = 0;
		EntityCursor<ForwardIndex> entities = accessor.forwardIndexByDocId.entities();
		for (Iterator<ForwardIndex> iterator = entities.iterator(); iterator.hasNext(); iterator.next()) { 
			count++;
		}
		entities.close();
		return count;
	}
	
    public static void main(String[] args) {
    	DataRepository dataRepo = new DataRepository("target/datastore");
//    	dataRepo.setup();

    	System.out.println("Document Index...");
    	List<DocumentIndex> documentIndices = dataRepo.getDocumentIndices();
    	for (DocumentIndex index : documentIndices) {
    		System.out.println(index.getDocId() + " - " + index.getUrl() + "\n" + index.getContent());
    	}

    	System.out.println("\nLexicon...");
    	List<LexiconEntry> lexiconEntries = dataRepo.getLexiconEntries();
    	for (LexiconEntry entry : lexiconEntries) {
    		System.out.println(entry.getWordId() + " - " + entry.getWordName());
    	}
    	
    	StringBuilder buffer = new StringBuilder();
    	System.out.println("\nInverted Index...");
    	List<InvertedIndex> invertedIndices = dataRepo.getInvertedIndices();
    	for (InvertedIndex index : invertedIndices) {
    		Map<String, List<Integer>> hitList = index.getPositionHitList();
    		System.out.println(index.getWordId());
    		buffer.delete(0, buffer.length());
    		for (Map.Entry<String, List<Integer>> entry : hitList.entrySet()) {
    			buffer.append(entry.getKey() + " - ");
    			List<Integer> occurrences = entry.getValue();
    			for (Integer position : occurrences) {
    				buffer.append(position).append(", ");
    			}
    		}
    		System.out.println(buffer);
    	}

    	System.out.println("\nForward Index...");
    	List<ForwardIndex> forwardIndices = dataRepo.getForwardIndices();
    	for (ForwardIndex index : forwardIndices) {
    		Map<Integer, List<Integer>> hitList = index.getPositionHitList();
    		System.out.println(index.getDocId());
    		buffer.delete(0, buffer.length());
    		for (Map.Entry<Integer, List<Integer>> entry : hitList.entrySet()) {
    			buffer.append(entry.getKey() + " - ");
    			List<Integer> occurrences = entry.getValue();
    			for (Integer position : occurrences) {
    				buffer.append(position).append(", ");
    			}
    		}
    		System.out.println(buffer);
    	}
    	
    	System.out.println("\nDocument Count...");
    	int docs = dataRepo.getDocumentCount();
    	System.out.println(docs);
    	
    	dataRepo.shutdown();
    }
}
