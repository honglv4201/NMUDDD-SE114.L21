package com.lamhong.mybook

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_shot_video.*
import net.alhazmy13.mediapicker.Video.VideoPicker
import java.io.ByteArrayOutputStream


class AddShotVideoActivity : AppCompatActivity() {



    private val VIDEO_PICK_GALLERY_CODE =100
    private val VIDEO_PICK_CAMERA_CODE= 101
    //permission code
    private val CAMERA_REQUEST_CODE= 102
    private lateinit var cameraPermision : Array<String>
    private lateinit var storagePermission : Array<String>

    private var videoUri : Uri?=null
    private var path : String?=null
    private var thumbnailuri: Uri ?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shot_video)



        //init permission
        cameraPermision= arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission= arrayOf( android.Manifest.permission.READ_EXTERNAL_STORAGE)
        btnSelectVideo.setOnClickListener{
            pickVideoDialog()
        }
        btn_upVideo.setOnClickListener{
            uploadVideo()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun uploadVideo(){
        val progressDialog: ProgressDialog= ProgressDialog(this)
        progressDialog.setTitle("Upload")
        progressDialog.setMessage("Đang tải video lên")
        progressDialog.show()
        val timestamp= System.currentTimeMillis().toString()
        val filePathName="Videos/video$timestamp"
        val storageReference= FirebaseStorage.getInstance().getReference(filePathName)

        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uri= taskSnapshot.storage.downloadUrl
                while (!uri.isSuccessful);
                val downloadUri = uri.result


                if(uri.isSuccessful){
                    // push image uri to storage
                    val file = FirebaseStorage.getInstance().reference.child("ShotVideoThumbnails")
                        .child(System.currentTimeMillis().toString() +".jpg")
                    var uploadTask  : StorageTask<*>

                    uploadTask= file!!.putFile(thumbnailuri!!)
                    var myUrl: String =""
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                        if(!task.isSuccessful){
                            task.exception.let {
                                progressDialog.dismiss()
                                throw it!!

                            }
                        }else{
                            return@Continuation file.downloadUrl

                        }
                    }).addOnCompleteListener(OnCompleteListener<Uri>{ task ->
                        if(task.isSuccessful){
                            val downloadUrl = task.result
                            myUrl=downloadUrl.toString()

                            // push value to database
                            val ref= FirebaseDatabase.getInstance().reference.child("ShotVideos")
                            val map =HashMap<String, Any>()
                            val key  = ref.push().key


                            map["id"]=key.toString()
                            map["content"]=""
                            map["timestamp"]=timestamp
                            map["video"]= downloadUri.toString()
                            map["thumb"]= myUrl
                            map["publisher"]=FirebaseAuth.getInstance().currentUser.uid.toString()
                            map["views"]=0
                            ref.child(key.toString()).setValue(map).addOnSuccessListener {



                                progressDialog.dismiss()
                                Toast.makeText(this, "Thêm video thành công" , Toast.LENGTH_LONG).show()

                            }
                                .addOnFailureListener{it->
                                    Toast.makeText(this, "${it.message}" , Toast.LENGTH_LONG).show()

                                }

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Đăng thất bại , thử lại sau", Toast.LENGTH_SHORT).show()
                        }
                    })


                }
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this, "fail" , Toast.LENGTH_LONG).show()
            }
    }

    private fun settoVideoView(){
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_view)

        //media control
        video_view.setMediaController(mediaController)
        video_view.setVideoURI(videoUri)
        video_view.requestFocus()
        video_view.setOnPreparedListener {
            video_view.pause()
        }

        image_thumb.setImageBitmap(
            ThumbnailUtils.createVideoThumbnail(path.toString(),
            MediaStore.Video.Thumbnails.MICRO_KIND));
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(this, videoUri)
        val bm = mmr.getFrameAtTime(1000)

        image_thumb.setImageBitmap(bm)
        thumbnailuri= getImageUri(bm!!)


    }


    fun getImageUri( inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            this.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
    private fun pickVideoDialog(){
        val options= arrayOf("Camera", "Thư viện")
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Chọn video từ")
            .setItems(options){dialog: DialogInterface?, which: Int ->
                if(which==0){
                    if(!checkCameraPermission()){
                        requestPermission()
                    }
                    else{
                        videoPickCameRa()
                    }
                }
                else{

                        videoPickGalerry()


                }
            }.show()
    }
    private fun checkCameraPermission() : Boolean{
        val result1=ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.CAMERA
        )==PackageManager.PERMISSION_GRANTED
        val result2=ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )==PackageManager.PERMISSION_GRANTED
        return result1 && result2
    }
//    fun checkStorageReadPermission(): Boolean{
//        return ContextCompat.checkSelfPermission(
//            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
//        )==PackageManager.PERMISSION_GRANTED
//    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, cameraPermision, CAMERA_REQUEST_CODE
        )
    }
//    private fun requestStoragepermission(){
//        ActivityCompat.requestPermissions(
//            this, storagePermission, 1
//        )
//    }


    private fun videoPickGalerry(){
        val intent = Intent()
        intent.type="video/*"
        intent.action= Intent.ACTION_GET_CONTENT
     //   val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(
//            intent,
//            VIDEO_PICK_GALLERY_CODE
//        )
        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
            VIDEO_PICK_GALLERY_CODE
        )
//        VideoPicker.Builder(this)
//            .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
//            .directory(VideoPicker.Directory.DEFAULT)
//            .extension(VideoPicker.Extension.MP4)
//            .enableDebuggingMode(true)
//            .build()

    }
    private fun videoPickCameRa(){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode){
//            CAMERA_REQUEST_CODE->{
//                if(grantResults.size>0){
//                    val cameraAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    val storageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED
//                    if(cameraAccepted && storageAccepted){
//                        videoPickCameRa()
//
//                    }
//                    else{
//                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==VIDEO_PICK_CAMERA_CODE){
                videoUri=data!!.data
                path=data!!.data!!.path
                settoVideoView()
            }
            else if(requestCode==VIDEO_PICK_GALLERY_CODE){
                videoUri=data!!.data!! as Uri
                path= data!!.data!!.path
                settoVideoView()
            }
            else if(requestCode==VideoPicker.VIDEO_PICKER_REQUEST_CODE ){
                val mPaths: List<String>? =
                    data!!.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH)
                if(mPaths==null || mPaths.isEmpty()){
                    return
                }
                else{
                    videoUri=mPaths[0].toUri()

                    settoVideoView()

                }
            }

        }
        else{
            Toast.makeText(this, "Chưa chọn thành công", Toast.LENGTH_LONG).show()

        }
    }

}