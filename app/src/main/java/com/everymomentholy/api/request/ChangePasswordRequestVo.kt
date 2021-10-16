package com.everymomentholy.api.request

class ChangePasswordRequestVo {

    var userId : Int = 1

    var deviceId : String = ""

    var oldPassword : String = ""

    var newPassword : String = ""

    var confirmPassword : String = ""
}