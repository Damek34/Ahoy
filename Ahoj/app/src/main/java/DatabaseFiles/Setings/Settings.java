package DatabaseFiles.Setings;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Settings {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "MapType")
    int mapType;

   // @ColumnInfo(name = "Age")
   // int age;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }
}
