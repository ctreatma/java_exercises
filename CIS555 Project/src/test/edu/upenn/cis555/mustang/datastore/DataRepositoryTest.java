package edu.upenn.cis555.mustang.datastore;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import edu.upenn.cis555.mustang.crawl.Document;
import edu.upenn.cis555.mustang.index.HitList;
import edu.upenn.cis555.mustang.index.HitListEntry;

public class DataRepositoryTest extends TestCase {
	private static final String TEST_DATA = "target/testdata";
	private static final String URL_1 = "http://foo.com";
	private static final String PAGE_1 = "<html><head><title>Foo</title></head><body><h2>Foo</h2><div>Foo Test</div></body></html>";
	private static final String URL_2 = "http://bar.com";
	private static final String PAGE_2 = "<html><head><title>Bar</title></head><body><h2>Bar</h2><div>Bar Test</div></body></html>";
	private static final String DOC_ID_1 = "123456";
	private static final String DOC_ID_2 = "abcdef";
	private static final int WORD_ID_1 = 12345;
	private static final int WORD_ID_2 = 67890;
	
	private DataRepository dataRepo;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
    	dataRepo = new DataRepository(TEST_DATA);
    	dataRepo.setup();
	}

	public void testDocumentIndex() {
		Document document1 = new Document();
		document1.setUrl(URL_1);
		document1.setPage(PAGE_1);
		document1.setSize(PAGE_1.length());
		Document document2 = new Document();
		document2.setUrl(URL_2);
		document2.setPage(PAGE_2);
		document2.setSize(PAGE_2.length());
		dataRepo.createDocumentIndex(DOC_ID_1, document1);
		dataRepo.createDocumentIndex(DOC_ID_2, document2);
    	List<DocumentIndex> documentIndices = dataRepo.getDocumentIndices();
    	assertEquals(documentIndices.size(), 2);
    	DocumentIndex documentIndex = dataRepo.getDocumentIndex(DOC_ID_1); 
    	assertEquals(documentIndex.getContent(), PAGE_1);
    	assertEquals(documentIndex.getSize(), PAGE_1.length());
    	assertEquals(documentIndex.getUrl(), URL_1);
	}

	public void testForwardIndex() {
		HitListEntry hitList = new HitListEntry();
		hitList.setPosition(1);
		dataRepo.createForwardIndex(DOC_ID_1, WORD_ID_1, hitList);
		hitList = new HitListEntry();
		hitList.setPosition(2);
		dataRepo.createForwardIndex(DOC_ID_2, WORD_ID_2, hitList);
    	List<ForwardIndex> forwardIndices = dataRepo.getForwardIndices();
    	assertEquals(forwardIndices.size(), 2);
    	ForwardIndex forwardIndex = dataRepo.getForwardIndex(DOC_ID_2); 
    	assertEquals(forwardIndex.getDocId(), DOC_ID_2);
    	assertTrue(forwardIndex.getPositionHitList().get(WORD_ID_2).contains(2));
	}

	public void testInvertedIndex() {
		HitList hitList = new HitList();
		dataRepo.createInvertedIndex(WORD_ID_1, DOC_ID_1, hitList);
		hitList = new HitList();
		dataRepo.createInvertedIndex(WORD_ID_2, DOC_ID_2, hitList);
    	List<InvertedIndex> invertedIndices = dataRepo.getInvertedIndices();
    	assertEquals(invertedIndices.size(), 2);
    	InvertedIndex invertedIndex = dataRepo.getInvertedIndex(WORD_ID_2); 
    	assertEquals(invertedIndex.getWordId(), WORD_ID_2);
    	assertTrue(invertedIndex.getPositionHitList().get(DOC_ID_2).isEmpty());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
    	dataRepo.shutdown();
    	deleteTestData();
	}

	private void deleteTestData() {
		File directory = new File(TEST_DATA);
    	File[] files = directory.listFiles();
    	for (File file : files) {
    		file.delete();
    	}
    	directory.delete();
	}
}
