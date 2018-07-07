package gmads.it.gmads_lab1.Chat.item;

import android.content.Context
import android.view.View
import gmads.it.gmads_lab1.R
import gmads.it.gmads_lab1.Chat.glide.*
import gmads.it.gmads_lab1.UserPackage.Profile
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*


class PersonItem(val person: Profile,
                 val userId: String,
                 val notificationNumber : Int,
                 private val context: Context)
        : Item() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.textView_name.text = person.name
            if(notificationNumber != 0) {
                viewHolder.number.text = notificationNumber.toString()
                viewHolder.number.visibility = View.VISIBLE
            }
            if(notificationNumber == 0){
                viewHolder.number.visibility = View.GONE
            }
        //viewHolder.textView_bio.text = person.description
        if (person.image != null)
                GlideApp.with(context)
                .load(person.image)
                .placeholder(R.drawable.default_picture)
                .into(viewHolder.imageView_profile_picture)
        else
                GlideApp.with(context)
                .load(R.drawable.default_picture)
                .placeholder(R.drawable.default_picture)
                .into(viewHolder.imageView_profile_picture)
        }

        override fun getLayout() = R.layout.item_person
        }