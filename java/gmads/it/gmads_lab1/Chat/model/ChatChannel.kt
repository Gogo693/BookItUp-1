package gmads.it.gmads_lab1.Chat.model

data class ChatChannel(val userIds: MutableList<String>, val notificationNumber : Map<String, Int>) {

    constructor() : this(mutableListOf(), hashMapOf())

}
/*
e la lista dei messaggi...immagino che quando si scarica da firebase questa lista si riempa
 */