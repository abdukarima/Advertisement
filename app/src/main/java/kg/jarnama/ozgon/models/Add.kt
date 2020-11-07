package kg.jarnama.ozgon.models

import java.io.Serializable

class Add: Serializable {
    var title: String = ""
    var body: String = ""
    var price: String = ""
    var image: String = ""
    var timestamp: Int = 0

    constructor(title: String, body: String, price: String, image: String, timestamp: Int) {
        this.title = title
        this.body = body
        this.price = price
        this.image = image
        this.timestamp = timestamp
    }
}