package api.database;

import api.configs.Config;
import api.configs.PROPERTY;
import api.dao.AccountDao;
import api.dao.UserDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

public final class DBRequest {
  private final String table;
  private final List<Condition> conditions;

  private DBRequest(String table, List<Condition> conditions) {
    this.table = table;
    this.conditions = conditions;
  }

  public static Builder builder() {
    return new Builder();
  }

  public UserDao extractUser() {
    return extract(DBRequest::mapUserRow);
  }

  public AccountDao extractAccount() {
    return extract(DBRequest::mapAccountRow);
  }

  private <T> T extract(RowMapper<T> mapper) {
    String sql = buildSelectSql();
    try (Connection connection = openConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      bindParameters(statement);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapper.map(resultSet);
        }
        return null;
      }
    } catch (SQLException error) {
      throw new RuntimeException("Database query failed: " + sql, error);
    }
  }

  private String buildSelectSql() {
    StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table);
    if (!conditions.isEmpty()) {
      sql.append(" WHERE ");
      for (int i = 0; i < conditions.size(); i++) {
        if (i > 0) {
          sql.append(" AND ");
        }
        Condition condition = conditions.get(i);
        sql.append(condition.getColumn())
            .append(' ')
            .append(condition.getOperator())
            .append(" ?");
      }
    }
    return sql.toString();
  }

  private void bindParameters(PreparedStatement statement) throws SQLException {
    for (int i = 0; i < conditions.size(); i++) {
      statement.setObject(i + 1, conditions.get(i).getValue());
    }
  }

  private static Connection openConnection() throws SQLException {
    return DriverManager.getConnection(
        Config.getProperty(PROPERTY.DB_URL),
        Config.getProperty(PROPERTY.DB_USERNAME),
        Config.getProperty(PROPERTY.DB_PASSWORD));
  }

  private static UserDao mapUserRow(ResultSet resultSet) throws SQLException {
    return UserDao.builder()
        .id(resultSet.getLong("id"))
        .username(resultSet.getString("username"))
        .password(resultSet.getString("password"))
        .role(resultSet.getString("role"))
        .name(resultSet.getString("name"))
        .build();
  }

  private static AccountDao mapAccountRow(ResultSet resultSet) throws SQLException {
    return AccountDao.builder()
        .id(resultSet.getLong("id"))
        .accountNumber(resultSet.getString("account_number"))
        .balance(resultSet.getDouble("balance"))
        .customerId(resultSet.getLong("customer_id"))
        .build();
  }

  @FunctionalInterface
  private interface RowMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
  }

  public static final class Builder {
    private String table;
    private final List<Condition> conditions = new ArrayList<>();

    public Builder table(String table) {
      this.table = table;
      return this;
    }

    public Builder condition(Condition condition) {
      this.conditions.add(condition);
      return this;
    }

    public DBRequest build() {
      return new DBRequest(table, List.copyOf(conditions));
    }
  }
}
