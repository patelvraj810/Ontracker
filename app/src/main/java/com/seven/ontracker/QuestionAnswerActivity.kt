package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_question_answer.*

class QuestionAnswerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_answer)

        saveQuestionButton.setOnClickListener {
            if((!TextUtils.isEmpty(questionPublisherName.text)) && (!TextUtils.isEmpty(questionEditText.text))) {
                // Instance of out QAForum class
                val forum = QAForum()
                // save userName and question to the forum instance variables
                forum.userName = questionPublisherName.text.toString().trim()
                forum.question = questionEditText.text.toString().trim()

                // create the collection "questions" in the database
                val database = FirebaseFirestore.getInstance().collection("questions")
                // Take the firebase id and set to the forum instance variable id
                forum.id = database.document().id
                database.document(forum.id!!).set(forum)

                // clear the fields and display the message
                questionPublisherName.setText("")
                questionEditText.setText("")
                Toast.makeText(this, "Question Posted", Toast.LENGTH_LONG).show()

                // Pass user to the DisplayQuestionAnswerActivity page
                val intent = Intent(applicationContext, DisplayQuestionAnswerActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(this,"Please enter the inputs", Toast.LENGTH_LONG).show()
            }
        }
    }
}