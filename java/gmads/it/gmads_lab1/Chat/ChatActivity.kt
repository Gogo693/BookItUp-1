package gmads.it.gmads_lab1.Chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import gmads.it.gmads_lab1.Chat.glide.GlideApp
import gmads.it.gmads_lab1.constants.AppConstants
import gmads.it.gmads_lab1.Chat.model.ImageMessage
import gmads.it.gmads_lab1.Chat.model.MessageType
import gmads.it.gmads_lab1.Chat.model.TextMessage
import gmads.it.gmads_lab1.R
import gmads.it.gmads_lab1.Chat.util.FirebaseChat
import gmads.it.gmads_lab1.Chat.util.StorageUtil
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement
import gmads.it.gmads_lab1.UserPackage.Profile
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String

    private lateinit var messagesListenerRegistration: ValueEventListener
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section
    private val cw : Context = this
/*
attività usata per aprire la chat tra i due utenti
i due utenti vengono presi dall'intento
si crea il canale su cui entrambi fanno riferimento
si aggiunge un listener alla chat in caso si ricevano nuovi messaggi..in quel caso si aggiorna la recycle
image_view?
 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

    val otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        FirebaseChat.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                    FirebaseChat.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend =
                        TextMessage(editText_message.text.toString(), Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT)
                editText_message.setText("")
                if(messageToSend.text.isNotEmpty()) {
                    if (messageToSend.text.isNotBlank()) {
                        FirebaseChat.sendMessage(messageToSend, channelId)
                    }
                }
            }

            fab_send_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }

            FirebaseDatabase.getInstance().reference
                    .child("chatChannels")
                    .child(channelId)
                    .child("notificationNumber")
                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                    .setValue(0)
        }

    }
//ritorno dalla selezione dell'immagine
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                        ImageMessage(imagePath, Calendar.getInstance().time,
                                FirebaseAuth.getInstance().currentUser!!.uid)
                FirebaseChat.sendMessage(messageToSend, currentChannelId)
            }
        }
    }
//aggiorno il recycler
    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount - 1)
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount - 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}