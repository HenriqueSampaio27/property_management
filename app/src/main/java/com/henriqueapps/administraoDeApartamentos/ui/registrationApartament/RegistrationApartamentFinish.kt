package com.henriqueapps.administraoDeApartamentos.ui.registrationApartament

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.HomeActivity
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityRegistrationApartamentFinishBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.IDN
import java.util.*


class RegistrationApartamentFinish : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationApartamentFinishBinding

    private var type: String = ""
    private lateinit var dialog: AlertDialog
    private lateinit var dialogTwo: AlertDialog
    private val REQUEST_IMAGE_CAPTURE = 1
    private var uriOne: Uri = Uri.EMPTY
    private var uriTwo: Uri = Uri.EMPTY
    private var uriTree: Uri = Uri.EMPTY

    companion object {
        private val GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private val galleryRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                galleryResult.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            } else {
                showDialogPermission()
            }
        }

    @SuppressLint("SetTextI18n", "ObsoleteSdkInt")
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data?.data != null) {
                val bitmap: Bitmap = if(Build.VERSION.SDK_INT < 28){
                    Images.Media.getBitmap(baseContext.contentResolver, result.data?.data)
                }else{
                    val source = ImageDecoder.createSource(this.contentResolver, result.data?.data!!)
                    ImageDecoder.decodeBitmap(source)
                }
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val uri = getImageUri(applicationContext, bitmap)
                val file = File(getRealPathFromUri(uri))

                if (uriOne.toString().isEmpty()) {
                    uriOne = uri!!
                    binding.txtImage1.text = "Imagem carregada!"
                    binding.iconImage1.setImageResource(R.drawable.ic_clear)
                } else if (uriTwo.toString().isEmpty()) {
                    uriTwo = uri!!
                    binding.txtImage2.text = "Imagem carregada!"
                    binding.iconImage2.setImageResource(R.drawable.ic_clear)
                } else if (uriTree.toString().isEmpty()) {
                    uriTree = uri!!
                    binding.txtImage3.text = "Imagem carregada!"
                    binding.iconImage3.setImageResource(R.drawable.ic_clear)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationApartamentFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = ""

        checkType()


        binding.buttonRegistration.setOnClickListener {
            propertyRegistration()
        }

        binding.iconImage1.setOnClickListener {
            if (binding.txtImage1.text == "Imagem carregada!") {
                binding.txtImage1.text = "Fazer upload da imagem"
                uriOne = Uri.EMPTY
                binding.iconImage1.setImageResource(R.drawable.ic_upload)
            } else {
                DialogImageGallery()
            }

        }
        binding.iconImage2.setOnClickListener {
            if (binding.txtImage2.text == "Imagem carregada!") {
                binding.txtImage2.text = "Fazer upload da imagem"
                uriTwo = Uri.EMPTY
                binding.iconImage2.setImageResource(R.drawable.ic_upload)
            } else {
                DialogImageGallery()
            }
        }
        binding.iconImage3.setOnClickListener {
            if (binding.txtImage3.text == "Imagem carregada!") {
                binding.txtImage3.text = "Fazer upload da imagem"
                uriTree = Uri.EMPTY
                binding.iconImage3.setImageResource(R.drawable.ic_upload)

            } else {
                DialogImageGallery()
            }
        }
    }

    private fun checkType() {
        val ck_apartament = binding.ckApartament
        val ck_kitnet = binding.ckKitnet
        val ck_house = binding.ckHouse
        val ck_point = binding.ckPoint

        ck_house.setOnClickListener {
            if (ck_house.isChecked) {
                type = "Casa"
            } else if (!ck_house.isChecked) {
                type = ""
            }
            ck_apartament.isChecked = false
            ck_point.isChecked = false
            ck_kitnet.isChecked = false
        }
        ck_kitnet.setOnClickListener {
            if (ck_kitnet.isChecked) {
                type = "Kitnet"
            } else if (!ck_kitnet.isChecked) {
                type = ""
            }
            ck_apartament.isChecked = false
            ck_house.isChecked = false
            ck_point.isChecked = false
        }
        ck_point.setOnClickListener {
            if (ck_point.isChecked) {
                type = "Ponto Comercial"
            } else if (!ck_point.isChecked) {
                type = ""
            }
            ck_apartament.isChecked = false
            ck_house.isChecked = false
            ck_kitnet.isChecked = false
        }
        ck_apartament.setOnClickListener {
            if (ck_apartament.isChecked) {
                type = "Apartamento"
            } else if (!ck_apartament.isChecked) {
                type = ""
            }
            ck_kitnet.isChecked = false
            ck_house.isChecked = false
            ck_point.isChecked = false
        }

    }

    private fun permissionVerification(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun galleryPermissionVerification() {
        val galleryPermission =
            permissionVerification(GALLERY_PERMISSION)

        when {
            galleryPermission -> {
                galleryResult.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }

            shouldShowRequestPermissionRationale(GALLERY_PERMISSION) -> showDialogPermission()

            else -> galleryRequest.launch(GALLERY_PERMISSION)
        }
    }

    private fun showDialogPermission() {
        val buider = AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Precisamos do acesso a galeria do dispositivo, deseja permitir agora?")
            .setNegativeButton("Não") { _, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialogTwo = buider.create()
        dialogTwo.show()
    }

    private fun DialogImageGallery() {

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

    private fun pictureVerificationPermission() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val uri = getImageUri(applicationContext, imageBitmap)
            val file = File(getRealPathFromUri(uri))

            if (uriOne.toString().isEmpty()) {
                uriOne = uri!!
                binding.txtImage1.text = "Imagem carregada!"
                binding.iconImage1.setImageResource(R.drawable.ic_clear)
            } else if (uriTwo.toString().isEmpty()) {
                uriTwo = uri!!
                binding.txtImage2.text = "Imagem carregada!"
                binding.iconImage2.setImageResource(R.drawable.ic_clear)
            } else if (uriTree.toString().isEmpty()) {
                uriTree = uri!!
                binding.txtImage3.text = "Imagem carregada!"
                binding.iconImage3.setImageResource(R.drawable.ic_clear)
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri?): String {
        var path = ""
        if (contentResolver != null){
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null){
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
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
        //val imageBitmap2 = Bitmap.createScaledBitmap(imageBitmap, 1000, 800, true)
        val paths = Images.Media.insertImage(applicationContext!!.contentResolver, imageBitmap, title, null)
        return Uri.parse(paths)

    }

    private fun propertyRegistration(){
        binding.progressCircular.isVisible = true
        binding.progressCircular.isFocused
        val cep = intent.getStringExtra("cep").toString()
        val logradouro = intent.getStringExtra("logradouro").toString()
        val district = intent.getStringExtra("district").toString()
        val city = intent.getStringExtra("city").toString()
        val state = intent.getStringExtra("state").toString()
        var number = intent.getStringExtra("number").toString()
        val water = intent.getStringExtra("water").toString()
        val energy = intent.getStringExtra("energy").toString()
        val price = intent.getStringExtra("price").toString()
        val type = type

        if(number.isEmpty()){
            number = "Sem número"
        }

        if(type.isNotEmpty()){
            val archiveUuid = UUID.randomUUID().toString()
            val archiveNameOne = UUID.randomUUID().toString()
            val archiveNameTwo = UUID.randomUUID().toString()
            val archiveNameTree = UUID.randomUUID().toString()

            val referenceOne = FirebaseStorage.getInstance().getReference("/image/$archiveNameOne")
            val referenceTwo = FirebaseStorage.getInstance().getReference("/image/$archiveNameTwo")
            val referenceTree = FirebaseStorage.getInstance().getReference("/image/$archiveNameTree")

            val db = FirebaseFirestore.getInstance()
            val usuarioId = FirebaseAuth.getInstance().currentUser!!.uid

            val intent = Intent(this, HomeActivity::class.java)
            if(uriOne.toString().isNotEmpty() && uriTwo.toString().isNotEmpty() && uriTree.toString()
                    .isNotEmpty()) {
                referenceOne.putFile(uriOne).addOnSuccessListener {
                    referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                        referenceTwo.putFile(uriTwo).addOnSuccessListener {
                            referenceTwo.downloadUrl.addOnSuccessListener { uriFirestoreTwo ->
                                referenceTree.putFile(uriTree).addOnSuccessListener {
                                    referenceTree.downloadUrl.addOnSuccessListener { uriFirestoreTree ->
                                        val imageUriOne = uriFirestoreOne.toString()
                                        val imageUriTwo = uriFirestoreTwo.toString()
                                        val imageUriTree = uriFirestoreTree.toString()
                                        val apartament: MutableMap<String, String> = HashMap()

                                        apartament["owner"] = usuarioId
                                        apartament["cep"] = cep
                                        apartament["logradouro"] = logradouro
                                        apartament["number"] = number
                                        apartament["district"] = district
                                        apartament["city"] = city
                                        apartament["state"] = state
                                        apartament["water"] = water
                                        apartament["energy"] = energy
                                        apartament["price"] = price
                                        apartament["type"] = type
                                        apartament["imageOne"] = imageUriOne
                                        apartament["imageTwo"] = imageUriTwo
                                        apartament["imageTree"] = imageUriTree

                                        val documentReference =
                                            db.collection("Properties").document(archiveUuid)
                                        documentReference.set(apartament).addOnSuccessListener {
                                            binding.progressCircular.isVisible = true
                                            Toast.makeText(
                                                this,
                                                "Propriedade cadastrada com sucesso!",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            startActivity(intent)
                                            finish()
                                        }.addOnFailureListener {
                                            Log.i("RegistrationApartamentFinish", it.toString())
                                        }
                                    }.addOnFailureListener {
                                        Log.i("RegistrationApartamentFinish", it.toString())
                                    }
                                }.addOnFailureListener {
                                    Log.i("RegistrationApartamentFinish", it.toString())
                                }
                            }.addOnFailureListener {
                                Log.i("RegistrationApartamentFinish", it.toString())
                            }
                        }.addOnFailureListener {
                            Log.i("RegistrationApartamentFinish", it.toString())
                        }
                    }.addOnFailureListener {
                        Log.i("RegistrationApartamentFinish", it.toString())
                    }
                }.addOnFailureListener {
                    Log.i("RegistrationApartamentFinish", it.toString())
                }
            }else if (uriOne.toString().isNotEmpty() && uriTwo.toString().isNotEmpty()){
                referenceOne.putFile(uriOne).addOnSuccessListener {
                    referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                        referenceTwo.putFile(uriTwo).addOnSuccessListener {
                            referenceTwo.downloadUrl.addOnSuccessListener { uriFirestoreTwo ->
                                val imageUriOne = uriFirestoreOne.toString()
                                val imageUriTwo = uriFirestoreTwo.toString()
                                val imageUriTree = ""
                                val apartament: MutableMap<String, String> = HashMap()

                                apartament["owner"] = usuarioId
                                apartament["cep"] = cep
                                apartament["logradouro"] = logradouro
                                apartament["number"] = number
                                apartament["district"] = district
                                apartament["city"] = city
                                apartament["state"] = state
                                apartament["water"] = water
                                apartament["energy"] = energy
                                apartament["price"] = price
                                apartament["type"] = type
                                apartament["imageOne"] = imageUriOne
                                apartament["imageTwo"] = imageUriTwo
                                apartament["imageTree"] = imageUriTree

                                val documentReference =
                                    db.collection("Properties").document(archiveUuid)
                                documentReference.set(apartament).addOnSuccessListener {
                                    binding.progressCircular.isVisible = true
                                    Toast.makeText(
                                        this,
                                        "Propriedade cadastrada com sucesso!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    startActivity(intent)
                                    finish()
                                }.addOnFailureListener {
                                    Log.i("RegistrationApartamentFinish", it.toString())
                                }
                            }.addOnFailureListener {
                                Log.i("RegistrationApartamentFinish", it.toString())
                            }
                        }.addOnFailureListener {
                            Log.i("RegistrationApartamentFinish", it.toString())
                        }
                    }.addOnFailureListener {
                        Log.i("RegistrationApartamentFinish", it.toString())
                    }
                }.addOnFailureListener{
                    Log.i("RegistrationApartamentFinish", it.toString())
                }
            }else if(uriOne.toString().isNotEmpty()){
                referenceOne.putFile(uriOne).addOnSuccessListener {
                    referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                        val imageUriOne = uriFirestoreOne.toString()
                        val imageUriTwo = ""
                        val imageUriTree = ""
                        val apartament: MutableMap<String, String> = HashMap()

                        apartament["owner"] = usuarioId
                        apartament["cep"] = cep
                        apartament["logradouro"] = logradouro
                        apartament["number"] = number
                        apartament["district"] = district
                        apartament["city"] = city
                        apartament["state"] = state
                        apartament["water"] = water
                        apartament["energy"] = energy
                        apartament["price"] = price
                        apartament["type"] = type
                        apartament["imageOne"] = imageUriOne
                        apartament["imageTwo"] = imageUriTwo
                        apartament["imageTree"] = imageUriTree

                        val documentReference = db.collection("Properties").document(archiveUuid)
                        documentReference.set(apartament).addOnSuccessListener {
                            binding.progressCircular.isVisible = true
                            Toast.makeText(
                                this,
                                "Propriedade cadastrada com sucesso!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {
                            Log.i("RegistrationApartamentFinish", it.toString())
                        }
                    }.addOnFailureListener {
                        Log.i("RegistrationApartamentFinish", it.toString())
                    }
                }.addOnFailureListener{
                    Log.i("RegistrationApartamentFinish", it.toString())
                }
            }else{
                val imageUriOne = ""
                val imageUriTwo = ""
                val imageUriTree = ""
                val apartament: MutableMap<String, String> = HashMap()

                apartament["owner"] = usuarioId
                apartament["cep"] = cep
                apartament["logradouro"] = logradouro
                apartament["number"] = number
                apartament["district"] = district
                apartament["city"] = city
                apartament["state"] = state
                apartament["water"] = water
                apartament["energy"] = energy
                apartament["price"] = price
                apartament["type"] = type
                apartament["imageOne"] = imageUriOne
                apartament["imageTwo"] = imageUriTwo
                apartament["imageTree"] = imageUriTree

                val documentReference = db.collection("Properties").document(archiveUuid)
                documentReference.set(apartament).addOnSuccessListener {
                    binding.progressCircular.isVisible = true
                    Toast.makeText(
                        this,
                        "Propriedade cadastrada com sucesso!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Log.i("RegistrationApartamentFinish", it.toString())
                }
            }
        }else{
            Toast.makeText(this, "Insira o tipo da propriedade!", Toast.LENGTH_SHORT).show()
        }
        binding.progressCircular.isVisible = false
    }
}