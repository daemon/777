package net.rocketeer.sevens;

import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.database.PlayerQueryThroughputTest;
import net.rocketeer.sevens.player.MySqlPlayerDatabase;
import net.rocketeer.sevens.player.PlayerDatabase;

public class Tester {
  public static void main(String[] args) throws Exception {
    String username = "username";
    String password = "password";
    String url = "jdbc:mysql://127.0.0.1:3306/database";
    DatabaseManager manager = new DatabaseManager(url, username, password);
    System.out.println("Connecting...");
    manager.initDatabase();
    PlayerDatabase database = new MySqlPlayerDatabase(manager);
    (new PlayerQueryThroughputTest(database, 20000)).run();
  }
}
