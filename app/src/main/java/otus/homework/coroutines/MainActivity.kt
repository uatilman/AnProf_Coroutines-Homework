package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import otus.homework.coroutines.di.DiContainer
import otus.homework.coroutines.presentation.CatsPresenter
import otus.homework.coroutines.presentation.CatsViewModel
import otus.homework.coroutines.ui.CatsView

class MainActivity : AppCompatActivity() {

    private lateinit var catsPresenter: CatsPresenter
    private lateinit var catsViewModel: CatsViewModel

    private val diContainer = DiContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        // Выбор реализации на основе параметров сборки (Product Flavors)
        if (BuildConfig.IMPL_TYPE == "PRESENTER") {
            initPresenter(view)
        } else {
            initViewModel(view)
        }
    }

    private fun initPresenter(view: CatsView) {
        catsPresenter = CatsPresenter(diContainer.service)
        view.presenter = catsPresenter
        catsPresenter.attachView(view)
        catsPresenter.onInitComplete()
    }

    private fun initViewModel(view: CatsView) {
        catsViewModel = CatsViewModel(diContainer.service)
        view.viewModel = catsViewModel
        catsViewModel.state.observe(this) { result ->
            view.populate(result)
        }
        catsViewModel.onInitComplete()
    }

    override fun onStop() {
        if (this::catsPresenter.isInitialized) {
            if (isFinishing) {
                catsPresenter.detachView()
            }
            catsPresenter.onStop()
        }
        super.onStop()
    }
}