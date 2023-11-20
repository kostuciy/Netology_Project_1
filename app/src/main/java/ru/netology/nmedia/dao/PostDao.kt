package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun changeContentById(id: Long, content: String)

    @Query("UPDATE PostEntity SET videoAttachment = :videoLink WHERE id = :id")
    fun changeVideoAttachmentById(id: Long, videoLink: String?)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """
    )
    fun updateLikesById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        shares = shares + 1
        WHERE id = :id
    """
    )
    fun updateSharesById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun deleteById(id: Long)

    //
    fun savePost(post: PostEntity) =
        if (post.id == 0L) insert(post) // id = 0 => new post, that needs to be added
        else with(post) {// other posts - already existing ones, that need to be updated in db
            changeContentById(post.id, post.content)
            changeVideoAttachmentById(post.id, post.videoAttachment)
        }
}