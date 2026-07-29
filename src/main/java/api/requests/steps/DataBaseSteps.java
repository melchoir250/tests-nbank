package api.requests.steps;

import api.configs.Config;
import api.configs.PROPERTY;
import api.database.Condition;
import api.database.DBRequest;
import api.dao.AccountDao;
import api.dao.UserDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DataBaseSteps {
  private DataBaseSteps() {
  }

  public static UserDao getUserByUsername(String username) {
    return DBRequest.builder()
        .table("customers")
        .condition(Condition.equalTo("username", username))
        .build()
        .extractUser();
  }

  public static AccountDao getAccountById(long accountId) {
    return DBRequest.builder()
        .table("accounts")
        .condition(Condition.equalTo("id", accountId))
        .build()
        .extractAccount();
  }

  public static AccountDao getAccountByAccountNumber(String accountNumber) {
    return DBRequest.builder()
        .table("accounts")
        .condition(Condition.equalTo("account_number", accountNumber))
        .build()
        .extractAccount();
  }

  public static long countTransactions(long accountId) {
    String sql = "SELECT COUNT(*) FROM transactions WHERE account_id = ?";
    try (Connection connection = openConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, accountId);
      try (ResultSet resultSet = statement.executeQuery()) {
        resultSet.next();
        return resultSet.getLong(1);
      }
    } catch (SQLException error) {
      throw new RuntimeException("Failed to count transactions for account " + accountId, error);
    }
  }

  private static Connection openConnection() throws SQLException {
    return DriverManager.getConnection(
        Config.getProperty(PROPERTY.DB_URL),
        Config.getProperty(PROPERTY.DB_USERNAME),
        Config.getProperty(PROPERTY.DB_PASSWORD));
  }
}
