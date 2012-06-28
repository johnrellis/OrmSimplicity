package ormsimplicity

class Account {
  static belongsTo = [owner: Customer]

  Long accountNumber

  static constraints = {
    accountNumber min: 0l, nullable: false, unique: true
  }
}
