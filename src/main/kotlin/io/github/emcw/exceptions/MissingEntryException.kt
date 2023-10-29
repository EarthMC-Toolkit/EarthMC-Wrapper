package io.github.emcw.exceptions

class MissingEntryException : Exception {
    constructor() : super("No entries found! Make sure the map is properly populated.")
    constructor(message: String?) : super(message)
}