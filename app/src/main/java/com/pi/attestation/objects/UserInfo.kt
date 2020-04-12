package com.pi.attestation.objects

import java.io.Serializable

class UserInfo(val firstName: String?, val lastName: String?, val birthDate: String?,
               val birthPlace: String?, val address: String?, val city: String?,
               val postalCode: String?) : Serializable