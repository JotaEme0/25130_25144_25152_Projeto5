import java.sql.*;

public class ConectaBD {
        private static final String URL = "jdbc:sqlserver://regulus.cotuca.unicamp.br;databaseName=TI129M_PROJETO2_EQUIPE10;integratedSecurity=false;encrypt=true;trustServerCertificate=true";
        private static final String USER = "TI129M_PROJETO2_EQUIPE10";
        private static final String PASSWORD = "cotuca";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
}
