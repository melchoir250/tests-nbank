package api.models.fraud;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudTransferRequest extends BaseModel {
  private int senderAccountId;
  private int receiverAccountId;
  private double amount;
  private String description;
}
