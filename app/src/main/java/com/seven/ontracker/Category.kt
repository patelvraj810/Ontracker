package com.seven.ontracker;

public class Category {
    var id: String? = null
    var categoryName: String? = null
    var categoryImage: String? = null

    constructor() {}

    constructor(id: String?, categoryName: String?, categoryImage: String?) {
        this.id = id
        this.categoryName = categoryName
        this.categoryImage = categoryImage
    }
}

