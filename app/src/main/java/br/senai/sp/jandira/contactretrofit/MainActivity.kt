package br.senai.sp.jandira.contactretrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.senai.sp.jandira.contactretrofit.api.ContactCall
import br.senai.sp.jandira.contactretrofit.api.RetrofitApi
import br.senai.sp.jandira.contactretrofit.model.Contact
import br.senai.sp.jandira.contactretrofit.ui.theme.ContactRetrofitTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactRetrofitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {

    var nameState by remember {
        mutableStateOf("")
    }

    var emailState by remember {
        mutableStateOf("")
    }

    var phoneState by remember {
        mutableStateOf("")
    }

    var activeState by remember {
        mutableStateOf(false)
    }

    val retrofit = RetrofitApi.getRetrofit()
    val contactsCall = retrofit.create(ContactCall::class.java)
    val call = contactsCall.getAll()

    var contacts by remember {
        mutableStateOf(listOf<Contact>())
    }

    //executar a chamada para o endpoint
    call.enqueue(object : Callback<List<Contact>> {
        override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
            contacts = response.body()!!
        }

        override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
            //  Log.i("ds3m", t.message.toString())
        }

    })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Cadastro de contatos")
        OutlinedTextField(value = nameState,
            onValueChange = { nameState = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact name")
            })


        OutlinedTextField(value = emailState,
            onValueChange = { emailState = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact Email")
            })

        OutlinedTextField(value = phoneState,
            onValueChange = { phoneState = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact Phone")
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )

        {
            Checkbox(checked = false, onCheckedChange = { activeState = it })
            Text(text = "Enabled")
        }
        Button(onClick = {
            val contact = Contact(
                name = nameState,
                email = emailState,
                phone = phoneState,
                active = activeState
            )
            val callContactPost = contactsCall.save(contact)

            callContactPost.enqueue(object : Callback<Contact> {
                override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                    Log.i("ds3m", response.body()!!.toString())
                }

                override fun onFailure(call: Call<Contact>, t: Throwable) {
                    Log.i("ds3m", t.message.toString())
                }
            })
        }
        )
        {
            Text(text = "Save contact")
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(contacts) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        //quando clicar no card ele preenche os inputs com os dados :000000
                        .clickable {
                            nameState = it.name
                            emailState = it.email
                            phoneState = it.phone
                            activeState = it.active
                        },
                    backgroundColor = Color(0, 188, 212, 255)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = it.name)
                        Text(text = it.email)
                        Text(text = it.phone)
                        Button(onClick = {
                            val callContactDelete = contactsCall.delete(it.id)
                            callContactDelete.enqueue(object : Callback<String> {
                                override fun onResponse(
                                    call: Call<String>,
                                    response: Response<String>
                                ) {
                                    Toast.makeText( context,                         
                                       response.code().toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                }
                                override fun onFailure(call: Call<String>, t: Throwable) {
                                }
                            })
                        })
                        {
                            Text(text = "Delete")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ContactRetrofitTheme {
        Greeting("Android")
    }
}