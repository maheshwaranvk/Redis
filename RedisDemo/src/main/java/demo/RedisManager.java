package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisManager {
	private Jedis jedis;

	public RedisManager() {
		this.jedis = new Jedis("localhost",6379);
	}
	
	public RedisManager(String host, int port) {
		this.jedis = new Jedis(host, port);
		
	}

	// Create only when it is not available before
	public void createTable(String tableName) {
		jedis.sadd(tableName, "");
	}

	// Get the count of records in the table for the queue column
	public Long getRecordCount(String tableName) {
		return jedis.scard(tableName);
	}

	// Get all the records in the table
	public Set<String> getAllRecords(String tableName) {
		return jedis.smembers(tableName);
	}

	// Add only if the record does not exist
	public synchronized void pushToTable(String tableName, String data) {
		jedis.sadd(tableName, data);
	}

	// Remove an element from the Set and return it
	public synchronized String popFromTable(String tableName) {
		return jedis.spop(tableName);
	}

	// Get the random record in the table
	public String getRandomRecordFromHash(String dataStore) {

		// Get a random key from the hash
		Set<String> keys = jedis.hkeys(dataStore);
		return new ArrayList<>(keys).get((int) 
				(Math.random() * keys.size()));

	}

	// Get the top record in the table
	public String getUnusedRecordFromHash(String dataStore, String testName) {

		String key = null;

		// Get the keys from the hash
		List<String> keys = new ArrayList<>(jedis.hkeys(dataStore));

		// Iterator through the records
		for (int i = 0; i < keys.size(); i++) {

			// Get the key
			String nextKey = keys.get(i);

			// Get the value associated with the first key
			String value = jedis.hget(dataStore, nextKey);

			if(!value.contains(testName)) {
				jedis.hset(dataStore, nextKey, value+testName+",");
				value = jedis.hget(dataStore, nextKey);

				key = nextKey;
				break;
			}
		}
		return key;
	}

	// Add only if the record does not exist
	public synchronized void pushToHash(String dataStore, String data) {
		jedis.hset(dataStore, data, "");
	}

	// Remove an element from the Set and return it
	public synchronized String popFromHash(String dataStore) {

		// Get the first key from the hash
		String firstKey = jedis.hkeys(dataStore).iterator().next();

		// Remove the first key-value pair from the hash
		jedis.hdel(dataStore, firstKey);

		return firstKey;
	}

	// Add to the list
	public synchronized void pushToList(String dataStore, String data) {
		jedis.rpush(dataStore, data);
	}

	// Remove an element from the List and return it
	public synchronized String popFromList(String dataStore) {

		// Get the first key from the list
		return jedis.rpop(dataStore);
	}

	public void close() {
		jedis.close();
	}

}
