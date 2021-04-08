package io.github.syex.skadi.sample

import kotlinx.coroutines.delay

class LoadMoviesUseCase {

    suspend fun execute(): List<MovieModel> {
        // Simulate some network delay
        delay(2500)

        return listOf(
            MovieModel(
                "Batman",
                "https://images-na.ssl-images-amazon.com/images/I/41KexDwgESL._AC_.jpg"
            ),
            MovieModel(
                "Superman",
                "https://images-na.ssl-images-amazon.com/images/I/51KtPqh3nkL.jpg"
            ),
            MovieModel(
                "Matrix",
                "https://images-na.ssl-images-amazon.com/images/I/91BKjFYwvoL._SY450_.jpg"
            ),
            MovieModel(
                "Avengers",
                "https://images-na.ssl-images-amazon.com/images/I/71wV2rzkFwL._AC_SL1022_.jpg"
            ),
            MovieModel(
                "Simpsons",
                "https://images-na.ssl-images-amazon.com/images/I/41AUWfJN%2BJL._AC_.jpg"
            )
        )
    }
}