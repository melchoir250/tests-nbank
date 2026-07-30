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
public class FraudCheckStatusResponse extends BaseModel {
  private String status;
  private long transactionId;
  private String note;
}
