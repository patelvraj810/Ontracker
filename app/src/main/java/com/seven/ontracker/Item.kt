package com.seven.ontracker

public class Item {
    var id: String? = null
    var itemName: String? = null
    var itemLocation: String? = null
    var itemDescription: String? = null
    var itemImage: String? = null

    constructor() {}

    constructor(itemId: String?, itemName: String?, itemLocation: String?, itemDescription: String?, itemImage: String?) {
        this.id = itemId
        this.itemName = itemName
        this.itemLocation = itemLocation
        this.itemDescription = itemDescription
        this.itemImage = itemImage
    }
}