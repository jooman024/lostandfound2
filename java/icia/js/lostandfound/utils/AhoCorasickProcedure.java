package icia.js.lostandfound.utils;

import java.sql.Array;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;


public class AhoCorasickProcedure {
	 public static void searchKeywords(String text, List<String> keywords, ResultSet[] rs) throws SQLException {
        List<String> matches = AhoCorasick.searchKeywords(text, keywords);
        Array array = ((OracleConnection) DriverManager.getConnection("jdbc:default:connection"))
                .createOracleArray("MY_ARRAY", matches.toArray());
        ((OracleResultSet) rs[0]).updateArray(1, array);
    }

}
