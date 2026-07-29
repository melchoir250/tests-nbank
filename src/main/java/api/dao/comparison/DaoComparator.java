package api.dao.comparison;

import java.lang.reflect.Field;
import java.util.Map;
import org.assertj.core.data.Offset;
import org.assertj.core.api.Assertions;

public final class DaoComparator {
  private final DaoComparisonConfigLoader configLoader;

  public DaoComparator() {
    this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
  }

  public void compare(Object apiResponse, Object dao) {
    DaoComparisonConfigLoader.DaoComparisonRule rule = configLoader.getRuleFor(apiResponse.getClass());
    if (rule == null) {
      throw new RuntimeException("No comparison rule found for " + apiResponse.getClass().getSimpleName());
    }

    for (Map.Entry<String, String> mapping : rule.getFieldMappings().entrySet()) {
      Object apiValue = getFieldValue(apiResponse, mapping.getKey());
      Object daoValue = getFieldValue(dao, mapping.getValue());
      if (apiValue instanceof Number apiNumber && daoValue instanceof Number daoNumber) {
        Assertions.assertThat(apiNumber.doubleValue())
            .as("Field %s", mapping.getKey())
            .isCloseTo(daoNumber.doubleValue(), Offset.offset(0.001));
      } else if (!java.util.Objects.equals(apiValue, daoValue)) {
        throw new AssertionError(String.format(
            "Field mismatch for %s: API=%s, DAO=%s",
            mapping.getKey(),
            apiValue,
            daoValue));
      }
    }
  }

  private Object getFieldValue(Object object, String fieldName) {
    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException error) {
      throw new RuntimeException("Failed to get field value: " + fieldName, error);
    }
  }
}
