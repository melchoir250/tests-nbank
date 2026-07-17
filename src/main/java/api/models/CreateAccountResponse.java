package api.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateAccountResponse extends BaseModel {
  private int id;
  private String accountNumber;
  private double balance;
  private List<Object> transactions;
}

