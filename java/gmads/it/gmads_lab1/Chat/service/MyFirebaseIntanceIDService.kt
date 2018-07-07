package gmads.it.gmads_lab1.Chat.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import gmads.it.gmads_lab1.Chat.util.FirebaseChat


class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
//in caso si aggiornasse il token
    override fun onTokenRefresh() {
        val newRegistrationToken = FirebaseInstanceId.getInstance().token
        Log.d("refresh",newRegistrationToken)
        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirebase(newRegistrationToken)
    }
//sotto classe di myFirebaseInstance
    companion object {
        fun addTokenToFirebase(newRegistrationToken: String?) {
            /*
            funzione richiamata sia se si iaggiorne sia che si crea il token
             */
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirebaseChat.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                FirebaseChat.setFCMRegistrationTokens(tokens)
            }
        }
    }
/*
arriva qua appena e solo dopo essersi registrato
 */
    fun addToken(newRegistrationToken: String?){
        Log.d("add",newRegistrationToken)
        addTokenToFirebase(newRegistrationToken);
    }
}