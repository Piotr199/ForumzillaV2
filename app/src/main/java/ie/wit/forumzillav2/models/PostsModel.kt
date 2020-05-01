package ie.wit.forumzillav2.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Post(var postID:String = "",
                var postTitle:String = "",
                var postContent:String="",
                var author:String="",
                var likes: Int = 0,
                var date: String ="",
                var postCategory: String ="" //sport,comedy,news
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postID" to postID,
            "postTitle" to postTitle,
            "postContent" to postContent,
            "author" to author,
            "likes" to likes,
            "date" to date,
            "postCategory" to postCategory
        )
    }
}