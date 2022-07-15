package es.uva.retobici.frontend.ui.viewmodels.states

import es.uva.retobici.frontend.domain.model.User

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: Int? = null
)