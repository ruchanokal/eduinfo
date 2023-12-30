package com.edu.info.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.edu.info.R
import com.edu.info.activities.SignActivity
import com.edu.info.databinding.FragmentMainBinding
import com.edu.info.model.Student
import com.edu.info.model.StudentInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class MainFragment : Fragment() {

    private val TAG = "MainFragment"
    private var binding : FragmentMainBinding? = null
    var reference : ListenerRegistration? = null
    private lateinit var alertDialog: AlertDialog.Builder
    lateinit var mAuth: FirebaseAuth
    lateinit var user : FirebaseUser
    private lateinit var db : FirebaseFirestore
    var loginType = ""
    var username = ""
    var userUid = ""
    var dialog = activity?.let { Dialog(it) }
    private var studentList = arrayListOf<Student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = activity?.let { Dialog(it) }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userUid = mAuth.currentUser?.uid.toString()

        backButton()
        login()
        signOut()

    }

    private fun backButton() {
        val callback = object  : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun signOut() {

        binding?.signOutButton?.setOnClickListener {

            alertDialog = AlertDialog.Builder(requireContext())

            alertDialog.setTitle(getString(R.string.exitstring))
            alertDialog.setMessage(getString(R.string.exit_desc))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(getString(R.string.exitstring)) { dialog,which ->

                reference?.remove()

                mAuth.signOut()
                val intent = Intent(requireActivity(), SignActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }.setNeutralButton(getString(R.string.cancelstring)) { dialog,which ->


            }

            alertDialog.show()


        }

    }

    private fun login() {

        val intent = requireActivity().intent
        val definiteNumber = intent.getIntExtra("definite",0)

        Log.i(TAG,"definiteNumber --> " + definiteNumber)

        if ( definiteNumber == 1) {

            loginType = "student"
            binding!!.teacherLayout.visibility = View.GONE
            binding!!.studentLayout.visibility = View.VISIBLE

            username = intent.getStringExtra("username")!!
            binding?.usernameText?.setText(username)
            Log.i(TAG,"new student --> " + username)
            getMyNotes(userUid)

        } else if ( definiteNumber == 2) {

            loginType = "student"
            binding!!.teacherLayout.visibility = View.GONE
            binding!!.studentLayout.visibility = View.VISIBLE

            Log.i(TAG,"student entered --> " + mAuth.currentUser?.email)
            getMyNotes(userUid)
        } else if ( definiteNumber == 3) {

            loginType = "teacher"
            binding!!.studentLayout.visibility = View.GONE
            binding!!.teacherLayout.visibility = View.VISIBLE

            username = intent.getStringExtra("username")!!
            binding?.usernameText?.setText(username)
            Log.i(TAG,"new teacher --> " + username)
            getStudentScores()

        } else if ( definiteNumber == 4) {

            loginType = "teacher"
            binding!!.studentLayout.visibility = View.GONE
            binding!!.teacherLayout.visibility = View.VISIBLE

            Log.i(TAG,"teacher entered --> " + mAuth.currentUser?.email)
            getStudentScores()

        }

    }

    private fun getMyNotes(userUid: String) {

        db.collection("Student").document(userUid).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                if (value != null) {

                    if (value.exists()) {

                        val username = value["username"] as? String
                        val mathScore = value["math"] as? String
                        val geoScore = value["geo"] as? String
                        val sociologyScore = value["soc"] as? String
                        val physicsScore = value["phy"] as? String
                        val literatureScore = value["lit"] as? String
                        val chemistryScore = value["che"] as? String
                        val economicsScore = value["eco"] as? String
                        val absence = value["absence"] as? String

                        username?.let {
                            binding!!.usernameText.text = it
                        }

                        mathScore?.let {
                            binding!!.mathScoreText.text = it
                        }

                        geoScore?.let {
                            binding!!.geographyScoreText.text = it
                        }

                        sociologyScore?.let {
                            binding!!.sociologyScoreText.text = it
                        }

                        physicsScore?.let {
                            binding!!.physicsScoreText.text = it
                        }

                        literatureScore?.let {
                            binding!!.literatureScoreText.text = it
                        }

                        chemistryScore?.let {
                            binding!!.chemistryScoreText.text = it
                        }

                        economicsScore?.let {
                            binding!!.economicsScoreText.text = it
                        }

                        absence?.let {

                            if (it.equals("0") || it.equals("1")){
                                binding!!.absenteeismText.text = "${it} day"
                            } else {
                                binding!!.absenteeismText.text = "${it} days"
                            }

                        }

                    }

                }


            }

        }
    }

    private fun getStudentScores() {

        db.collection("Student").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents
                        studentList.clear()
                        for (document in documents){

                            val username = document.getString("username")
                            val mathScore = document.getString("math")
                            val geoScore = document.getString("geo")
                            val sociologyScore = document.getString("soc")
                            val physicsScore = document.getString("phy")
                            val literatureScore = document.getString("lit")
                            val chemistryScore = document.getString("che")
                            val economicsScore = document.getString("eco")
                            val absence = document.getString("absence")
                            val userUid = document.id

                            username?.let {
                                val info = StudentInfo(mathScore,physicsScore,chemistryScore,economicsScore,literatureScore,geoScore,sociologyScore,absence)
                                val student = Student(username,userUid,info)
                                studentList.add(student)
                            }

                        }


                        val adapter = StudentAdapter(requireContext(), android.R.layout.simple_spinner_item, studentList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding!!.spinner.adapter = adapter

                        binding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                                val selectedStudent = studentList[position]
                                val selectedName = selectedStudent.username
                                val selectedUserUid = selectedStudent.userUid
                                val selectedItem = binding!!.spinner.selectedItem.toString()

                                Log.i(TAG,"selected position: " + position)
                                Log.i(TAG,"selected userUid: " + selectedUserUid)
                                Log.i(TAG,"selected name: " + selectedName)

                                Log.i(TAG,"selected math: " + selectedStudent.info.math)
                                Log.i(TAG,"selected phy: " + selectedStudent.info.phy)


                                if (selectedStudent.info.math != null){
                                    binding!!.mathScoreEditText.setText(selectedStudent.info.math)
                                } else {
                                    binding!!.mathScoreEditText.setText("")
                                    binding!!.mathScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.phy != null){
                                    binding!!.physicsScoreEditText.setText(selectedStudent.info.phy)
                                } else {
                                    binding!!.physicsScoreEditText.setText("")
                                    binding!!.physicsScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.che != null){
                                    binding!!.chemistryScoreEditText.setText(selectedStudent.info.che)
                                } else {
                                    binding!!.chemistryScoreEditText.setText("")
                                    binding!!.chemistryScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.geo != null){
                                    binding!!.geographyScoreEditText.setText(selectedStudent.info.geo)
                                } else {
                                    binding!!.geographyScoreEditText.setText("")
                                    binding!!.geographyScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.lit != null){
                                    binding!!.literatureScoreEditText.setText(selectedStudent.info.lit)
                                } else {
                                    binding!!.literatureScoreEditText.setText("")
                                    binding!!.literatureScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.eco != null){
                                    binding!!.economicsScoreEditText.setText(selectedStudent.info.eco)
                                } else {
                                    binding!!.economicsScoreEditText.setText("")
                                    binding!!.economicsScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.soc != null){
                                    binding!!.sociologyScoreEditText.setText(selectedStudent.info.soc)
                                } else {
                                    binding!!.sociologyScoreEditText.setText("")
                                    binding!!.sociologyScoreEditText.setHint("Enter score..")
                                }

                                if (selectedStudent.info.absence != null){
                                    binding!!.absenteeismEditText.setText(selectedStudent.info.absence)
                                } else {
                                    binding!!.absenteeismEditText.setText("")
                                    binding!!.absenteeismEditText.setHint("Enter absence..")
                                }

                                sendScores(selectedStudent.userUid)


                            }

                            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                // Hiçbir öğe seçilmediğinde çalışacak kodlar
                                Log.i(TAG,"no selectedItem: " + adapterView?.selectedItem)
                            }
                        }
                    }
                }

            }


        }

    }

    private fun sendScores(selectedUserUid : String) {

        binding!!.sendButton.setOnClickListener {

            val soc = binding!!.sociologyScoreEditText.text.toString()
            val eco = binding!!.economicsScoreEditText.text.toString()
            val math = binding!!.mathScoreEditText.text.toString()
            val phy = binding!!.physicsScoreEditText.text.toString()
            val che = binding!!.chemistryScoreEditText.text.toString()
            val geo = binding!!.geographyScoreEditText.text.toString()
            val lit = binding!!.literatureScoreEditText.text.toString()
            val absence = binding!!.absenteeismEditText.text.toString()

            if (soc.isEmpty()
                || eco.isEmpty()
                || math.isEmpty()
                || phy.isEmpty()
                || che.isEmpty()
                || geo.isEmpty()
                || absence.isEmpty()
                || lit.isEmpty()) {

                Toast.makeText(requireContext(),"Please fill in the required fields",Toast.LENGTH_LONG).show()

            } else {

                val hashMap = hashMapOf<String,Any>()
                hashMap.put("soc",soc)
                hashMap.put("math",math)
                hashMap.put("eco",eco)
                hashMap.put("phy",phy)
                hashMap.put("che",che)
                hashMap.put("geo",geo)
                hashMap.put("lit",lit)
                hashMap.put("absence",absence)

                db.collection("Student").document(selectedUserUid).update(hashMap).addOnSuccessListener {
                    Toast.makeText(requireContext(),"Send it successfully!",Toast.LENGTH_LONG).show()
                }

            }

        }

    }

    class StudentAdapter(context: Context, resource: Int, private val itemList: List<Student>) :
        ArrayAdapter<Student>(context, resource, itemList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val item = itemList.get(position)
            val usernameTextView = view.findViewById<TextView>(android.R.id.text1)
            usernameTextView.text = item.username
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val item = itemList[position]
            val usernameTextView = view.findViewById<TextView>(android.R.id.text1)
            usernameTextView.text = item.username
            return view
        }
    }



    private fun getNotes(loginType : String) {

        if (loginType.equals("student")){

            db.collection("Student").document(userUid).addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(requireContext(),"Error: ${error.localizedMessage}",Toast.LENGTH_LONG).show()
                } else {

                    if (value != null) {

                        if (value.exists()){

                            val mathScore = value["math"] as? String
                            val geoScore = value["geo"] as? String
                            val sociologyScore = value["soc"] as? String
                            val physicsScore = value["phy"] as? String
                            val literatureScore = value["lit"] as? String
                            val chemistryScore = value["che"] as? String
                            val economicsScore = value["eco"] as? String
                            val absence = value["absence"] as? String
                            val username = value["username"] as? String

                            username?.let {
                                binding!!.usernameText.text = it
                            }
                            mathScore?.let {
                                binding!!.mathScoreText.text = it
                            }
                            geoScore?.let {
                                binding!!.geographyScoreText.text = it
                            }
                            sociologyScore?.let {
                                binding!!.sociologyScoreText.text = it
                            }
                            physicsScore?.let {
                                binding!!.physicsScoreText.text = it
                            }
                            literatureScore?.let {
                                binding!!.literatureScoreText.text = it
                            }
                            chemistryScore?.let {
                                binding!!.chemistryScoreText.text = it
                            }
                            economicsScore?.let {
                                binding!!.economicsScoreText.text = it
                            }
                            absence?.let {
                                binding!!.absenteeismText.text = "${it} day"
                            }


                        }

                    }
                }

            }


        } else {




        }




    }

    private fun getUsername(who: String) {

    }



}