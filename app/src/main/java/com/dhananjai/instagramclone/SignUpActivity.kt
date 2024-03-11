package com.dhananjai.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dhananjai.instagramclone.Modals.User
import com.dhananjai.instagramclone.databinding.ActivitySignUpBinding
import com.dhananjai.instagramclone.utils.USER_NODE
import com.dhananjai.instagramclone.utils.USER_PROFILE_FOLDER
import com.dhananjai.instagramclone.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    lateinit var user: User

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri ->
        uri?.let{
            uploadImage(uri, USER_PROFILE_FOLDER){
                if (it == null){

                }else{
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text = "<font color = #111111> Already have an account? </font> <font color = #405DE6>Login here! </font>"
        binding.loginPageBtn.text = Html.fromHtml(text)
        binding.loginPageBtn.setOnClickListener{
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
        user = User()

        binding.registerBtn.setOnClickListener {
            if (binding.name.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("") or
                binding.password.editText?.text.toString().equals("")
            ) {
                Toast.makeText(this@SignUpActivity, "Please fill the details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener { result ->

                    if (result.isSuccessful) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Login Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                                finish()
                            }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

    }
}