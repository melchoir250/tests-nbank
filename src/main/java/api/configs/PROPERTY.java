package api.configs;

public enum PROPERTY {
  SERVER("server"),
  API_VERSION("apiVersion"),
  API_CONTRACT_VERSION("api.contractVersion"),
  ADMIN_USERNAME("admin.username"),
  ADMIN_PASSWORD("admin.password"),
  DB_URL("db.url"),
  DB_USERNAME("db.username"),
  DB_PASSWORD("db.password");

  private final String key;

  PROPERTY(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
