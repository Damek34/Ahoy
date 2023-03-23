package DatabaseFiles.Setings;


import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = Settings.class, version = 1)
public abstract class SettingsDatabase extends RoomDatabase {
    public abstract SettingsDAO settingsDAO();
}
