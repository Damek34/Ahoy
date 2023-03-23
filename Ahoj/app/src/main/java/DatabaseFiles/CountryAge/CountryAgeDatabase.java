package DatabaseFiles.CountryAge;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = User.class, version = 1)
public abstract class CountryAgeDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
}
