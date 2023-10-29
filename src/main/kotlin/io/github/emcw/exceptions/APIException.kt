package io.github.emcw.exceptions

class APIException : Exception {
    constructor() : super("An unknown API exception has occurred.")
    constructor(message: String?) : super(message)
}