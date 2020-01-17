package com.stacktivity.voiceproject.data.db

import androidx.room.*
import com.quickblox.core.helper.StringifyArrayList
import com.quickblox.users.model.QBUser

@Dao
abstract class UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun incert(user: User)

    suspend fun incertAllQbUsers(users: ArrayList<QBUser>) {
        for (qbUser in users) {
            incert(User(qbUser))
        }
    }

    @Query("SELECT * from User")
    protected abstract suspend fun getAllUsers(): List<User>

    @Query("SELECT * from User where user_ID == :userId")
    protected abstract suspend fun getUserById(userId: Int): User

    suspend fun getUserByIds(ids: ArrayList<Int>): ArrayList<User> {
        val usersList = arrayListOf<User>()
        for (id in ids)
            usersList.add(getUserById(id))
        return usersList
    }

    @Delete
    abstract suspend fun deleteUser(item: User)

    @Query("DELETE from User")
    abstract suspend fun clearDB()

    /*@Query("UPDATE User set item_count = item_count + :count where id = :id")
    suspend fun increaseCount(id: Int, count: Int)*/

    suspend fun getQbUserById(userId: Int): QBUser  {
        val qbUser = QBUser()
        val user: User = getUserById(userId)
        qbUser.fullName = user.userFullName
        qbUser.login = user.userLogin
        qbUser.id = user.userID
        qbUser.password = user.userPass

        val tags = StringifyArrayList<String>()

        tags.add(*user.userTag
            .split(",".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray())

        qbUser.tags = tags

        return qbUser
    }

    suspend fun getQbUsersByIds(usersIds: List<Int>): ArrayList<QBUser> {
        val qbUsers = ArrayList<QBUser>()

        for (userId in usersIds) {
            qbUsers.add(getQbUserById(userId))
        }
        return qbUsers
    }


    suspend fun getAllQbUsers(): ArrayList<QBUser> {
        val allQbUsers = arrayListOf<QBUser>()

        for (user in getAllUsers()) {
            allQbUsers.add(getQbUser(user))
        }

        return allQbUsers
    }

    private fun getQbUser(user: User): QBUser {
        val qbUser = QBUser()
        qbUser.fullName = user.userFullName
        qbUser.login = user.userLogin
        qbUser.id = user.userID
        qbUser.password = user.userPass

        return qbUser
    }
}