package DatabaseFiles.CountryAge;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import DatabaseFiles.CountryAge.User;

@Dao
public interface UserDAO {
    @Insert
    void insertAll(User... users);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT Country FROM user")
    String getCountry();

    @Query("SELECT EXISTS(SELECT * FROM user)")
    Boolean isExists();

}
