package com.example.autotapper.navigation

import android.app.Application
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.autotapper.R
import com.example.autotapper.presentation.activities.MainActivity
import dagger.hilt.android.scopes.FragmentScoped

const val ARG_SCREEN = "SCREEN"

class MainNavigator(
    application: Application
): AndroidViewModel(application), Navigator {

    val whenActivityActive = MainActivityActions()

    private val _result = MutableLiveData<Event<Any>>()
    val result: LiveData<Event<Any>> = _result

    override fun launch(screen: BaseScreen, addToBackStack: Boolean , aboveAll: Boolean) = whenActivityActive{
        it as MainActivity
        if (!aboveAll)launchFragment(it, screen, addToBackStack)
        else launchFragment(it, screen, addToBackStack, R.id.fragmentMainContainer)
    }


    override fun goBack(result: Any?) = whenActivityActive{
        if (result != null){
            _result.value = Event(result)
        }
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

    override fun activityScope(block: (AppCompatActivity) -> Unit) = whenActivityActive{
        block(it)
    }

    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = false, @IdRes idFragment: Int = R.id.fragmentContainer){
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.replace(idFragment, fragment).commit()
    }

}