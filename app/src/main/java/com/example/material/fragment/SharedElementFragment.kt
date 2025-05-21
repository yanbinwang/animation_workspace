package com.example.material.fragment

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.animation.R
import com.example.material.bean.SampleBean

class SharedElementFragment : Fragment() {

    companion object {
        private val EXTRA_SAMPLE: String = "sample"

        fun newInstance(sample: SampleBean?): SharedElementFragment {
            val args = Bundle()
            args.putParcelable(EXTRA_SAMPLE, sample)
            val fragment = SharedElementFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_sharedelement, container, false)
        val bundle = arguments
        //以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            //如果传递有值，则获取
            val sample = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            val squareBlue = view.findViewById<ImageView>(R.id.square_blue)
            sample?.color?.let { DrawableCompat.setTint(squareBlue.drawable, it) }
            view.findViewById<View>(R.id.sample2_button1).setOnClickListener {
                addNextFragment(sample, squareBlue, false)
            }
            view.findViewById<View>(R.id.sample2_button2).setOnClickListener {
                addNextFragment(sample, squareBlue, true)
            }
        }
        return view
    }

    private fun addNextFragment(sample: SampleBean?, squareBlue: ImageView, overlap: Boolean) {
        val sharedElementFragment2 = SharedElementFragment2.newInstance(sample)
        val slideTransition = Slide(Gravity.END)
        slideTransition.setDuration(500)
        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.setDuration(500)
        sharedElementFragment2.enterTransition = slideTransition
        sharedElementFragment2.allowEnterTransitionOverlap = overlap
        sharedElementFragment2.allowReturnTransitionOverlap = overlap
        sharedElementFragment2.sharedElementEnterTransition = changeBoundsTransition
        fragmentManager?.beginTransaction()
            ?.replace(R.id.sample2_content, sharedElementFragment2)
            ?.addToBackStack(null)
            ?.addSharedElement(squareBlue, "square_blue")
            ?.commit()
    }

}