package com.seven.ontracker

class QAForum {
    var id: String? = null
    var userName: String? = null
    var question: String? = null

    constructor() {}

    constructor(id: String, userName: String, question: String) {
        this.id = id
        this.userName = userName
        this.question = question
    }
}