package com.example.finalassignment.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserRegistration::class],version = 1, exportSchema = false)
abstract class UserRegistrationDatabase: RoomDatabase() {

    abstract fun userRegistrationDAO(): UserRegistrationDAO

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