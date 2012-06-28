package ormsimplicity

class BankingTransactionService {

  Collection<BankingTransactionDTO> retrieveTransactionsLazily() {
    def transactions = BankingTransaction.list()
    return transactions.collect {it.toBankingTransactionDTO()}
  }

  Collection<BankingTransactionDTO> retrieveTransactionsEagerly() {
    final queryParams = [fetch: [
            fromAccount: 'join',
            toAccount: 'join',
            'toAccount.owner': 'join',
            'fromAccount.owner': 'join']
    ]
    def transactions = BankingTransaction.list(queryParams)
    return transactions.collect {it.toBankingTransactionDTO()}
  }

}
