package ru.myitschool.todo.utils.exceptions

class NotFoundException(message:String):Exception(message){
    constructor():this("Not found")
}