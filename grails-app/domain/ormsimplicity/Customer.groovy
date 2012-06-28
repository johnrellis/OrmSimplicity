package ormsimplicity

class Customer {

  String name
  Account account

  static constraints = {
    account unique: true, nullable: false, blank: false
    name nullable: false, blank: false
  }
}
