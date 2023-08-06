package edu.temple.assignment2

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //grabbing each view
        val message = findViewById<TextView>(R.id.message)
        val nameInput = findViewById<EditText>(R.id.inputName)
        val emailInput = findViewById<EditText>(R.id.inputEmail)
        val passwordInput = findViewById<EditText>(R.id.inputPassword)
        val passwordConfirmInput = findViewById<EditText>(R.id.inputConfirmPassword)
        val spinner = findViewById<Spinner>(R.id.spinner)

        //grabbing value from each input
        var name = nameInput.text
        var email = emailInput.text
        var password = passwordInput.text
        var passwordConfirm = passwordConfirmInput.text

        val programs = arrayOf("Please select your program", "Computer Science", "Information Science", "Math and CS", "Data Science", "Other")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, programs.asList())

        findViewById<View>(R.id.button).setOnClickListener{

            var selectedProgram = spinner.selectedItem.toString()

            if(name.toString().equals("") || email.toString().equals("") || selectedProgram.equals("Please select your program") || password.toString().equals("")){
                if (name.toString().equals("")) {
                    nameInput.error = "Name not entered"
                    message.text = "Please fix the errors"
                }
                if (email.toString().equals("")) {
                    emailInput.error = "Email not entered"
                    message.text = "Please fix the errors"
                }
                if (selectedProgram.equals("Please select your program")) {
                    (spinner.selectedView as TextView).error = "Error message"
                    message.text = "Please fix the errors"
                }
                if (password.toString().equals("")) {
                    passwordInput.error = "Password not entered"
                    message.text = "Please fix the errors"
                }
            }else if(password.toString() != passwordConfirm.toString()){
                passwordConfirmInput.error = "Password fields do not match"
                message.text = "Please fix the errors"
            }
            else{
                message.text = "Welcome to the app, ${name}"
            }

        }

    }
}