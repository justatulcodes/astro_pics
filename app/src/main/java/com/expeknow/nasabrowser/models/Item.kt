package com.expeknow.nasabrowser.models

data class Item(
    val `data`: List<Data>,
    val href: String,
    val links: List<Link>
)