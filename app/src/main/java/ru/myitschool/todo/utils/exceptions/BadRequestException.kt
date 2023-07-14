package ru.myitschool.todo.utils.exceptions

class BadRequestException(message:String): Exception(message){
    constructor():this("Bad request")
}