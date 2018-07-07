package gmads.it.gmads_lab1.Chat

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import gmads.it.gmads_lab1.*
import gmads.it.gmads_lab1.constants.AppConstants
import gmads.it.gmads_lab1.Chat.fragments.PeopleFragment
import gmads.it.gmads_lab1.UserPackage.*
import gmads.it.gmads_lab1.HomePackage.*
import gmads.it.gmads_lab1.BookPackage.*
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibrary
import gmads.it.gmads_lab1.R.id.drawer_layout
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.chat_list.*
import gmads.it.gmads_lab1.RequestPackage.*
import java.io.File
import java.io.IOException
import java.util.*
class ChatList : AppCompatActivity() {
    internal var navName: TextView? =null
    internal var navMail: TextView? =null
    internal var navImage: ImageView? =null
    internal var navReqNotification : ImageView? = null
    private var navigationView: NavigationView? =null
    private var profile: Profile? = null
    private val myProfileBitImage: Bitmap? = null
    private var headerView: View? =null
    private var drawer: DrawerLayout? = null
    private var mProfileReference: DatabaseReference? = null
    private var mProfileListener: ValueEventListener? = null
/*
attivita per vedere la lista delle chat
 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        //prendo riferimento firebase dell'utente
        mProfileReference = FirebaseManagement.getDatabase()
                .reference
                .child("users")
                .child(FirebaseManagement.getUser().uid)
        mProfileReference?.keepSynced(true)
        //
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        setSupportActionBar(toolbarList)
        //supportActionBar?.title = "Chat"
    //setto la toobar
        supportActionBar?.title= "Chat"
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        setNavViews()
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        replaceFragment(PeopleFragment())

        mProfileReference?.child("chatNotified")?.setValue(false);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer?.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        //inserisco nel posto dedicato al fragment peoplefragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()
    }

    fun setNavViews() {

        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        headerView = navigationView?.getHeaderView(0)
        navName = headerView?.findViewById(R.id.navName)
        navMail = headerView?.findViewById(R.id.navMail)
        navImage = headerView?.findViewById(R.id.navImage)
        navReqNotification = headerView?.findViewById(R.id.req_notification)
        headerView?.setBackgroundResource(R.color.colorPrimaryDark)

        navigationView?.setNavigationItemSelectedListener{
            val id = it.itemId
            when (id) {
                R.id.nav_showProfile -> {
                    val intentMod = Intent(this, ShowProfile::class.java)
                    startActivity(intentMod)
                    finish()
                    true
                }
                R.id.nav_addBook -> {
                    val intentMod = Intent(this, AddBook::class.java)
                    startActivity(intentMod)
                    finish()
                    true
                }
                R.id.nav_home -> {
                    //deve solo chiudersi la navbar
                    //drawer.closeDrawers();
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_requests -> {
                    val intent = Intent(this, RequestActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_chat -> true
                R.id.nav_logout -> {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener { v ->
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_mylibrary -> {
                    startActivity(Intent(this, MyLibrary::class.java))
                    finish()
                    true
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        if (profile != null) {
            navName.apply { intent.getStringExtra(AppConstants.USER_NAME) }
            //navMail.setText(profile.getEmail())
            navMail.apply { intent.getStringExtra(profile?.getEmail()) }
            if (myProfileBitImage != null) {
                navImage?.setImageBitmap(myProfileBitImage)
            } else {
                navImage?.setImageDrawable(getDrawable(R.drawable.default_picture))
            }
        }
    }
    private fun getUserInfo() {
        val postListener= object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myuser = dataSnapshot.getValue(Profile::class.java)
                if (myuser != null) {
                    //dati navbar

                    navName?.text = myuser.getName()
                    navMail?.text = myuser.getEmail()
                    if (myuser.isReqNotified()) {
                        //per ricezione notifiche
                        val menu = navigationView?.getMenu()
                        val item = menu?.getItem(4)
                        item?.setIcon(R.drawable.ic_round_notification_important_24px)
                        //
                        //navReqNotification.setVisibility(View.VISIBLE);
                    } else {
                        val menu = navigationView?.getMenu()
                        val item = menu?.getItem(4)
                        //item.setIcon(R.drawable.ic_round_notifications_24px);
                        item?.setIcon(R.drawable.ic_round_notifications_24px)
                        //navReqNotification.setVisibility(View.GONE);
                    }
                    //setto foto
                    if (Objects.requireNonNull(myuser).getImage() != null) {
                        try {
                            val localFile = File.createTempFile("image", "jpg")
                            val profileImageRef = FirebaseManagement.getStorage().reference
                                    .child("users")
                                    .child(FirebaseManagement.getUser().uid)
                                    .child("profileimage.jpg")

                            profileImageRef.getFile(localFile)
                                    .addOnSuccessListener { _ -> navImage?.setImageBitmap(BitmapFactory.decodeFile(localFile.path)) }
                                    .addOnFailureListener { e -> Log.d("errore", e.toString()) }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    } else {//default image
                        navImage?.setImageDrawable(getDrawable(R.drawable.default_picture))
                    }
                } else {
                    navName?.text = getString(R.string.nameExample)
                    navMail?.text = getString(R.string.emailExample)
                    navImage?.setImageDrawable(getDrawable(R.drawable.default_picture))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        mProfileReference?.addValueEventListener(postListener)
        mProfileListener = postListener

    }

    override fun onStart() {
        super.onStart()
        getUserInfo()
    }

    fun onClickNotify(view: View) {
        val intentMod = Intent(applicationContext, RequestActivity::class.java)
        startActivity(intentMod)
        //finish();
    }
}
