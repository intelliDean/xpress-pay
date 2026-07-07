# Xpress-Pay Transaction Upgrade Plans

This document outlines the plans for upgrading the Xpress-Pay transaction engine by fully implementing the transaction lifecycle defined in `TransactionType`.

---

## 1. Integrate Wallet Balance Verification & Deduction
Currently, the `Customer` entity has a `balance` field, but it is not integrated with the checkout process.
* **Objective**: Ensure that a customer has enough balance to buy airtime before forwarding the request to the Xpress API.
* **Proposed Implementation**:
  ```java
  if (customer.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException("Insufficient wallet balance");
  }
  customer.setBalance(customer.getBalance().subtract(amount));
  customerRepository.save(customer);
  ```

---

## 2. Implement Wallet Funding (`FUND_WALLET`)
Allow users to add money to their digital wallets.
* **Endpoint**: `POST /api/v1/customer/wallet/fund`
* **Request DTO**:
  ```java
  public record FundWalletRequestDTO(
      Long userId,
      BigDecimal amount,
      String paymentGatewayReference
  ) {}
  ```
* **Workflow**:
  1. Receive the request and payment gateway reference.
  2. Verify the payment transaction with the gateway provider (e.g., Paystack/Flutterwave).
  3. Credit the `Customer.balance` by `amount`.
  4. Record a `Transaction` of type `FUND_WALLET`.

---

## 3. Implement Peer-to-Peer Transfer (`TRANSFER_MONEY`)
Allow customers to send money to other registered customers using their emails or phone numbers.
* **Endpoint**: `POST /api/v1/customer/wallet/transfer`
* **Request DTO**:
  ```java
  public record WalletTransferRequestDTO(
      Long senderUserId,
      String recipientEmailOrPhone,
      BigDecimal amount,
      String narration
  ) {}
  ```
* **Workflow**:
  1. Validate that the recipient customer exists.
  2. Ensure the sender has a sufficient balance (`sender.balance >= amount`).
  3. Deduct `amount` from the sender's balance and credit the recipient's balance within a `@Transactional` block.
  4. Create two `Transaction` records (a debit transaction for the sender and a credit transaction for the recipient) of type `TRANSFER_MONEY`.

---

## 4. Implement Money Withdrawal (`WITHDRAW_MONEY`)
Allow customers to withdraw their wallet balances to their external bank accounts.
* **Endpoint**: `POST /api/v1/customer/wallet/withdraw`
* **Request DTO**:
  ```java
  public record WithdrawRequestDTO(
      Long userId,
      BigDecimal amount,
      String accountNumber,
      String bankCode
  ) {}
  ```
* **Workflow**:
  1. Ensure the customer has sufficient balance.
  2. Deduct the amount from the customer's balance.
  3. Initiate a payout request to a payout gateway.
  4. Record a `Transaction` of type `WITHDRAW_MONEY`.

---

## 5. Enhance the Audit Trail (`Transaction` Entity)
To make the financial logs robust and audit-friendly:
* **Unique References**: Generate unique transaction references (e.g., `TXN-1234567890`) for tracking.
* **Narration/Description**: Add a `narration` field to store transaction remarks.
* **Balance Snapshotting**: Add `previousBalance` and `newBalance` fields to the `Transaction` entity to track historical balances.
