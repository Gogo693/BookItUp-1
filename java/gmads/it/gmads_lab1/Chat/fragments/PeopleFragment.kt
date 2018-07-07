package gmads.it.gmads_lab1.Chat.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import gmads.it.gmads_lab1.Chat.ChatActivity
import gmads.it.gmads_lab1.R
import gmads.it.gmads_lab1.constants.AppConstants
import gmads.it.gmads_lab1.Chat.item.PersonItem
import gmads.it.gmads_lab1.Chat.util.FirebaseChat
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity

class PeopleFragment : Fragment() {

    private lateinit var userListenerRegistration: ValueEventListener

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        userListenerRegistration =
                FirebaseChat.addUsersListener(this.activity!!, this::updateRecyclerView)

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseChat.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<PersonItem>) {

        fun init() {
            recycler_view_people.apply {
                if (this@PeopleFragment.context != null) {
                    layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                    adapter = GroupAdapter<ViewHolder>().apply {
                        peopleSection = Section(items)
                        add(peopleSection)
                        setOnItemClickListener(onItemClick)
                    }

                    shouldInitRecyclerView = false
                }
                //shouldInitRecyclerView = false
            }
        }

        fun updateItems() {

            items.sortedWith(compareBy(PersonItem::notificationNumber) )
            items as List<Item>
            peopleSection.update(items)
        }


        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            /*
            TODO: rendere questa stringa adatta per il java quando si cerca di chattare premendo il pulsante dalla attività del profilo del proprietario
             */
            startActivity<ChatActivity>(
                    AppConstants.USER_NAME to item.person.name,
                    AppConstants.USER_ID to item.userId
            )
        }
    }

}
