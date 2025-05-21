package com.example.material.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.animation.R
import com.example.material.bean.SampleBean

class SharedElementFragment2 : Fragment() {

    companion object {
        private val EXTRA_SAMPLE: String = "sample"

        fun newInstance(sample: SampleBean?): SharedElementFragment2 {
            val args = Bundle()
            args.putParcelable(EXTRA_SAMPLE, sample)
            val fragment = SharedElementFragment2()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_sharedelement2, container, false)
        val bundle = arguments
        //以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            //如果传递有值，则获取
            val sample = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            val squareBlue = view.findViewById<ImageView>(R.id.square_blue)
            sample?.color?.let { DrawableCompat.setTint(squareBlue.drawable, it) }
        }
        return view
    }

}