package DatabaseFiles.Setings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import DatabaseFiles.CountryAge.User;

@Dao
public interface SettingsDAO {

    @Insert
    void insertAll(Settings... settings);

    @Query("SELECT * FROM settings")
    List<Settings> getAllSettings();

    @Query("SELECT MapType FROM settings")
    int getMapType();

    @Query("UPDATE settings SET MapType= 1 WHERE id=1 ")  //normal
    void updateMapTypeToNormal();

    @Query("UPDATE settings SET MapType= 2 WHERE id=1 ")  //terrain
    void updateMapTypeToTerrain();

    @Query("UPDATE settings SET MapType= 3 WHERE id=1 ")  //satellite
    void updateMapTypeToSatellite();

    @Query("UPDATE settings SET MapType= 4 WHERE id=1 ")  //hybrid
    void updateMapTypeToTHybrid();

    @Query("SELECT EXISTS(SELECT * FROM settings)")
    Boolean isExists();
}
