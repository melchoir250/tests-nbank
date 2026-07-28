package ui.pages;

import java.util.Locale;
import lombok.Getter;

@Getter
public enum TransactionType {
    TRANSFER_IN("TRANSFER_IN - $"),
    TRANSFER_OUT("TRANSFER_OUT - $");

    private final String prefix;

    TransactionType(String prefix) {
        this.prefix = prefix;
    }

    public String withAmount(double amount) {
        return prefix + String.format(Locale.US, "%.2f", amount);
    }
}
