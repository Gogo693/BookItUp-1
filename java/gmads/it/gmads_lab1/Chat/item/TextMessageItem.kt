package gmads.it.gmads_lab1.Chat.item

import android.content.Context
import gmads.it.gmads_lab1.R
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import gmads.it.gmads_lab1.Chat.model.TextMessage
import kotlinx.android.synthetic.main.item_text_message.*

class TextMessageItem(val message: TextMessage,
                      val context: Context)
    : MessageItem(message) {
    /*
       Qua si lega il messaggio in generale da cui derivano le altre
     */
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        super.bind(viewHolder, position)
    }

    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}