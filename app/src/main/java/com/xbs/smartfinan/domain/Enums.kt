package com.xbs.smartfinan.domain

enum class Regularity(val value: String) {
    ONCE("once"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly")
}
enum class Category(val value: String) {
    NECESSARY("necessary"),
    UNNECESSARY("unnecessary"),
}
