package ormsimplicity

import org.junit.*
import groovy.sql.Sql
import org.hibernate.SessionFactory

class BankingTransactionTests {

  Sql sql
  SessionFactory sessionFactory
  BankingTransactionService bankingTransactionService

  @Before
  void setUp() {
    BankingTransaction.withNewSession {session ->
      100.times {num ->
        new Customer(name: "Customer ${num}", account: new Account(accountNumber: num)).save(failOnError: true)
      }
    }

    BankingTransaction.withNewSession {session ->
      final allCustomers = Customer.list()
      def generousCustomer = allCustomers[0]
      allCustomers[1..-1].eachWithIndex {customer, index ->
        new BankingTransaction(fromAccount: generousCustomer.account, toAccount: customer.account, transactionNumber: index, amount: index * 100).save(failOnError: true)
      }
    }
    sql = new Sql(sessionFactory.currentSession.connection())
    sql.metaClass.getNumberOfSelects = {->
      return delegate.rows('SHOW STATUS LIKE "Com_select"')[0].Value as Long
    }
  }

  @After
  void tearDown() {

  }

  @Test
  void testSimpleQuery() {
    //get the current number of selects in this session
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    final transaction = BankingTransaction.list(max: 1)//1 query

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest

    //is it what we expect??
    assert numberOfSelects == 1
  }

  @Test
  void testSimpleLazyPropertyAccess() {
    //get the current number of selects in this session
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    final transaction = BankingTransaction.list(max: 1)[0]//1 query
    def fromAccountNumber = transaction.fromAccount.accountNumber//1 query
    def fromAccountCustomerName = transaction.fromAccount.owner.name//1 query

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest

    //is it what we expect??
    assert numberOfSelects == 3
  }

  @Test
  void testSimpleEagerPropertyAccess() {
    //get the current number of selects in this session
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    final transaction = BankingTransaction.list([max: 1, fetch: [fromAccount: 'join', 'fromAccount.owner': 'join']])[0]//1 query
    def fromAccountNumber = transaction.fromAccount.accountNumber//0 query
    def fromAccountCustomerName = transaction.fromAccount.owner.name//0 query

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest

    //is it what we expect??
    assert numberOfSelects == 1
  }


  @Test
  void testLazyDtoCreation() {
    //get the current number of selects in this session
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    def dtos = bankingTransactionService.retrieveTransactionsLazily()

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest

    //is it what we expect??
    assert numberOfSelects == 201
  }


  @Test
  void testEagerDtoCreation() {
    //get the current number of selects in this session
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    def dtos = bankingTransactionService.retrieveTransactionsEagerly()

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest

    //is it what we expect??
    assert numberOfSelects == 1
  }

}
