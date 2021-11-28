package com.example.finalassignment.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserRegistration::class, Transaction::class, ActiveUser::class],version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class UserRegistrationDatabase: RoomDatabase() {

    abstract fun userRegistrationDAO(): UserRegistrationDAO
    abstract fun transactionDAO(): TransactionDAO
    abstract fun activeUserDAO(): ActiveUserDAO

    companion object{
        @Volatile
        private var INSTANCE: UserRegistrationDatabase? = null

        fun getDatabase(context: Context): UserRegistrationDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null)
            {
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserRegistrationDatabase::class.java,
                    "user_registration_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}