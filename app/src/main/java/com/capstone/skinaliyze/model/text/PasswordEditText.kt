package com.capstone.skinaliyze.model.text


import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewParent
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import com.capstone.skinaliyze.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (parentLayout == null) findParentTextInputLayout()
                if (s.isNullOrEmpty() || s.length < 8) {
                    parentLayout?.error = context.getString(R.string.error_password)
                } else {
                    parentLayout?.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun findParentTextInputLayout() {
        var parent: ViewParent? = parent
        while (parent != null && parent !is TextInputLayout) {
            parent = parent.parent
        }
        if (parent is TextInputLayout) {
            parentLayout = parent
        }
    }
}
