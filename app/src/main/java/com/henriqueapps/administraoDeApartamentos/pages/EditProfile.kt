package com.henriqueapps.administraoDeApartamentos.pages

import android.Manifest
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityEditProfileBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogConfirmBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogUpdateEmailBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogUpdatePhoneBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var dialog: AlertDialog
    private var byte: ByteArray = "".toByteArray()
    private var byteString = ""
    private var updateEmail = ""
    private var updatePhone = ""
    private var updatePassword = ""
    private var userId : String = ""
    private val REQUEST_IMAGE_CAPTURE = 1

    companion object{
        private val GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private val galleryRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if(permission){
            galleryResult.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }else{
            showDialogPermission()
        }
    }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.data?.data != null){
            val bitmap: Bitmap = if(Build.VERSION.SDK_INT < 28){
                MediaStore.Images.Media.getBitmap(baseContext.contentResolver, result.data?.data)
            }else{
                val source = ImageDecoder.createSource(this.contentResolver, result.data?.data!!)
                ImageDecoder.decodeBitmap(source)
            }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            byte = baos.toByteArray()
            byteString = baos.toString()
            binding.userAvatar.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.backgroundIcCamera.setOnClickListener {
            DialogImageGallery()
        }

        binding.buttonEditEmail.setOnClickListener {
            dialogUpdateEmail(it)
        }

        binding.buttonEditPhone.setOnClickListener {
            dialogUpdatePhone(it)
        }
        binding.buttonEditPassword.setOnClickListener {
            dialogUpdatePassword(it)
        }

        binding.buttonUpdate.setOnClickListener {
            saveUserUpdate()
        }
    }

    private fun saveUserUpdate(){
        val user = FirebaseAuth.getInstance().currentUser
        val userUUID = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        if(!updateEmail.isEmpty()){
            user!!.updateEmail(updateEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                    }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
        }

        if(!updatePassword.isEmpty()){
            user!!.updatePassword(updatePassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                    }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
        }

        if (!updatePhone.isEmpty()){
            db.collection("Usuarios").document(userUUID)
                .update(
                    mapOf("phone" to updatePhone)
                ).addOnSuccessListener {

                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
        }

        if (!byteString.isEmpty()){
            val archiveName = UUID.randomUUID().toString()
            val reference = FirebaseStorage.getInstance().getReference("/imagens/$archiveName")
            reference.putBytes(byte).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener {    byteFirestore ->
                    var byteImage = byteFirestore.toString()

                    db.collection("Usuarios").document(userUUID)
                        .update(mapOf(
                            "image" to byteImage
                        )).addOnSuccessListener {

                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
        }
        Toast.makeText(this, "Atualização realizada com sucesso!", Toast.LENGTH_SHORT)
            .show()
        finish()

    }

    private fun dialogUpdateEmail(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Atualização de email")
            .setMessage("Insira seu novo email!")
            .setView(inflater.inflate(R.layout.dialog_update_email, null))
            .setPositiveButton("Atualizar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updateEmail = DialogUpdateEmailBinding.inflate(layoutInflater).emailUser.text.toString()
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()

    }

    private fun dialogUpdatePhone(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Atualização de Telefone")
            .setMessage("Insira seu novo número!")
            .setView(inflater.inflate(R.layout.dialog_update_phone, null))
            .setPositiveButton("Atualizar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updatePhone = DialogUpdatePhoneBinding.inflate(layoutInflater).phoneUser.text.toString()
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()
    }

    private fun dialogUpdatePassword(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Atualização de Senha")
            .setMessage("Insira sua nova senha!")
            .setView(inflater.inflate(R.layout.dialog_confirm, null))
            .setPositiveButton("Atualizar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updatePassword = DialogConfirmBinding.inflate(layoutInflater).password.text.toString()
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()
    }
    private fun DialogImageGallery(){

        val selectPhoto = AlertDialog.Builder(this)
            .setTitle("Origem da foto")
            .setMessage("Por favor, selecione a origem da foto")
            .setPositiveButton("Câmera",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    pictureVerificationPermission()
                })
            .setNegativeButton("Galeria",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    galleryPermissionVerification()
                })

        dialog = selectPhoto.create()
        dialog.show()

    }

    private fun permissionVerification(permission : String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun galleryPermissionVerification(){
        val galleryPermission = permissionVerification(EditProfile.GALLERY_PERMISSION)

        when{
            galleryPermission -> {
                galleryResult.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }

            shouldShowRequestPermissionRationale(EditProfile.GALLERY_PERMISSION) -> showDialogPermission()

            else -> galleryRequest.launch(EditProfile.GALLERY_PERMISSION)
        }
    }

    private fun showDialogPermission(){
        val buider = AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Precisamos do acesso a galeria do dispositivo, deseja permitir agora?")
            .setNegativeButton("Não") { _, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = buider.create()
        dialog.show()
    }

    private fun pictureVerificationPermission(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.userAvatar.setImageBitmap(imageBitmap)
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            byte = baos.toByteArray()
            byteString = baos.toString()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStart() {
        super.onStart()
        val db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        val emailID = FirebaseAuth.getInstance().currentUser!!.email

        val documentReference = db.collection("Usuarios").document(userId)
        documentReference.addSnapshotListener { value, error ->
            if (value != null) {
                //Glide.with(applicationContext).load(value.getString("image")).into(findViewById(R.id.imageAvatar))
            }
        }
    }
}