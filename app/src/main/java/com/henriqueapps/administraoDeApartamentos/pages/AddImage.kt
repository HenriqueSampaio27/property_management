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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityAddImageBinding
import java.io.ByteArrayOutputStream
import java.util.HashMap
import java.util.UUID

class AddImage : AppCompatActivity() {

    private lateinit var binding: ActivityAddImageBinding

    private lateinit var dialog: AlertDialog
    private lateinit var dialogTwo: AlertDialog
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var byteOne: ByteArray
    private lateinit var byteTwo: ByteArray
    private lateinit var byteTree: ByteArray

    private lateinit var uriImageOne: Uri
    private lateinit var uriImageTwo: Uri
    private lateinit var uriImageTree: Uri

    private var imageOne : Boolean = true
    private var imageTwo : Boolean = true
    private var imageTree : Boolean = true

    companion object {
        private const val GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveImages()

        binding.backgroundIcCameraOne.setOnClickListener {
            DialogImageGallery()
            imageOne = true
            imageTwo = false
            imageTree = false
        }
        binding.backgroundIcCameraTwo.setOnClickListener {
            DialogImageGallery()
            imageOne = false
            imageTwo = true
            imageTree = false
        }
        binding.backgroundIcCameraTree.setOnClickListener {
            DialogImageGallery()
            imageOne = false
            imageTwo = false
            imageTree = true
        }

        binding.buttonSave.setOnClickListener {
            saveImages()
        }
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun retrieveImages(){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")

        db.collection("Properties").document(documentId!!)
            .get().addOnSuccessListener { document ->
                uriImageOne = document.data!!["imageOne"].toString().toUri()
                uriImageTwo = document.data!!["imageTwo"].toString().toUri()
                uriImageTree = document.data!!["imageTree"].toString().toUri()

                if (uriImageOne.toString().isNotEmpty()){
                    binding.imageOne.setImageURI(uriImageOne)
                }
                if (uriImageTwo.toString().isNotEmpty()){
                    binding.imageTwo.setImageURI(uriImageTwo)
                }
                if (uriImageTree.toString().isNotEmpty()){
                    binding.imageTree.setImageURI(uriImageTree)
                }

            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    private fun saveImages(){

        val archiveNameOne = UUID.randomUUID().toString()
        val archiveNameTwo = UUID.randomUUID().toString()
        val archiveNameTree = UUID.randomUUID().toString()

        val referenceOne = FirebaseStorage.getInstance().getReference("/image/$archiveNameOne")
        val referenceTwo = FirebaseStorage.getInstance().getReference("/image/$archiveNameTwo")
        val referenceTree = FirebaseStorage.getInstance().getReference("/image/$archiveNameTree")

        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")

        if(byteOne.toString().isNotEmpty() && byteTwo.toString().isNotEmpty() && byteTree.toString()
                .isNotEmpty()){
            referenceOne.putBytes(byteOne).addOnSuccessListener { byteFirestoreOne ->
                referenceTwo.putBytes(byteTwo).addOnSuccessListener { byteFirestoreTwo ->
                    referenceTree.putBytes(byteTree).addOnSuccessListener { byteFirestoreTree ->
                        val imageByteOne = byteFirestoreOne.toString()
                        val imageByteTwo = byteFirestoreTwo.toString()
                        val imageByteTree = byteFirestoreTree.toString()
                        val apartament: MutableMap<String, String> = HashMap()

                        apartament["imageOne"] = imageByteOne
                        apartament["imageTwo"] = imageByteTwo
                        apartament["imageTree"] = imageByteTree

                        db.collection("Properties").document(documentId!!)
                            .update(apartament as Map<String, Any>).addOnSuccessListener {

                                Toast.makeText(
                                this,
                                "Imagens atualizadas com sucesso!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                    }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if (byteOne.toString().isNotEmpty() && byteTwo.toString().isNotEmpty()){
            referenceOne.putBytes(byteOne).addOnSuccessListener { byteFirestoreOne ->
                referenceTwo.putBytes(byteTwo).addOnSuccessListener { byteFirestoreTwo ->
                    val imageByteOne = byteFirestoreOne.toString()
                    val imageByteTwo = byteFirestoreTwo.toString()
                    val imageByteTree = uriImageTree.toString()
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageByteOne
                    apartament["imageTwo"] = imageByteTwo
                    apartament["imageTree"] = imageByteTree

                    db.collection("Properties").document(documentId!!)
                        .update(apartament as Map<String, Any>).addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Imagens atualizadas com sucesso!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
                }
        }else if (byteOne.toString().isNotEmpty() && byteTree.toString().isNotEmpty()){
            referenceOne.putBytes(byteOne).addOnSuccessListener { byteFirestoreOne ->
                referenceTwo.putBytes(byteTree).addOnSuccessListener { byteFirestoreTree ->
                    val imageByteOne = byteFirestoreOne.toString()
                    val imageByteTwo = uriImageTwo.toString()
                    val imageByteTree = byteFirestoreTree.toString()
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageByteOne
                    apartament["imageTwo"] = imageByteTwo
                    apartament["imageTree"] = imageByteTree

                    db.collection("Properties").document(documentId!!)
                        .update(apartament as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Imagens atualizadas com sucesso!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                    }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if (byteTwo.toString().isNotEmpty() && byteTree.toString().isNotEmpty()){
            referenceOne.putBytes(byteTwo).addOnSuccessListener { byteFirestoreTwo ->
                referenceTwo.putBytes(byteTree).addOnSuccessListener { byteFirestoreTree ->
                    val imageByteOne = uriImageOne.toString()
                    val imageByteTwo = byteFirestoreTwo.toString()
                    val imageByteTree = byteFirestoreTree.toString()
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageByteOne
                    apartament["imageTwo"] = imageByteTwo
                    apartament["imageTree"] = imageByteTree

                    db.collection("Properties").document(documentId!!)
                        .update(apartament as Map<String, Any>).addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Imagens atualizadas com sucesso!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if(byteOne.toString().isNotEmpty()){
            referenceOne.putBytes(byteOne).addOnSuccessListener { byteFirestoreOne ->
                val imageByteOne = byteFirestoreOne.toString()
                val imageByteTwo = ""
                val imageByteTree = ""
                val apartament: MutableMap<String, String> = HashMap()

                apartament["imageOne"] = imageByteOne
                apartament["imageTwo"] = imageByteTwo
                apartament["imageTree"] = imageByteTree

                db.collection("Properties").document(documentId!!)
                    .update(apartament as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Imagens atualizadas com sucesso!",
                            Toast.LENGTH_SHORT
                            )
                            .show()
                        finish()
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                    }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if(byteTwo.toString().isNotEmpty()){
            referenceOne.putBytes(byteTwo).addOnSuccessListener { byteFirestoreTwo ->
                val imageByteOne = uriImageOne.toString()
                val imageByteTwo = byteFirestoreTwo.toString()
                val imageByteTree = uriImageTree.toString()
                val apartament: MutableMap<String, String> = HashMap()

                apartament["imageOne"] = imageByteOne
                apartament["imageTwo"] = imageByteTwo
                apartament["imageTree"] = imageByteTree

                db.collection("Properties").document(documentId!!)
                    .update(apartament as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Imagens atualizadas com sucesso!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                    }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if(byteTree.toString().isNotEmpty()){
            referenceOne.putBytes(byteTree).addOnSuccessListener { byteFirestoreTree ->
                val imageByteOne = uriImageOne.toString()
                val imageByteTwo = uriImageTwo.toString()
                val imageByteTree = byteFirestoreTree.toString()
                val apartament: MutableMap<String, String> = HashMap()

                apartament["imageOne"] = imageByteOne
                apartament["imageTwo"] = imageByteTwo
                apartament["imageTree"] = imageByteTree

                db.collection("Properties").document(documentId!!)
                    .update(apartament as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Imagens atualizadas com sucesso!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                    }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else{
            Toast.makeText(this, "Nenhuma imagem foi atualizada!", Toast.LENGTH_SHORT)
                .show()
        }

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

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data?.data != null) {
                val bitmap: Bitmap = if(Build.VERSION.SDK_INT < 28){
                    MediaStore.Images.Media.getBitmap(baseContext.contentResolver, result.data?.data)
                }else{
                    val source = ImageDecoder.createSource(this.contentResolver, result.data?.data!!)
                    ImageDecoder.decodeBitmap(source)
                }
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                if (imageOne) {
                    byteOne = baos.toByteArray()
                    binding.imageOne.setImageBitmap(bitmap)
                } else if (imageTwo) {
                    byteTwo = baos.toByteArray()
                    binding.imageTwo.setImageBitmap(bitmap)
                } else if (imageTree) {
                    byteTree = baos.toByteArray()
                    binding.imageTree.setImageBitmap(bitmap)
                }
            }
        }

    private fun permissionVerification(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun galleryPermissionVerification() {
        val galleryPermission =
            permissionVerification(AddImage.GALLERY_PERMISSION)

        when {
            galleryPermission -> {
                galleryResult.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }

            shouldShowRequestPermissionRationale(AddImage.GALLERY_PERMISSION) -> showDialogPermission()

            else -> galleryRequest.launch(AddImage.GALLERY_PERMISSION)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            if (imageOne) {
                byteOne = baos.toByteArray()
                binding.imageOne.setImageBitmap(imageBitmap)
            } else if (imageTwo) {
                byteTwo = baos.toByteArray()
                binding.imageTwo.setImageBitmap(imageBitmap)
            } else if (imageTree) {
                byteTree = baos.toByteArray()
                binding.imageTree.setImageBitmap(imageBitmap)
            }
        }
    }
}