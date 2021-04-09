package app.simple.inure.ui.app

import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.decorations.views.Pie
import app.simple.inure.decorations.views.TypeFaceTextView
import app.simple.inure.dialogs.app.Information
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.glide.util.AppIconExtensions.loadAppIcon
import app.simple.inure.packagehelper.PackageUtils
import app.simple.inure.util.FileSizeHelper.getDirectoryLength
import app.simple.inure.viewmodels.AppSize


class AppInfo : ScopedFragment() {

    private lateinit var icon: ImageView

    private lateinit var name: TypeFaceTextView
    private lateinit var packageId: TypeFaceTextView
    private lateinit var appInformation: DynamicRippleTextView
    private lateinit var storage: DynamicRippleTextView
    private lateinit var pie: Pie

    private lateinit var applicationInfo: ApplicationInfo
    private val model: AppSize by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_app_info, container, false)

        icon = view.findViewById(R.id.fragment_app_info_icon)
        name = view.findViewById(R.id.fragment_app_name)
        packageId = view.findViewById(R.id.fragment_app_package_id)
        appInformation = view.findViewById(R.id.app_info_information_tv)
        storage = view.findViewById(R.id.app_info_storage_tv)
        pie = view.findViewById(R.id.pie)

        applicationInfo = requireArguments().getParcelable("application_info")!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pie.value = 270F

        model.getTotalAppSize().observe(requireActivity(), {
            val x = applicationInfo.sourceDir.getDirectoryLength().toDouble() / it.toDouble() * 100.0
            println(x * (360.0 / 100.0))
            pie.value = (x * (360.0 / 100.0)).toFloat()
        })

        icon.transitionName = requireArguments().getString("transition_name")
        icon.loadAppIcon(requireContext(), applicationInfo.packageName)
        startPostponedEnterTransition()

        name.text = applicationInfo.name
        packageId.text = PackageUtils.getApplicationVersion(requireContext(), applicationInfo)

        appInformation.setOnClickListener {
            Information.newInstance(applicationInfo)
                    .show(childFragmentManager, "information")
        }

        storage.setOnClickListener {
            Storage.newInstance(applicationInfo)
                    .show(childFragmentManager, "storage")
        }
    }

    override fun onPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        /* no-op */
    }

    companion object {
        fun newInstance(applicationInfo: ApplicationInfo, transitionName: String): AppInfo {
            val args = Bundle()
            args.putParcelable("application_info", applicationInfo)
            args.putString("transition_name", transitionName)
            val fragment = AppInfo()
            fragment.arguments = args
            return fragment
        }
    }
}
