package com.henriqueapps.administraoDeApartamentos.ui.settings

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.HomeActivity
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.DialogConfirmBinding
import com.henriqueapps.administraoDeApartamentos.databinding.FragmentSettingsBinding
import com.henriqueapps.administraoDeApartamentos.pages.EditProfile
import com.henriqueapps.administraoDeApartamentos.pages.Login
import com.henriqueapps.administraoDeApartamentos.useful.startActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteAccount.setOnClickListener {
            dialogDelete(it)
        }

        binding.editData.setOnClickListener {
            startActivity(EditProfile::class.java)
        }
    }

    private fun dialogDelete(view: View) {
        val delete = AlertDialog.Builder(view.context)
            .setTitle("Confirmação")
            .setMessage("Deseja excluir sua conta?")
            .setPositiveButton("Sim",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialogComfirmDelete(view)
                })
            .setNegativeButton("Não",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = delete.create()
        dialog.show()

    }

    private fun dialogComfirmDelete(view: View) {
        val inflater = requireActivity().layoutInflater;
        val delete = AlertDialog.Builder(view.context)
            .setTitle("Confirmação")
            .setMessage("Digite sua senha para confirmação!")
            .setView(inflater.inflate(R.layout.dialog_confirm, null))
            .setPositiveButton("Confirmar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    val bindingDialog = DialogConfirmBinding.inflate(layoutInflater)
                    val password = bindingDialog.password.text.toString()
                    verificationDelete(password)
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = delete.create()
        dialog.show()

    }

    private fun verificationDelete(password: String) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = FirebaseAuth.getInstance().currentUser!!.uid
        val emailUid = FirebaseAuth.getInstance().currentUser!!.email.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailUid, password)
            .addOnSuccessListener {
                user!!.delete()
                    .addOnSuccessListener {
                        db.collection("Usuarios").document(userUid)
                            .delete().addOnSuccessListener {
                                db.collection("Properties").whereEqualTo("usuario", userUid)
                                    .get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            db.collection("Properties")
                                                .document(document.id)
                                                .delete()
                                                .addOnSuccessListener {
                                                    startActivity(Login::class.java)
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot successfully deleted!"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(
                                                        TAG,
                                                        "Error deleting document",
                                                        e
                                                    )
                                                }
                                        }
                                    }.addOnFailureListener {
                                        Log.i(it.toString(), "SettingsFragment")
                                    }
                            }.addOnFailureListener {
                                Log.i("SettingsFragment", it.toString())
                            }
                    }.addOnFailureListener {
                        Log.i("SettingsFragment", it.toString())
                    }
            }.addOnFailureListener {
                Log.i("SettingsFragment", it.toString())
            }
        }
    }
