package com.ash.note.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "noteTable")
public class Note implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public Note() {

    }




    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "subTitle")
    public String subTitle;

    @ColumnInfo(name = "bgColor")
    public String bgColor;

    @ColumnInfo(name = "charCount")
    public int charNumber;

    @ColumnInfo(name = "isPinned")
    public boolean isPinned;

    @ColumnInfo(name = "imagePath")
    public String imagePath;


    protected Note(Parcel in) {
        uid = in.readInt();
        title = in.readString();
        content = in.readString();
        date = in.readString();
        subTitle = in.readString();
        bgColor = in.readString();
        charNumber = in.readInt();
        isPinned = in.readByte() != 0;
        imagePath = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }


    public int getUid()
    {
        return uid;
    }

    public String getDate()
    {
        return date;
    }

    public int getCharNumber() {
        return charNumber;
    }

    public void setCharNumber(int charNumber) {
        this.charNumber = charNumber;
    }


    public String getTitle() {
        return title;
    }

    public String getContent()
    {
        return content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeString(subTitle);
        parcel.writeString(bgColor);
        parcel.writeInt(charNumber);
        parcel.writeByte((byte) (isPinned ? 1 : 0));
        parcel.writeString(imagePath);
    }
}
