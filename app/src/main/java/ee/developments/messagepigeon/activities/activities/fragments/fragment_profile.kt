package ee.developments.messagepigeon.activities.activities.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.projemanag.R
import ee.developments.messagepigeon.activities.activities.activity.MainActivity
import ee.developments.messagepigeon.activities.activities.firebase.FireStore
import ee.developments.messagepigeon.activities.activities.models.User
import ee.developments.messagepigeon.activities.activities.utils.Constans
import kotlinx.android.synthetic.main.fragment_profile2.*
import kotlinx.android.synthetic.main.fragment_profile2.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_profile : Fragment(),View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mtvName:TextView? = null
    var name:String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view =  inflater.inflate(R.layout.fragment_profile2, container, false)

        Glide
            .with(view)
            .load(Constans.USER_IMAGE)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(view.profile_image)

        Log.e("user image", Constans.USER_IMAGE)
        if (Constans.USER_NAME != null) {
            view?.tv_name?.text = Constans.USER_NAME
        }else{
            view?.tv_name?.text = "NAME"
        }
        return view
    }

    override fun onClick(v: View?){}
}