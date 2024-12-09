package com.example.opp_e2guana.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.opp_e2guana.FriendListAdapter
import com.example.opp_e2guana.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Userdata_viewmodel : ViewModel() {

    //라이브 데이터라고 해서 (외부)프레그먼트에서 모니터링(옵저브)할 수 있는 string임, 외부 관찰은 가능하나 함부로 못바꿈(private 반드시 걸것)
    private val name = MutableLiveData<String>("UNKNOWN")
    val show_name: LiveData<String> get() = name //이런식으로 볼 수는 있으나 외부에서 바꿀 수 없는 패턴으로 디자인해야함

    private val emailAddress = MutableLiveData<String>("Not have email")
    val show_email: LiveData<String> get() = emailAddress

    private val phoneNumber = MutableLiveData<String>("Not have phoneNumber")
    val show_phone: LiveData<String> get() = phoneNumber

    private val password = MutableLiveData<String>("error") //패스워드는 당연히 보이면 안됨!!
    val show_password: LiveData<String> get() = password          //비밀번호 변경할 때 검증 목적으로

    private val imageURL =
        MutableLiveData<String>("https://example.com/default_profile_image.jpg") // 기본 이미지 URL 설정
    val show_imageURL: LiveData<String> get() = imageURL // -MOON이 추가(2024.12.02)

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

    // Firebase Auth & Database 초기화 -MOON
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun set_name(nameData: String) {                     //이름 변경
        Log.d("name", "$nameData")
        val setName =
            name.value?.let {                 //name.value를 바꾸지 않고 리포지토리로 발로 올리게 됨. DB의존도가 더 커지는거 아닌가?
                nameData
            } ?: "UNKNOWN"

        repository.postName(setName)
    }

    fun set_email(emailData: String) {                   //이메일 변경
        Log.d("email", "$emailData")
        emailAddress.value = emailAddress.value?.let {
            emailData
        } ?: "Not have email"
    }

    fun set_phone(phone: String) {                       //전화번호 변경
        Log.d("phone", "$phone")
        phoneNumber.value = phoneNumber.value?.let {
            phone
        } ?: "Not have phoneNumber"
    }

    fun set_password(setPassword: String) {              //패스워드 변경
        Log.d("password", "$setPassword")
        password.value = password.value?.let {
            setPassword
        } ?: "Error"
    }

    fun set_imageURL(imageURLdata: String) {             //프로필이미지url변경
        Log.d("imageURL", "$imageURLdata")
        imageURL.value = imageURLdata
    }

    // Firebase Auth에 계정 생성 및 로그인, 경현이 사용하는 함수 -MOON
    fun createFirebaseUser(onComplete: (Boolean, Exception?) -> Unit) {
        val email = emailAddress.value ?: ""
        val password = this.password.value ?: ""

        // Firebase Auth에 계정 생성
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "User account create Success")
                    // Firebase Realtime Database에 데이터 업로드
                    val currentImageUrl = imageURL.value
                        ?: "https://example.com/default_profile_image.jpg" // 기본 URL 처리
                    uploadUserDataToFirebase(currentImageUrl) // String 타입 URL 전달
                    // 성공 콜백
                    onComplete(true, null)
                } else {
                    // 실패 콜백
                    Log.e("Firebase", "User account create Fail", task.exception)
                    onComplete(false, task.exception)
                }
            }
    }

    // Firebase에 사용자 정보 변경, 경현,승훈이 둘다 사용하는 함수 -MOON
    fun uploadUserDataToFirebase(imageUrl: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userMap = mapOf(
                "name" to name.value,
                "email" to emailAddress.value,
                "phone" to phoneNumber.value,
                "profileImageUrl" to imageUrl // 이미지 URL 추가
            )

            database.child("users").child(userId).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "User data with profile upload Success")
                    } else {
                        Log.e("Firebase", "User data with profile upload Fail", task.exception)
                    }
                }
        } else {
            Log.e("Firebase", "No user found in Auth")
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

            set_name(name)
            set_email(email)
            set_phone(phone)

            Log.d("Firebase", "User data fetched: Name=$name, Phone=$phone")
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch user data", it)
        }
    }

    // Firebase에서 다른사용자(친구)의 데이터를 가져오는 함수
    fun fetchFriendsData(onComplete: (List<FriendListAdapter.Friend_Data>) -> Unit) {
        database.child("users").get().addOnSuccessListener { snapshot ->
            val friends = mutableListOf<FriendListAdapter.Friend_Data>()
            for (child in snapshot.children) {
                val userId = child.key ?: continue
                val name = child.child("name").value as? String ?: "Unknown"
                val phone = child.child("phone").value as? String ?: "No Phone"
                friends.add(FriendListAdapter.Friend_Data(userId, name, phone))
            }
            friendList.value = friends // ViewModel 변수에 데이터 저장
            onComplete(friends) // 데이터를 Fragment로 전달
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch friends", it)
            onComplete(emptyList()) // 실패 시 빈 리스트 반환
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