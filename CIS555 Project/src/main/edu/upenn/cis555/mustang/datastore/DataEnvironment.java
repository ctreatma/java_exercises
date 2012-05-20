package edu.upenn.cis555.mustang.datastore;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class DataEnvironment {
	private Environment env;
	private EntityStore store;
	
	void setup(String envPath) {
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		envConfig.setReadOnly(false);
		storeConfig.setReadOnly(false);
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		storeConfig.setAllowCreate(true);
		storeConfig.setTransactional(true);
		env = new Environment(getEnvironmentDirectory(envPath), envConfig);
		store = new EntityStore(env, "EntityStore", storeConfig);
	}
	
	EntityStore getEntityStore() {
		return store;
	}
	
	Environment getEnvironment() {
		return env;
	}
	
	void shutdown() {
		if (store != null) {
			try {
				store.close();
			} catch(DatabaseException e) {
//				System.out.println("Error closing store: " + e);
			}
		}
		if (env != null) {
			try {
				env.close();
			} catch(DatabaseException e) {
//				System.out.println("Error closing environment: " + e);
			}
		}
	}
	
	private File getEnvironmentDirectory(String path) {
		File directory = new File(path);
		if (!directory.isDirectory()) {
			return directory.mkdirs() ? directory : null;
		}
		return directory;
	}
}
