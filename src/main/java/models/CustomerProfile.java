package models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerProfile extends BaseModel {
  private int id;
  private String username;
  private String password;
  private String name;
  private String role;
  private List<Object> accounts;
}
