package com.svbackend.natai.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.svbackend.natai.Greeting
import com.svbackend.natai.android.databinding.ActivityMainBinding
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.model.REMINDER_ID
import com.svbackend.natai.android.service.AlarmReceiver
import com.svbackend.natai.android.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : ScopedActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var account: Auth0

    private val viewModel by viewModels<NoteViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()

        val prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)

        account = (application as DiaryApplication).appContainer.auth0

        binding.addNoteBtn.setOnClickListener {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivity(intent)

//            launch {
//                viewModel.repository.insert(
//                    Note(
//                        title = UUID.randomUUID().toString(),
//                        content = "Some dummy note content",
//                    )
//                )
//            }
        }

        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            WebAuthProvider.login(account)
                .withScheme("natai")
                .withScope("openid profile email")
                // Launch the authentication passing the callback where the results will be received
                .start(this, object : Callback<Credentials, AuthenticationException> {
                    // Called when there is an authentication failure
                    override fun onFailure(error: AuthenticationException) {
                        // Something went wrong!
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Login Error: \n${error.message}",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    }

                    // Called when authentication completed successfully
                    override fun onSuccess(result: Credentials) {
                        // Get the access token from the credentials object.
                        // This can be used to call APIs
                        val accessToken = result.accessToken
                        val idToken = result.idToken
                        showUserProfile(accessToken)
                        with(prefs.edit()) {
                            putString("access_token", accessToken)
                            putString("id_token", idToken)
                            apply()
                        }
                    }
                })
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }


        val storedAccessToken = prefs.getString("access_token", null)

        if (null != storedAccessToken) {
            showUserProfile(storedAccessToken)
        }

        addReminder()
        loadNotes()
    }

    private fun loadNotes() = launch {
        viewModel.repository.loadNotes()

        val onClick = OnClickListener<Note> {
            val intent = Intent(this@MainActivity, NoteDetailsActivity::class.java).apply {
                putExtra(PARAM_NOTE_ID, it.id)
            }
            startActivity(intent)
        }

        viewModel.notes.collect { notes ->
            viewManager = GridLayoutManager(application, 1)
            viewAdapter = NoteAdapter(notes, onClick)

            println("=========Collect called============")

            recyclerView = (findViewById<RecyclerView>(R.id.NotesRecyclerView)).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter
            }

            return@collect
        }
    }

    private fun addReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //val reminderService = ReminderService()
        val reminderReceiverIntent = Intent(this, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("reminderId", REMINDER_ID)
        //reminderReceiverIntent.putExtra("isServiceRunning", isServiceRunning(reminderService))
        val pendingIntent =
            PendingIntent.getBroadcast(this, REMINDER_ID.toInt(), reminderReceiverIntent, 0)

        val date: Calendar = Calendar.getInstance()
        println("Current Date and TIme : " + date.getTime())
        val timeInSecs: Long = date.getTimeInMillis()
        val afterAdding1Min = Date(timeInSecs + 10 * 1000)
        println("After adding 1 min : $afterAdding1Min")

        val formattedDate = formatDate(afterAdding1Min.time, "dd/MM/YYYY HH:mm")

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, afterAdding1Min.time, pendingIntent
        )

        Toast.makeText(this, "Alarm is set at : $formattedDate", Toast.LENGTH_SHORT).show()
        //finish()
    }

    private fun formatDate(timeInMillis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(timeInMillis)
    }

    private fun showUserProfile(accessToken: String) {
        val client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                    Toast.makeText(
                        this@MainActivity,
                        "Error getting profile \n${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onSuccess(result: UserProfile) {
                    // We have the user's profile!
                    binding.nameTv.text = result.name
                    binding.emailTv.text = result.email
                    Toast.makeText(
                        this@MainActivity,
                        "Login Successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("natai")
            .start(
                this,
                object : Callback<Void?, AuthenticationException> {
                    override fun onSuccess(result: Void?) {
                        // The user has been logged out!
                        Toast.makeText(
                            this@MainActivity,
                            "Successfully logged out!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.nameTv.text = resources.getString(R.string.john_doe)
                        binding.emailTv.text = resources.getString(R.string.email)
                    }

                    override fun onFailure(error: AuthenticationException) {
                        Toast.makeText(
                            this@MainActivity,
                            "Couldn't Logout!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }
}
