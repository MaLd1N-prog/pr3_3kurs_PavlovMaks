package com.bignerdranch.android.pr17_pavlov

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

private const val REQUEST_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"

class FragmentCrime : Fragment() {

    private lateinit var crime: Crime
    private lateinit var title: EditText
    private lateinit var date: Button
    private lateinit var check1: CheckBox
    private lateinit var suspectButton: Button
    private lateinit var reportButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_fragment_crime, container, false)

        title = view.findViewById(R.id.crime_title)
        date = view.findViewById(R.id.crime_date)
        check1 = view.findViewById(R.id.crime_solved)
        suspectButton = view.findViewById(R.id.crime_suspect)
        reportButton = view.findViewById(R.id.crime_report)

        date.apply {
            text = DateFormat.format(DATE_FORMAT, crime.date).toString()
            isEnabled = false
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                crime.title = s.toString()
            }
        })

        check1.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }


        reportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }
            val chooser = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooser)
        }


        val pickContactIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI
        )

        suspectButton.apply {
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            val pm: PackageManager = requireActivity().packageManager
            val resolved = pm.resolveActivity(
                pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            isEnabled = resolved != null


            if (crime.suspect.isNotBlank()) {
                text = crime.suspect
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri = data.data ?: return
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                val cursor = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)

                cursor?.use {
                    if (it.count == 0) return
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    suspectButton.text = suspect
                }
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectString
        )
    }
}