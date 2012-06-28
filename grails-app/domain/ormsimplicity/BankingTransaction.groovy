package ormsimplicity

class BankingTransaction {

  Long transactionNumber
  BigDecimal amount
  Account fromAccount
  Account toAccount

  static constraints = {
    transactionNumber min: 0l, unique: true, nullable: false
    fromAccount nullable: false, validator: {Account thisFromAccount, BankingTransaction thisTransaction ->
      if (thisFromAccount?.accountNumber == thisTransaction?.toAccount?.accountNumber) {
        return false//cannot have a transaction to the same account... probably
      }
    }
    toAccount nullable: false
    amount nullable: false
  }

  BankingTransactionDTO toBankingTransactionDTO() {
    def dto = new BankingTransactionDTO()
    dto.with {//probably better ways of doing this but this illustrated the example
      transactionNumber = this.transactionNumber
      amount = this.amount
      fromCustomersName = fromAccount.owner.name
      toCustomersName = toAccount.owner.name
    }
    return dto
  }

}
