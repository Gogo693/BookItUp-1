package gmads.it.gmads_lab1.Chat.model

import java.util.*

data class ImageMessage(val imagePath: String,
                        override val time: Date,
                        override val senderId: String,
                        override val type: String = MessageType.IMAGE)
    : Message {
    constructor() : this("", Date(0), "")
}
/*
classe per contenere i dati dell'immagine
 */