package ormsimplicity

class Customer {

  static hasMany = [favourites : Customer]

  String name
  Account account

  static constraints = {
    account unique: true, nullable: false, blank: false
    name nullable: false, blank: false
  }
}
