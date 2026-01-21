package com.example.p.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(
    entities = {User.class, Message.class, Group.class, GroupMember.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class LinkUpDatabase extends RoomDatabase {
    private static LinkUpDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    public abstract GroupDao groupDao();
    public abstract GroupMemberDao groupMemberDao();

    public static synchronized LinkUpDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                LinkUpDatabase.class,
                "linkup_database"
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
