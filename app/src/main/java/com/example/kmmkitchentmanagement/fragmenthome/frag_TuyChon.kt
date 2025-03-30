package com.example.kmmkitchentmanagement.fragmenthome

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import android.content.Context
import android.content.Intent

import android.view.View
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog
import com.example.kmmkitchentmanagement.customdialog.SetPassword
import com.example.kmmkitchentmanagement.fragment_setting.Infomation


class frag_TuyChon : Fragment() {
    private lateinit var profileLayout: LinearLayout
    private lateinit var accountOptions: LinearLayout
    private lateinit var profileImage: ImageView
    private lateinit var profileImageArrow: ImageView
    private lateinit var mExit: Button
    private lateinit var profileName: TextView
    private lateinit var ttcn: TextView
    private lateinit var tdmk: TextView
    private lateinit var logApp: TextView
    private lateinit var userViewModel: UserViewModel
    private var logoutListener: LogoutListener? = null
    private var user: NguoiDung? = null
    private var isClicked = false

    private val infoActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val userEmail = requireActivity().intent.getStringExtra("user_email")
            userEmail?.let { userViewModel.fetchUserData(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        getUserData()
        return inflater.inflate(R.layout.fragment_tuy_chon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addView(view)
    }

    private fun addView(view: View) {
        profileLayout = view.findViewById(R.id.profile_layout)
        accountOptions = view.findViewById(R.id.AccoutOptions)
        profileImage = view.findViewById(R.id.profile_image)
        profileImageArrow = view.findViewById(R.id.image_profile_arrow)
        mExit = view.findViewById(R.id.logout)
        profileName = view.findViewById(R.id.profile_name)
        ttcn = view.findViewById(R.id.ttcn)
        tdmk = view.findViewById(R.id.tdmk)
        logApp = view.findViewById(R.id.logLoading)

        profileName.text = "Không xác định"

        profileLayout.setOnClickListener { onAccountClick() }
        ttcn.setOnClickListener { openInfoFragment() }
        tdmk.setOnClickListener {
            if (!NetworkUtil.isWifiConnected(requireContext())) {
                showErrorConnectDialog()
            } else {
                user?.email?.let { SetPassword(it).show(parentFragmentManager, "setPassword") }
            }
        }
        mExit.setOnClickListener { onLogoutButtonClicked() }
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
            profileName.text = user?.name ?: "Không xác định"
            logApp.visibility = if (user != null) View.GONE else View.VISIBLE
        }
    }

    private fun onAccountClick() {
        if (!isClicked) {
            accountOptions.visibility = View.VISIBLE
            profileImageArrow.setImageResource(R.drawable.up_arrow)
        } else {
            accountOptions.visibility = View.GONE
            profileImageArrow.setImageResource(R.drawable.arrow_down_sign_to_navigate)
        }
        isClicked = !isClicked
    }

    private fun openInfoFragment() {
        user?.email?.let {
            val intent = Intent(requireActivity(), Infomation::class.java).apply {
                putExtra("user_email", it)
            }
            infoActivityResultLauncher.launch(intent)
        }
    }

    private fun onLogoutButtonClicked() {
        logoutListener?.onLogout()
    }

    private fun showErrorConnectDialog() {
        val log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!"
        ErrorDialog(log, false).show(parentFragmentManager, "errorConnectDialog")
    }
}
