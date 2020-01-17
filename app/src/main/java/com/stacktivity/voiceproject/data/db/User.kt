package com.stacktivity.voiceproject.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.quickblox.core.helper.StringifyArrayList
import com.quickblox.users.model.QBUser
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "user_ID") var userID: Int,
    @ColumnInfo(name = "user_full_name") var userFullName: String,
    @ColumnInfo(name = "user_login") var userLogin: String,
    @ColumnInfo(name = "user_pass ") var userPass: String?,
    @ColumnInfo(name = "user_tag") var userTag: String
): Serializable {

    @Ignore
    constructor(qbUser: QBUser): this(
        null, qbUser.id, qbUser.fullName,
        qbUser.login, qbUser.password,
        qbUser.tags.itemsAsString)

    @Ignore
    constructor(login: String, pass: String, fullName: String): this(
        null, -1, fullName, login, pass, "")

    @Ignore
    fun getQbUser(): QBUser {
        val qbUser = QBUser(userLogin, userPass)
        qbUser.id = userID

        // Translate to utf-8, since the full name can contain UTF-16 characters,
        // and QuickBlox does not support it
        qbUser.fullName = String(userFullName.toByteArray(Charsets.UTF_8), Charsets.UTF_8)

        val tags: StringifyArrayList<String> = StringifyArrayList()
        if (userTag.isNotEmpty()) {
            tags.add(*userTag.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }

        qbUser.tags = tags

        return qbUser
    }
}