import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectaBD {
        private static final String URL =
                "jdbc:sqlserver://regulus.cotuca.unicamp.br:1433;databaseName=TI129M_PROJETO2_EQUIPE10"+
                        ";integratedSecurity=false;encrypt=true;trustServerCertificate=true";

        private static final String USER = "TI129M_PROJETO2_EQUIPE10";
        private static final String PASSWORD = "cotuca";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
}
