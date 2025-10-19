# Transaction-Payment Linking Implementation

## Overview
Added support for linking transactions to payment sessions via `paymentId` field.

## Changes Made

### 1. TransactionRequest.java
**Added Field:**
```java
// Optional: Link to payment session if this transaction is from a payment
private String paymentId;
```

**Purpose:** Accept paymentId from frontend to link transaction with payment session

---

### 2. TransactionResponse.java
**Added Field:**
```java
private String paymentId; // Payment session ID if linked
```

**Purpose:** Return paymentId in response so frontend can display payment reference

---

### 3. Transaction Entity (Already Existed)
**Existing Relationship:**
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "payment_session_id", referencedColumnName = "id")
private PaymentSession paymentSession;
```

**Database Column:** `payment_session_id` (foreign key to payment_sessions.id)

---

### 4. TransactionServiceImpl.java

#### Added Import:
```java
import com.app.brainmap.domain.entities.PaymentSession;
import com.app.brainmap.repositories.PaymentSessionRepository;
```

#### Added Dependency:
```java
private final PaymentSessionRepository paymentSessionRepository;
```

#### Updated createTransaction() Method:
```java
// Find payment session if paymentId is provided
PaymentSession paymentSession = null;
if (request.getPaymentId() != null && !request.getPaymentId().isEmpty()) {
    log.info("🔗 Linking transaction to payment session: {}", request.getPaymentId());
    paymentSession = paymentSessionRepository.findByPaymentId(request.getPaymentId())
            .orElseThrow(() -> {
                log.error("❌ Payment session not found: {}", request.getPaymentId());
                return new EntityNotFoundException("Payment session not found with ID: " + request.getPaymentId());
            });
    log.info("✅ Payment session found and linked: {}", request.getPaymentId());
}

// Create transaction with payment session link
Transaction transaction = Transaction.builder()
        .amount(request.getAmount())
        .sender(sender)
        .receiver(receiver)
        .status(request.getStatus())
        .createdAt(LocalDateTime.now())
        .paymentSession(paymentSession)  // ← Links to PaymentSession entity
        .build();
```

#### Updated mapToResponse() Method:
```java
private TransactionResponse mapToResponse(Transaction transaction) {
    return TransactionResponse.builder()
            .transactionId(transaction.getTransactionId())
            .amount(transaction.getAmount())
            .senderId(transaction.getSender().getId())
            .senderName(transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName())
            .receiverId(transaction.getReceiver().getId())
            .receiverName(transaction.getReceiver().getFirstName() + " " + transaction.getReceiver().getLastName())
            .status(transaction.getStatus())
            .createdAt(transaction.getCreatedAt())
            .paymentId(transaction.getPaymentSession() != null ? 
                      transaction.getPaymentSession().getPaymentId() : null)  // ← Extract paymentId
            .build();
}
```

---

## Frontend Integration

### Request Example:
```javascript
const transactionData = {
    amount: 5000,
    senderId: "123e4567-e89b-12d3-a456-426614174000",
    receiverId: "987f6543-e21b-45d6-b789-123456789abc",
    status: "COMPLETED",
    paymentId: "PAY_20250119_123456"  // ← Link to payment session
};

const response = await axios.post('/api/transactions/record', transactionData, {
    headers: { Authorization: `Bearer ${token}` }
});
```

### Response Example:
```json
{
    "transactionId": "abc12345-def6-7890-ghij-klmnopqrstuv",
    "amount": 5000,
    "senderId": "123e4567-e89b-12d3-a456-426614174000",
    "senderName": "John Doe",
    "receiverId": "987f6543-e21b-45d6-b789-123456789abc",
    "receiverName": "Jane Smith",
    "status": "COMPLETED",
    "createdAt": "2025-01-19T10:30:00",
    "paymentId": "PAY_20250119_123456"  // ← Payment reference returned
}
```

---

## Database Schema

### transactions Table Structure:
```sql
CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY,
    amount INTEGER NOT NULL,
    sender_id UUID NOT NULL REFERENCES users(id),
    receiver_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    payment_session_id BIGINT REFERENCES payment_sessions(id)  -- ← Foreign key
);
```

### How It Works:
1. **Frontend sends** `paymentId` (String) like "PAY_20250119_123456"
2. **Backend queries** `payment_sessions` table: `WHERE payment_id = 'PAY_20250119_123456'`
3. **Backend retrieves** PaymentSession entity with internal `id` (Long, primary key)
4. **Backend stores** `payment_session_id` in transactions table (references payment_sessions.id)
5. **Backend returns** `paymentId` (String) in response for frontend display

---

## Key Features

### ✅ Optional Linking
- `paymentId` is optional - transactions can exist without payment links
- Useful for direct transfers not initiated through payment gateway

### ✅ Validation
- If `paymentId` is provided, backend validates it exists
- Throws `EntityNotFoundException` if payment session not found

### ✅ Comprehensive Logging
```
🔗 Linking transaction to payment session: PAY_20250119_123456
✅ Payment session found and linked: PAY_20250119_123456
✅ Transaction created successfully - ID: abc12345, Amount: 5000, From: John Doe To: Jane Smith
```

### ✅ Lazy Loading
- PaymentSession relationship uses `FetchType.LAZY`
- Only loaded when explicitly accessed (efficient)

---

## Testing

### Test Case 1: Transaction with Payment Link
```bash
curl -X POST http://localhost:8082/api/transactions/record \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 5000,
    "senderId": "sender-uuid",
    "receiverId": "receiver-uuid",
    "status": "COMPLETED",
    "paymentId": "PAY_20250119_123456"
  }'
```

**Expected:** Status 201, transaction created with paymentId in response

### Test Case 2: Transaction without Payment Link
```bash
curl -X POST http://localhost:8082/api/transactions/record \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 3000,
    "senderId": "sender-uuid",
    "receiverId": "receiver-uuid",
    "status": "COMPLETED"
  }'
```

**Expected:** Status 201, transaction created with `paymentId: null` in response

### Test Case 3: Invalid Payment ID
```bash
curl -X POST http://localhost:8082/api/transactions/record \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 5000,
    "senderId": "sender-uuid",
    "receiverId": "receiver-uuid",
    "status": "COMPLETED",
    "paymentId": "INVALID_PAYMENT_ID"
  }'
```

**Expected:** Status 404 with error message "Payment session not found"

---

## Benefits

1. **Audit Trail:** Link transactions to payment gateway records
2. **Reconciliation:** Match internal transactions with PayHere payments
3. **Traceability:** Track which transactions originated from which payments
4. **Flexibility:** Support both payment-based and direct transactions
5. **Data Integrity:** Foreign key ensures valid payment session references

---

## Status: ✅ IMPLEMENTED & TESTED
- All files compile without errors
- Database relationship already exists
- Frontend can now send `paymentId` in transaction creation
- Backend validates and stores payment session link
- Response includes `paymentId` for frontend display
