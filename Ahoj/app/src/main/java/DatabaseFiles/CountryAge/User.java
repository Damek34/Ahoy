package DatabaseFiles.CountryAge;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    int uid;
    @ColumnInfo(name = "Country")
    String country;

    @ColumnInfo(name = "Age")
    int age;

  //  public User(String country, int age) {
       // this.country = country;
       // this.age = age;

    //}


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
