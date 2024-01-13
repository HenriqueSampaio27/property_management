package com.henriqueapps.administraoDeApartamentos.pages

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityAddImageBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.HashMap
import java.util.UUID

class AddImage : AppCompatActivity() {

    private lateinit var binding: ActivityAddImageBinding

    private lateinit var dialog: AlertDialog
    private lateinit var dialogTwo: AlertDialog
    private val REQUEST_IMAGE_CAPTURE = 1

    private var uriOne: Uri = Uri.EMPTY
    private var uriTwo: Uri = Uri.EMPTY
    private var uriTree: Uri = Uri.EMPTY

    private var uriImageOne: String = ""
    private var uriImageTwo: String = ""
    private var uriImageTree: String = ""

    private var imageOne : Boolean = true
    private var imageTwo : Boolean = true
    private var imageTree : Boolean = true

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val GALLERY_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
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
                uriImageOne = document.data!!["imageOne"].toString()
                uriImageTwo = document.data!!["imageTwo"].toString()
                uriImageTree = document.data!!["imageTree"].toString()

                if (uriImageOne.isNotEmpty()){
                    Glide.with(applicationContext).load(uriImageOne).into(binding.imageOne)
                }
                if (uriImageTwo.isNotEmpty()){
                    Glide.with(applicationContext).load(uriImageTwo).into(binding.imageTwo)
                }
                if (uriImageTree.isNotEmpty()){
                    Glide.with(applicationContext).load(uriImageTree).into(binding.imageTree)
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

        if(uriOne.toString().isNotEmpty() && uriTwo.toString().isNotEmpty() && uriTree.toString()
                .isNotEmpty()){
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

                                    apartament["imageOne"] = imageUriOne
                                    apartament["imageTwo"] = imageUriTwo
                                    apartament["imageTree"] = imageUriTree

                                    db.collection("Properties").document(documentId!!)
                                        .update(apartament as Map<String, Any>)
                                        .addOnSuccessListener {

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
        }else if (uriOne.toString().isNotEmpty() && uriTwo.toString().isNotEmpty()){
            referenceOne.putFile(uriOne).addOnSuccessListener {
                referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                    referenceTwo.putFile(uriTwo).addOnSuccessListener {
                        referenceTwo.downloadUrl.addOnSuccessListener { uriFirestoreTwo ->
                            val imageUriOne = uriFirestoreOne.toString()
                            val imageUriTwo = uriFirestoreTwo.toString()
                            val imageUriTree = uriImageTree
                            val apartament: MutableMap<String, String> = HashMap()

                            apartament["imageOne"] = imageUriOne
                            apartament["imageTwo"] = imageUriTwo
                            apartament["imageTree"] = imageUriTree

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
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if (uriOne.toString().isNotEmpty() && uriTree.toString().isNotEmpty()){
            referenceOne.putFile(uriOne).addOnSuccessListener {
                referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                    referenceTree.putFile(uriTree).addOnSuccessListener {
                        referenceTree.downloadUrl.addOnSuccessListener { uriFirestoreTree ->
                            val imageUriOne = uriFirestoreOne.toString()
                            val imageUriTwo = uriImageTwo
                            val imageUriTree = uriFirestoreTree.toString()
                            val apartament: MutableMap<String, String> = HashMap()

                            apartament["imageOne"] = imageUriOne
                            apartament["imageTwo"] = imageUriTwo
                            apartament["imageTree"] = imageUriTree

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
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
        }else if (uriTwo.toString().isNotEmpty() && uriTree.toString().isNotEmpty()){
            referenceTwo.putFile(uriTwo).addOnSuccessListener {
                referenceTwo.downloadUrl.addOnSuccessListener { uriFirestoreTwo ->
                    referenceTree.putFile(uriTree).addOnSuccessListener {
                        referenceTree.downloadUrl.addOnSuccessListener { uriFirestoreTree ->
                            val imageUriOne = uriImageOne
                            val imageUriTwo = uriFirestoreTwo.toString()
                            val imageUriTree = uriFirestoreTree.toString()
                            val apartament: MutableMap<String, String> = HashMap()

                            apartament["imageOne"] = imageUriOne
                            apartament["imageTwo"] = imageUriTwo
                            apartament["imageTree"] = imageUriTree

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
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
            }.addOnFailureListener{
                Log.d(TAG, it.toString())
            }
        }else if(uriOne.toString().isNotEmpty()){
            referenceOne.putFile(uriOne).addOnSuccessListener {
                referenceOne.downloadUrl.addOnSuccessListener { uriFirestoreOne ->
                    val imageUriOne = uriFirestoreOne.toString()
                    val imageUriTwo = uriImageTwo
                    val imageUriTree = uriImageTree
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageUriOne
                    apartament["imageTwo"] = imageUriTwo
                    apartament["imageTree"] = imageUriTree

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
        }else if(uriTwo.toString().isNotEmpty()){
            referenceTwo.putFile(uriTwo).addOnSuccessListener {
                referenceTwo.downloadUrl.addOnSuccessListener { uriFirestoreTwo ->
                    val imageUriOne = uriImageOne
                    val imageUriTwo = uriFirestoreTwo.toString()
                    val imageUriTree = uriImageTree
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageUriOne
                    apartament["imageTwo"] = imageUriTwo
                    apartament["imageTree"] = imageUriTree

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
        }else if(uriTree.toString().isNotEmpty()){
            referenceTree.putFile(uriTree).addOnSuccessListener {
                referenceTree.downloadUrl.addOnSuccessListener { uriFirestoreTree ->
                    val imageUriOne = uriImageOne
                    val imageUriTwo = uriImageTwo
                    val imageUriTree = uriFirestoreTree.toString()
                    val apartament: MutableMap<String, String> = HashMap()

                    apartament["imageOne"] = imageUriOne
                    apartament["imageTwo"] = imageUriTwo
                    apartament["imageTree"] = imageUriTree

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

                val uri = getImageUri(applicationContext, bitmap)
                val file = File(getRealPathFromUri(uri))

                if (imageOne) {
                    uriOne = uri!!
                    binding.imageOne.setImageBitmap(bitmap)
                } else if (imageTwo) {
                    uriTwo = uri!!
                    binding.imageTwo.setImageBitmap(bitmap)
                } else if (imageTree) {
                    uriTree = uri!!
                    binding.imageTree.setImageBitmap(bitmap)
                }
            }
        }

    private fun permissionVerification(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            val uri = getImageUri(applicationContext, imageBitmap)
            val file = File(getRealPathFromUri(uri))

            if (imageOne) {
                uriOne = uri!!
                binding.imageOne.setImageBitmap(imageBitmap)
            } else if (imageTwo) {
                uriTwo = uri!!
                binding.imageTwo.setImageBitmap(imageBitmap)
            } else if (imageTree) {
                uriTree = uri!!
                binding.imageTree.setImageBitmap(imageBitmap)
            }
        }
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

}