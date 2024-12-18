package com.example.opp_e2guana.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.FriendListAdapter
import com.example.opp_e2guana.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class Userdata_viewmodel : ViewModel() {

    companion object { // 기본 이미지 URL
        const val DEFAULT_PROFILE_IMAGE_URL = "android.resource://com.example.opp_e2guana/drawable/ic_profile_icon"
    }

    //라이브 데이터라고 해서 (외부)프레그먼트에서 모니터링(옵저브)할 수 있는 string임, 외부 관찰은 가능하나 함부로 못바꿈(private 반드시 걸것)
    private val name = MutableLiveData<String>("UNKNOWN")
    val show_name: LiveData<String> get() = name //이런식으로 볼 수는 있으나 외부에서 바꿀 수 없는 패턴으로 디자인해야함

    private val emailAddress = MutableLiveData<String>("Not have email")
    val show_email: LiveData<String> get() = emailAddress

    private val phoneNumber = MutableLiveData<String>("Not have phoneNumber")
    val show_phone: LiveData<String> get() = phoneNumber

    private val password = MutableLiveData<String>("error") //패스워드는 당연히 보이면 안됨!!
    val show_password: LiveData<String> get() = password          //비밀번호 변경할 때 검증 목적으로

    private val imageURL = MutableLiveData<String>(DEFAULT_PROFILE_IMAGE_URL)
    val show_imageURL: LiveData<String> get() = imageURL

    private val imageBitmap = MutableLiveData<Bitmap>()
    val show_imageBitmap: LiveData<Bitmap> get() = imageBitmap

    var imageData: ByteArray? = null

    // 친구 목록 데이터를 저장하는 변수
    private val friendList = MutableLiveData<List<FriendListAdapter.Friend_Data>>()
    val show_friendList: List<FriendListAdapter.Friend_Data>
        get() = friendList.value ?: emptyList()

    // 선택된 Friend_Data를 저장할 LiveData
    private val _selectedFriend = MutableLiveData<FriendListAdapter.Friend_Data>()
    val selectedFriend: LiveData<FriendListAdapter.Friend_Data> get() = _selectedFriend

    fun selectFriend(friend: FriendListAdapter.Friend_Data) {   //친구 리스트 정보 불러오기
        _selectedFriend.value = friend
    }

    //현재 사용자 ID관리 - e2guana 추가
    private val _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String> get() = _currentUserId

    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }


    // Firebase Auth & Database 초기화 -MOON
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun set_name(nameData: String) { name.value = nameData }

    fun set_email(emailData: String) { emailAddress.value = emailData }

    fun set_phone(phone: String) { phoneNumber.value = phone }

    fun set_imageURL(imageURLdata: String) { imageURL.value = imageURLdata }

    fun set_password(passwordData: String) { password.value = passwordData }

    // Firebase Auth에 계정 생성 및 로그인, 경현이 사용하는 함수 -MOON
    fun createFirebaseUser(onComplete: (Boolean, Exception?) -> Unit) {
        val email = emailAddress.value ?: ""
        val password = password.value ?: ""

        // Firebase Auth에 계정 생성
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadUserDataToFirebase() // String 타입 URL 전달
                    onComplete(true, null) // 성공 콜백
                } else {
                    onComplete(false, task.exception) // 실패 콜백
                }
            }
    }

    // Firebase에 사용자 정보 변경, 경현,승훈이 둘다 사용하는 함수 -MOON
    fun uploadUserDataToFirebase() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userMap = mapOf(
                "name" to name.value,
                "email" to emailAddress.value,
                "phone" to phoneNumber.value,
                "profileImageUrl" to imageURL.value
            )

            // 여기ㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣ
            database.child("users").child(currentUser.uid).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UserdataViewModel", "User data uploaded successfully")
                    } else {
                        Log.e("UserdataViewModel", "Failed to upload user data")
                    }
                }
        } else {
            Log.e("UserdataViewModel", "No authenticated user found")
        }
    }


    // firebase에서 사용자 계정 삭제 - j
    fun removeUserDataToFirebase(onComplete: (Boolean) -> Unit) {   //콜백을 통해서 T/F를 반환하고, 프로필세팅에서 반환 값보고 로직 추가할 예정
        val currentUser = auth.currentUser?.let { user ->
            user.delete()
                .addOnCompleteListener {  //firebase 문서에서는 it대신 task로 쓰는게 가독성 측면에서 좋아보이나, 아래 if문이 전부이기에 생략합니다.
                    if(it.isSuccessful) {
                        Log.d("Firebase", "Remove user data")
                        onComplete(true)
                    } else {
                        Log.e("Firebase", "failed to remove user data")
                        onComplete(false)
                    }
                }
        } ?: {                                                      // 계정이 null일 경우
            Log.e("Firebase", "account data is Null!!")
            onComplete(false)
        }
    }

    // Firebase 비밀번호 변경 함수, 승훈이 사용하는 함수. -MOON
    fun updatePassword(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // 현재 사용자 정보 출력 (디버깅용)
            Log.d("Firebase", "Current user: Email=${user.email}")

            // 비밀번호 업데이트
            user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Password update success")
                    } else {
                        Log.e("Firebase", "Password update failed", task.exception)
                    }
                }
            }
        else {
            Log.e("Firebase", "User not logged in")
        }
    }

    // Firebase 사용자 로그인, 관형이 사용하는 함수 -MOON
    fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Login Success")
                    onComplete(true, null) // 성공 반환
                } else {
                    Log.e("Firebase", "Login Fail", task.exception)
                    onComplete(false, task.exception) // 실패 반환
                }
            }
    }

    // Firebase에서 사용자의 데이터를 가져오는 함수
    fun fetchUserData(userId: String) {
        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            val name = snapshot.child("name").value as? String ?: "UNKNOWN"
            val email = snapshot.child("email").value as? String ?: "Not have email"
            val phone = snapshot.child("phone").value as? String ?: "Not have phoneNumber"
            val profileImageUrl = snapshot.child("profileImageUrl").value as? String
                ?: "https://example.com/default_profile_image.jpg" // 기본 이미지 URL

            // ViewModel 변수 업데이트
            set_name(name)
            set_email(email)
            set_phone(phone)
            set_imageURL(profileImageUrl)

            Log.d("Firebase", "User data fetched: Name=$name, Phone=$phone, ProfileImageUrl=$profileImageUrl")
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch user data", it)
        }
    }

    fun fetchFriendsData() {
        val friendsRef = database.child("users")

        friendsRef.get().addOnSuccessListener { snapshot ->
            val friends = snapshot.children.mapNotNull { child ->
                val userId = child.key ?: return@mapNotNull null
                val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                val phone = child.child("phone").getValue(String::class.java) ?: "No Phone"
                val profileImageUrl = child.child("profileImageUrl").getValue(String::class.java)
                    ?: "https://example.com/default_profile_image.jpg"

                FriendListAdapter.Friend_Data(userId, name, phone, profileImageUrl)
            }
            friendList.postValue(friends) // LiveData에 업데이트
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to fetch friends", exception)
            friendList.postValue(emptyList()) // 실패 시 빈 리스트
        }
    }

    // 이미지 데이터를 저장 (갤러리에서 선택된 이미지 처리)
    fun saveImageData(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val croppedBitmap = cropToSquare(bitmap)
            val outputStream = ByteArrayOutputStream()
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            imageData = outputStream.toByteArray()
            imageBitmap.value = croppedBitmap
            Log.d("UserdataViewModel", "Image data saved successfully")
        } catch (e: Exception) {
            Log.e("UserdataViewModel", "Failed to process image: ${e.message}")
            imageData = null
        }
    }

    // 프로필 이미지를 정사각형으로 크롭하는 이미지 처리 함수
    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    // Firebase Storage에 프로필 이미지 업로드
    fun uploadProfileImage(onComplete: (String?) -> Unit) {
        if (imageData == null) { // 이미지가 비어있을 때 기본 이미지로 대체
            onComplete(DEFAULT_PROFILE_IMAGE_URL)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val profileRef = storageRef.child("profiles/${System.currentTimeMillis()}.jpg")

        profileRef.putBytes(imageData!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("UserdataViewModel", "Image uploaded: $uri")
                    onComplete(uri.toString()) // 이미지 업로드
                }.addOnFailureListener {
                    Log.e("UserdataViewModel", "Failed to get download URL.")
                    onComplete(DEFAULT_PROFILE_IMAGE_URL)
                }
            }.addOnFailureListener {
                Log.e("UserdataViewModel", "Image upload failed.")
                onComplete(DEFAULT_PROFILE_IMAGE_URL)
            }
    }



    /*
    // Firebase에서 다른사용자(친구)의 데이터를 가져오는 함수(원래 함수)
    fun fetchFriendsData(onComplete: (List<FriendListAdapter.Friend_Data>) -> Unit) {
        database.child("users").get().addOnSuccessListener { snapshot ->
            val friends = mutableListOf<FriendListAdapter.Friend_Data>()
            for (child in snapshot.children) {
                val userId = child.key ?: continue
                val name = child.child("name").value as? String ?: "Unknown"
                val phone = child.child("phone").value as? String ?: "No Phone"
                val profileImageUrl = child.child("profileImageUrl").value as? String
                    ?: "https://"
                friends.add(FriendListAdapter.Friend_Data(userId, name, phone, profileImageUrl))
            }
            friendList.value = friends // ViewModel 변수에 데이터 저장
            onComplete(friends) // 데이터를 Fragment로 전달
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch friends", it)
            onComplete(emptyList()) // 실패 시 빈 리스트 반환
        }
    }*/

    //채팅방 생성을 위해 한 명의 Friend 정보를 가져옴
    fun fetchFriendData(friendId: String, onComplete: (FriendListAdapter.Friend_Data?) -> Unit) {
        val userRef = database.child("users").child(friendId)

        userRef.get().addOnSuccessListener { snapshot ->
            val friend = snapshot.getValue(FriendListAdapter.Friend_Data::class.java)
            onComplete(friend) // 친구 데이터 반환
        }.addOnFailureListener {
            onComplete(null) // 실패 시 null 반환
        }
    }


    // 닉네임 형식 검증 함수
    fun isValidNickname(nickname: String): String? {
        return when {
            nickname.isBlank() -> "닉네임은 비어 있을 수 없습니다."
            nickname.contains(" ") -> "닉네임에는 공백이 포함될 수 없습니다."
            nickname.toByteArray(Charsets.UTF_8).size > 30 -> "닉네임은 최대 한글 10글자, 영어 30글자까지 가능합니다."
            else -> null
        }
    }

    // 이메일 형식 검증 함수
    fun isValidEmail(email: String): Boolean {
        // 이메일 형식 검증을 위한 정규식
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"  //ex) asd@kau.kr
        return email.matches(Regex(emailPattern))
    }

    fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^01[016789]-?\\d{3,4}-?\\d{4}$" //ex) 010-1234-5678
        return phone.matches(Regex(phonePattern))
    }

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,15}$" //ex) abcd1234
        return password.matches(Regex(passwordPattern))
    }

    private val repository = UserRepository()       //얘는 다른 프로퍼티가 선언되고 나서 나중에 받도록 뒤에 있어야함

    init {
        repository.observeUser(name)
    }

}