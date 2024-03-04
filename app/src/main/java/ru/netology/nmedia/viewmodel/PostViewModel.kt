package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

//    *** WHEN USING LIVEDATA IN DAO AND REPOSITORY ***
//    gets LiveData<List<Post>>) from repository and transforms to LiveData with FeedModel
//    (has info about current list in local database)
//    * LiveData complete dependence:
//      local database (PostEntities) -> repository (Posts) -> view model (FeedModel with posts' info)
//
//    *** WHEN USING FLOW IN DAO AND REPOSITORY ***
//    Flow from from repository from dao constantly returns new post list,
//    (flow starts as .asLiveData() has terminal operator that starts its work)
//    so data in VM receives those changes (like normal liveData), which results
//    in constant update of postList (and ui because of observable) controlled by server
    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authState
        .flatMapLatest { auth ->
            repository.data.map { posts ->
                FeedModel(
                    posts.map { it.copy(ownedByMe = auth.id == it.authorId) },
                    posts.isEmpty()
                )
            }
        }.asLiveData()

//    has info about current app state while interacting with server
//    (if error occurred, or if posts are still loading from server)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

//    both map and switchMap result changes when underlying live data changes,
//    but switchMap is more suited for time-consuming operations
//    (flows from getNewerCount get disposed when list of posts changes
//    as they no longer have observers)
    val newerCount: LiveData<Int> = data.switchMap {
    repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
        .asLiveData(Dispatchers.Default)
    }

    init {
        loadPosts()
    }

//    launching from viewModelScope (as retrofit and room are adapted to launch from MainScope)
//    repository.getAll() results in all LiveData chain changing so database receives
//    data from server and shows it on screen (with observer on view model's data)
    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
//                    causes chain reaction of LiveData's from db to repository to view model
//                    which causes observer to update UI
                    repository.save(it, _photo.value)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun savePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeById(post)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.removeById(post)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}
