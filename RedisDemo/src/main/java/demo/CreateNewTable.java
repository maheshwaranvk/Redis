package demo;

public class CreateNewTable {

	public static void main(String[] args) {
		RedisManager rm = new RedisManager();
		
		rm.createTable("LeadIds");
		
		rm.close();

	}

}
