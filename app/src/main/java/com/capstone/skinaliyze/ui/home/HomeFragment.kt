package com.capstone.skinaliyze.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.skinaliyze.databinding.FragmentHomeBinding
import com.capstone.skinaliyze.ui.detail.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw NullPointerException("Binding is null")

    private val homeViewModel: HomeViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userEmail = auth.currentUser?.email
        userEmail?.let {
            fetchUserName(it)
        }

        productAdapter = ProductAdapter(emptyList()) { selectedItem ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", selectedItem.id.toString())
                putExtra("PRODUCT_TITLE", selectedItem.title)
                putExtra("PRODUCT_DESCRIPTION", selectedItem.description)
                putExtra("PRODUCT_FOTO", selectedItem.foto)
                putExtra("PRODUCT_HOW_TO_USE", ArrayList(selectedItem.howToUse ?: emptyList()))
                putExtra("PRODUCT_SKIN_TYPE", ArrayList(selectedItem.skinType ?: emptyList()))
            }
            startActivity(intent)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }

        homeViewModel.itemRow.observe(viewLifecycleOwner, Observer { productList ->
            productAdapter.updateList(productList)

            binding.recyclerView.visibility = if (productList.isEmpty()) View.GONE else View.VISIBLE
            binding.lottieAnimation.visibility = if (productList.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                homeViewModel.filterProducts(query)
            }
        })

        binding.cancelButton.setOnClickListener {
            binding.searchBar.text.clear()
            homeViewModel.filterProducts("")
        }

        return root
    }

    private fun fetchUserName(userEmail: String) {
        database.child("users").orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                        val username = user?.username ?: "User"

                        if (isAdded) {
                            binding.greetingText.text = "Hi, $username"
                        }
                    } else {
                        if (isAdded) {
                            binding.greetingText.text = "Hi, User"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        binding.greetingText.text = "Error fetching user data"
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
