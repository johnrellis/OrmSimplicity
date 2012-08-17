/***************************************************************
 * Copyright (c) 2012 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

package ormsimplicity

import org.junit.Before
import groovy.sql.Sql
import org.junit.Test
import org.hibernate.SessionFactory

/**
 * Comment Class
 * User: John
 * Date: 17/08/12
 * Time: 09:42
 */
class CustomerTests {

  Sql sql
  SessionFactory sessionFactory

  @Before
  void setUp() {
    BankingTransaction.withNewSession {session ->
      final customerOne = new Customer(name: "John", account: new Account(accountNumber: 123456)).save(failOnError: true)
      final customerTwo = new Customer(name: "Joe", account: new Account(accountNumber: 1234567)).save(failOnError: true)
      100.times {num ->
        def anotherCustomer = new Customer(name: "Customer ${num}", account: new Account(accountNumber: num)).save(failOnError: true)
        if (num % 2) {
          customerOne.addToFavourites(anotherCustomer)
        } else {
          customerTwo.addToFavourites(anotherCustomer)
        }
      }
      customerOne.save(failOnError: true)
      customerTwo.save(failOnError: true)
    }

    sql = new Sql(sessionFactory.currentSession.connection())
    sql.metaClass.getNumberOfSelects = {->
      return delegate.rows('SHOW STATUS LIKE "Com_select"')[0].Value as Long
    }
  }

  @Test
  void testNotRetrievingFavouritesCollection() {
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    def customer = Customer.findByName('John')//1 query

    //assert customer.favourites

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest
    //is it what we expect??
    assert numberOfSelects == 1
  }

  @Test
  void testRetrievingFavouritesCollection() {
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    def customer = Customer.findByName('John')//1 query

    assert customer.favourites// 1 query

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest
    //is it what we expect??
    assert numberOfSelects == 2//not sure
  }

  @Test
  void testRetrievingFavouritesLazily() {
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
    def customer = Customer.findByName('John')//1 query

    assert customer.favourites.name.size() == 50//1 query to get favourites + 50 individual queries to get name

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest
    //is it what we expect??
    assert numberOfSelects == 52
  }

  @Test
  void testRetrievingFavouritesEagerly() {
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
//    def customer = Customer.findByName('John', [lazy:[favourites:false]])//1 query
    def customer = Customer.findByName('John', [fetch: [favourites: 'join']])//1 query

    assert customer.favourites.name.size() == 50

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest
    //is it what we expect??
    assert numberOfSelects < 50
  }

  @Test
  void testRetrievingFavouritesEagerlyAgain() {
    def numberOfQueriesBeforeTest = sql.numberOfSelects

    //carry out the query
//    def customer = Customer.findByName('John', [lazy:[favourites:false]])//1 query
    def customer = Customer.findByName('John', [fetch: [favourites: 'select']])//1 query

    assert customer.favourites.name.size() == 50

    //now calculate the number of queries
    def numberOfQueriesAfterTest = sql.numberOfSelects
    final numberOfSelects = numberOfQueriesAfterTest - numberOfQueriesBeforeTest
    //is it what we expect??
    assert numberOfSelects < 50
  }

}
