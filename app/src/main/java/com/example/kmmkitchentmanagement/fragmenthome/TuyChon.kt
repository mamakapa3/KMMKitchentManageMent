package com.example.kmmkitchentmanagement.fragmenthome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog
import com.example.kmmkitchentmanagement.customdialog.SetPassword
import com.example.kmmkitchentmanagement.fragment_setting.Infomation
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel

class TuyChon : Fragment() {

    private lateinit var profileLayout: LinearLayout
    private lateinit var accountOptions: LinearLayout
    private lateinit var profileImage: ImageView
    private lateinit var profileImageArrow: ImageView
    private lateinit var mExit: Button
    private var isClicked = false
    private lateinit var profileName: TextView
    private lateinit var ttcn: TextView
    private lateinit var tdmk: TextView
    private lateinit var logApp: TextView

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var logoutListener: LogoutListener? = null
    private lateinit var user: NguoiDung

    private lateinit var infoActivityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        infoActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = requireActivity().intent
                val userEmail = intent.getStringExtra("user_email")
                userViewModel.fetchUserData(userEmail)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUserData()
        val view = inflater.inflate(R.layout.fragment_tuy_chon, container, false)
        addView(view)
        return view
    }

    private fun addView(view: View) {
        profileLayout = view.findViewById(R.id.profile_layout)
        accountOptions = view.findViewById(R.id.AccoutOptions)
        profileImage = view.findViewById(R.id.profile_image)
        profileImageArrow = view.findViewById(R.id.image_profile_arrow)
        mExit = view.findViewById(R.id.logout)
        profileName = view.findViewById(R.id.profile_name)
        profileName.text = "Không xác định"
        ttcn = view.findViewById(R.id.ttcn)
        tdmk = view.findViewById(R.id.tdmk)
        logApp = view.findViewById(R.id.logLoading)

        profileLayout.setOnClickListener {
            onAccountClick(it)
        }

        ttcn.setOnClickListener {
            openInfoFragment()
        }

        tdmk.setOnClickListener {
            if (!context?.let { it1 -> NetworkUtil.isWifiConnected(it1) }!!) {
                showErrorConnectDialog()
            } else {
                val setPassword = SetPassword(user.email)
                setPassword.show(parentFragmentManager, "setPassword")
            }
        }

        mExit.setOnClickListener {
            onLogoutButtonClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutListener) {
            logoutListener = context
        } else {
            throw RuntimeException("$context must implement LogoutListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        logoutListener = null
    }

    private fun getUserData() {
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            this.user = user
            if (user != null) {
                profileName.text = user.name
                logApp.visibility = View.GONE
            } else {
                profileName.text = "Không xác định"
                logApp.visibility = View.VISIBLE
            }
        }
    }

    private fun onAccountClick(view: View) {
        if (!isClicked) {
            accountOptions.visibility = View.VISIBLE
            profileImageArrow.setImageResource(R.drawable.up_arrow)
            isClicked = true
        } else {
            accountOptions.visibility = View.GONE
            profileImageArrow.setImageResource(R.drawable.arrow_down_sign_to_navigate)
            isClicked = false
        }
    }

    private fun openInfoFragment() {
        val intent = Intent(requireActivity(), Infomation::class.java)
        intent.putExtra("user_email", user.email)
        infoActivityResultLauncher.launch(intent)
    }

    private fun onLogoutButtonClicked() {
        logoutListener?.onLogout()
    }

    private fun showErrorConnectDialog() {
        val log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!"
        val errorDialog = ErrorDialog(log, false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }
}
