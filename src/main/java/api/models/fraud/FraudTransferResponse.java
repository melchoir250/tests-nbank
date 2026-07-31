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
public class FraudTransferResponse extends BaseModel {
  private String status;
  private String message;
  private long transactionId;
  private int senderAccountId;
  private int receiverAccountId;
  private double amount;
  private double fraudRiskScore;
  private String fraudReason;
  private boolean requiresVerification;
  private boolean requiresManualReview;
}
