package com.codepath.apps.tweetTweet.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by alinc on 10/3/15.
 */
public class OfflineTweets extends com.activeandroid.app.Application {


    @Table(name = "Tweets")
    public static class Tweets extends Model {
        // This is the unique id given by the server
        @Column(name = "tid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
        public long tid;
        @Column(name = "uid")
        public long uid;
        // This is a regular field
        @Column(name = "text")
        public String text;
        @Column(name = "created_at")
        public String created_at;
        // This is an association to another activeandroid model
        //@Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
        //public Users user;
        @Column(name = "retweet_count")
        public int retweet_count;
        @Column(name = "favourites_count")
        public int  favourites_count;
        @Column(name = "profile_image_url")
        public String profile_image_url;
        @Column(name = "userName")
        public String userName;
        @Column(name = "screen_name")
        public String screen_name;

        public Tweets(){
            super();
        }

        public Tweets(int tid, long uid, String text, String created_at, int retweet_count, int favourites_count, String profile_image_url, String userName, String screen_name){
            super();
            this.tid = tid;
            this.text = text;
            this.uid = uid;
            this.retweet_count = retweet_count;
            this.favourites_count = favourites_count;
            this.created_at = created_at;
            this.profile_image_url = profile_image_url;
            this.userName = userName;
            this.screen_name = screen_name;

        }

        public void truncateTable() {
            TableInfo tableInfo = Cache.getTableInfo(Tweets.class);
            ActiveAndroid.execSQL("delete from " + tableInfo.getTableName() + ";");
            ActiveAndroid.execSQL("delete from sqlite_sequence where name='"+tableInfo.getTableName()+"';");
        }
    }

   /* @Table(name = "Users")
    public static class Users extends Model {
        // This is the unique id given by the server
        @Column(name = "uid")
        public long uid;
        // This is a regular field
        @Column(name = "profile_image_url")
        public String profile_image_url;
        @Column(name = "name")
        public String name;
        @Column(name = "screen_name")
        public String screen_name;

        // Make sure to have a default constructor for every ActiveAndroid model
        public Users(){
            super();
        }

        public Users(long uid, String profile_image_url, String name, String screen_name) {
            this.uid = uid;
            this.profile_image_url = profile_image_url;
            this.name = name;
            this.screen_name = screen_name;
        }

        // Used to return items from another table based on the foreign key
        public List<Tweets> tweets() {
            return getMany(Tweets.class, "Users");
        }
    }*/
}
