//package com.example.kmmkitchentmanagement.fragmenthome
//
//class FragThemNoiDung : Fragment() {
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var collectionReference: CollectionReference
//    private lateinit var storage: FirebaseStorage
//    private lateinit var storageReference: StorageReference
//    private lateinit var linhvuclist: MutableList<String>
//    private var luanVanThumbnail: Uri? = null
//    private var luanvandocx: Uri? = null
//    private lateinit var radioXB: RadioGroup
//    private lateinit var viewDatePushed: LinearLayout
//    private lateinit var mImageButton: ImageButton
//    private lateinit var mEditTieuDe: EditText
//    private lateinit var mEditChuThich: EditText
//    private lateinit var mEditTrichDan: EditText
//    private lateinit var mEditDatePushed: EditText
//    private lateinit var mEditTacGia: EditText
//    private lateinit var mEditLinhVuc: EditText
//    private lateinit var mAddTacGia: Button
//    private lateinit var mAddLinhVuc: Button
//    private lateinit var mAddLV: Button
//    private lateinit var addFile: Button
//    private lateinit var setListener: DatePickerDialog.OnDateSetListener
//    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
//
//    private var tieuDe = ""
//    private var linhVuc = ""
//    private var moTa = ""
//    private var trichDan = ""
//    private var tacGia = ""
//    private var isPushed = true
//    private var datePushed: Date? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_themluanvan, container, false)
//        firebaseInit()
//        addView(view)
//        dataHandler()
//        onRadioClicked()
//        addImage()
//        chooseDate()
//        clickForAddTacGia()
//        clickForAddLinhVuc()
//        activityResult()
//        addLuanVan()
//        return view
//    }
//
//    private fun dataHandler() {
//        linhvuclist = mutableListOf()
//        firestore.collection("/linhvuc").document("/linhvuc")
//            .addSnapshotListener { value, error ->
//                if (error != null) return@addSnapshotListener
//                value?.get("linhvuc")?.let { linhvuc ->
//                    linhvuclist.clear()
//                    linhvuclist.addAll(linhvuc as List<String>)
//                }
//            }
//    }
//
//    private fun addView(view: View) {
//        radioXB = view.findViewById(R.id.radioXB)
//        viewDatePushed = view.findViewById(R.id.viewTGLV)
//        mImageButton = view.findViewById(R.id.btnAvaLV)
//        mEditTieuDe = view.findViewById(R.id.editTitleLV)
//        mEditChuThich = view.findViewById(R.id.editMoTaTG)
//        mEditTrichDan = view.findViewById(R.id.EditTrichDanLV)
//        mEditDatePushed = view.findViewById(R.id.editTGLV)
//        mAddTacGia = view.findViewById(R.id.btnAddTG)
//        mAddLinhVuc = view.findViewById(R.id.btnAddLinhVuc)
//        mEditTacGia = view.findViewById(R.id.editTacGiaLV)
//        mEditLinhVuc = view.findViewById(R.id.editLinhVucLV)
//        mAddLV = view.findViewById(R.id.btnAddLV)
//        addFile = view.findViewById(R.id.documentUpload)
//    }
//
//    private fun onRadioClicked() {
//        radioXB.setOnCheckedChangeListener { _, checkedId ->
//            viewDatePushed.visibility = if (checkedId == R.id.btnRadioXBTrue) View.GONE else View.VISIBLE
//        }
//    }
//
//    private fun addImage() {
//        mImageButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
//            activityResultLauncher.launch(intent)
//        }
//        addFile.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//            }
//            activityResultLauncher.launch(intent)
//        }
//    }
//
//    private fun activityResult() {
//        activityResultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                result.data?.data?.let { temp ->
//                    if (temp.toString().lowercase().contains("image")) {
//                        luanVanThumbnail = temp
//                        mImageButton.setImageURI(luanVanThumbnail)
//                    } else {
//                        luanvandocx = temp
//                        addFile.text = luanvandocx.toString()
//                    }
//                }
//            }
//    }
//
//    private fun chooseDate() {
//        val cal = Calendar.getInstance()
//        val year = cal.get(Calendar.YEAR)
//        val month = cal.get(Calendar.MONTH)
//        val day = cal.get(Calendar.DAY_OF_MONTH)
//
//        mEditDatePushed.setOnClickListener {
//            DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Dialog_MinWidth, setListener, year, month, day).apply {
//                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                show()
//            }
//        }
//
//        setListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, dayOfMonth ->
//            mEditDatePushed.setText("$dayOfMonth/${selectedMonth + 1}/$selectedYear")
//        }
//    }
//
//    private fun firebaseInit() {
//        firestore = FirebaseFirestore.getInstance()
//        collectionReference = firestore.collection("/luanvan")
//        storage = FirebaseStorage.getInstance()
//        storageReference = storage.getReference("/luanvan")
//    }
//}
