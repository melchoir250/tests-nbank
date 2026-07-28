package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $"),
    DEPOSIT_LIMIT_EXCEEDED("Please deposit less or equal to 5000$."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred $"),
    TRANSFER_AGAIN_SUCCESSFUL("✅ Transfer of $"),
    TRANSFER_LIMIT_EXCEEDED("Transfer amount cannot exceed 10000"),
    TRANSFER_CONFIRM_REQUIRED("Please fill all fields and confirm."),
    NAME_UPDATED_SUCCESSFULLY("Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS("Name must contain two words with letters only");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
