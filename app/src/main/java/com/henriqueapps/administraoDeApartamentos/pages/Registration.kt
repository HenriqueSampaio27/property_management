package com.henriqueapps.administraoDeApartamentos.pages

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityRegistrationBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class Registration : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var dialog: AlertDialog
    private lateinit var usuarioId : String
    private val REQUEST_IMAGE_CAPTURE = 1
    private var uri : Uri = Uri.EMPTY

    companion object{
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val GALLERY_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
    }

    private val galleryRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if(permission){
            galleryResult.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }else{
            showDialogPermission()
        }
    }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {  result ->
        if(result.data?.data != null){
            val bitmap: Bitmap = if(Build.VERSION.SDK_INT < 28){
                MediaStore.Images.Media.getBitmap(baseContext.contentResolver, result.data?.data)
            }else{
                val source = ImageDecoder.createSource(this.contentResolver, result.data?.data!!)
                ImageDecoder.decodeBitmap(source)
            }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            uri = getImageUri(applicationContext, bitmap)!!
            val file = File(getRealPathFromUri(uri))

            binding.userAvatar.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#00A86B")
        supportActionBar!!.hide()

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.backgroundIcCamera.setOnClickListener {
            DialogImageGallery()
        }

        binding.buttonCadastrar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val name = binding.editName.text.toString()
            val lastName = binding.editLastName.text.toString()
            val phone = binding.editPhone.masked

            val isDone = binding.editPhone.isDone

            when {
                email.isEmpty() && password.isEmpty() && name.isEmpty() && lastName.isEmpty() && phone.isEmpty() -> {
                    val snackbar =
                        Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                email.isEmpty() -> {
                    binding.editEmail.error = "Preencha o E-mail!"
                }
                password.isEmpty() -> {
                    binding.editPassword.error = "Preencha a Senha!"
                }
                name.isEmpty() -> {
                    binding.editName.error = "Preencha o Nome!"
                }
                lastName.isEmpty() -> {
                    binding.editLastName.error = "Preencha o Sobrenome!"
                }
                else -> {
                    register(email, password, name, lastName, phone)
                }
            }
        }
    }

    private fun register(
        email: String,
        password: String,
        name: String,
        lastName: String,
        phone: String
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUser(name, lastName, phone)
                }
            }.addOnFailureListener {
            val erro = it
            when {
                erro is FirebaseAuthWeakPasswordException -> {
                    binding.editPassword.error = "A senha deve ter no mínimo 6 caracteres!"
                }
                erro is FirebaseAuthUserCollisionException -> {
                    binding.editEmail.error = "E-mail ja existe!"
                }
                erro is FirebaseNetworkException -> {
                    Toast.makeText(this, "Sem conexão com a internet!", Toast.LENGTH_SHORT).show()
                }
                erro is FirebaseAuthInvalidCredentialsException -> {
                    binding.editEmail.error = "E-mail inválido!"
                }
                else -> {
                    Toast.makeText(this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUser(name: String, lastName: String, phone: String) {
        val archiveName = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/imagens/$archiveName")
        binding.progressCircular.isVisible = true
        binding.progressCircular.isFocused
        if(uri.toString().isEmpty()){
            val db = FirebaseFirestore.getInstance()
            val usuarios: MutableMap<String, String> = HashMap()
            usuarios["name"] = name
            usuarios["lastName"] = lastName
            usuarios["phone"] = phone
            usuarios["image"] = ""

            usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
            val documentReference = db.collection("Usuarios").document(usuarioId)
            documentReference.set(usuarios).addOnSuccessListener {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                binding.progressCircular.isVisible = false
                Log.i("Registration", it.toString())
            }
        }else{
            reference.putFile(uri).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener {    uriFirestore ->
                    val uriImage = uriFirestore.toString()
                    val db = FirebaseFirestore.getInstance()
                    val usuarios: MutableMap<String, String> = HashMap()
                    usuarios["name"] = name
                    usuarios["lastName"] = lastName
                    usuarios["phone"] = phone
                    usuarios["image"] = uriImage

                    usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
                    val documentReference = db.collection("Usuarios").document(usuarioId)
                    documentReference.set(usuarios).addOnSuccessListener {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        binding.progressCircular.isVisible = false
                        Log.i("Registration", it.toString())
                    }

                }.addOnFailureListener {
                    binding.progressCircular.isVisible = false
                    Log.i("Registration", it.toString())
                }
            }.addOnFailureListener {
                binding.progressCircular.isVisible = false
                Log.i("Registration o erro esta aq", it.toString())
            }
        }



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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun galleryPermissionVerification(){
        val galleryPermission = permissionVerification(GALLERY_PERMISSION)

        when{
            galleryPermission -> {
                galleryResult.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }

            shouldShowRequestPermissionRationale(GALLERY_PERMISSION) -> showDialogPermission()

            else -> galleryRequest.launch(GALLERY_PERMISSION)
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
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = buider.create()
        dialog.show()
    }

    private fun getRealPathFromUri(uri: Uri?): String {
        var path = ""
        if (contentResolver != null){
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null){
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    private fun getImageUri(applicationContext: Context?, imageBitmap: Bitmap): Uri? {
        val title = UUID.randomUUID().toString()
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val paths = MediaStore.Images.Media.insertImage(applicationContext!!.contentResolver, imageBitmap, title, null)
        return Uri.parse(paths)

    }

    private fun pictureVerificationPermission(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.userAvatar.setImageBitmap(imageBitmap)
            uri = getImageUri(applicationContext, imageBitmap)!!
            val file = File(getRealPathFromUri(uri))
        }
    }
}