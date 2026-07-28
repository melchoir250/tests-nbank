package api.configs;

public enum PROPERTY {
  SERVER("server"),
  API_VERSION("apiVersion"),
  ADMIN_USERNAME("admin.username"),
  ADMIN_PASSWORD("admin.password");

  private final String key;

  PROPERTY(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
