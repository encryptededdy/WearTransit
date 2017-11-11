package nz.zhang.aucklandtransportwear.atapi

class ATResponse<T>(
        val status: String,
        val response: T,
        val error: String?)