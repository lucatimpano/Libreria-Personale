package gestore_libreria.db;

import java.sql.*;

public class DatabaseConnectionSingleton {

    //Riutilizzo la stessa connessione al database, ed evito di aprire una connessione per ogni query che eseguo

    private static final String url = "jdbc:sqlite:Books_db.db";
    private static Connection instance;

    //costruttore privato
    private DatabaseConnectionSingleton(){}

    public static synchronized Connection getInstance() throws SQLException{
        if (instance == null || instance.isClosed()){
            instance = DriverManager.getConnection(url);
        }
        return instance;
    }
}
