package com.example.autotapper.navigation

import android.app.Application
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.autotapper.R
import com.example.autotapper.presentation.activities.MainActivity

const val ARG_SCREEN = "SCREEN"

class MainNavigator(
    application: Application
): AndroidViewModel(application), Navigator {

    val whenActivityActive = MainActivityActions()

    /**
     * Launches fragment from screen
     *
     * @param screen -> Screen of a need fragment
     * @param addToBackStack -> if we need to add fragment in back stack
     * @param aboveAll -> if we need to launch fragment above all other fragments
     */
    override fun launch(screen: BaseScreen, addToBackStack: Boolean , aboveAll: Boolean) = whenActivityActive{
        it as MainActivity
        if (!aboveAll)launchFragment(it, screen, addToBackStack)
        else launchFragment(it, screen, addToBackStack, R.id.fragmentMainContainer)
    }

    /**
     * Returns us to previous fragment
     *
     * @param result -> result to previous fragment
     */
    override fun goBack() = whenActivityActive{
        it.onBackPressedDispatcher.onBackPressed()
    }

    override fun onCleared() {
        super.onCleared()
        whenActivityActive.clear()
    }

    override fun toast(messageRes: Int) {
        Toast.makeText(getApplication(), messageRes, Toast.LENGTH_LONG).show()
    }

    override fun toast(messageString: String) {
        Toast.makeText(getApplication(), messageString, Toast.LENGTH_LONG).show()
    }

    /**
     * Scope which let us do something with Activity Context
     */
    override fun activityScope(block: (AppCompatActivity) -> Any) = whenActivityActive{
        block(it)
    }

    /**
     * Launches fragment from MainActivity
     *
     * @param activity -> Our main activity
     * @param screen -> Screen of a need fragment
     * @param addToBackStack -> If we need to add fragment in back stack
     * @param idContainer -> Container where we launch the fragment
     */
    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = false, @IdRes idContainer: Int = R.id.fragmentContainer){
        val fragment = screen.javaClass.enclosingClass.getDeclaredConstructor().newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.replace(idContainer, fragment).commit()
    }

}