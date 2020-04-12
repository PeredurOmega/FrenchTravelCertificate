package com.pi.attestation.objects

import java.io.Serializable

class Reason (val shortName: String, val fullDescription: String, val icon: Int, val color: Int,
              val id: Int)
    : Serializable