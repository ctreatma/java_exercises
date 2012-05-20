package edu.upenn.cis555.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  XPathEngineTest.class,
  XPathHandlerTest.class,
  XPathDBTest.class,
  XPathCrawlerTest.class
})

public class AllTests {
}
