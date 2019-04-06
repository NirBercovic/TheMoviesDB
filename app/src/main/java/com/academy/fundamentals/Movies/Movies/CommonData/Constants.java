package com.academy.fundamentals.Movies.Movies.CommonData;

public interface Constants {

    String SPECIFIC_POSITION        = "SP";
    String DATA_SOURCE              = "DS";
    String SPECIFIC_DETAILS         = "SD";
    int SLEEP_INTERVAL              = 500;

    //AsynceTask/Threads
    String FINISH_RUN               = "Done!";
    String CANCEL_RUN               = "Canceled!";
    String INTERRUPT_RUN            = "got interrupted!";
    String RUNNING                  = "Running";
    String READY                    = "Ready";
    String CANCEL                   = "Canceled";
    String FINISH                   = "Finished";
    String PROGRESS_UPDATE_ACTION   = "PROGRESS_UPDATE_ACTION";
    String PROGRESS_VALUE_KEY       = "PROGRESS_VALUE_KEY";
    String SERVICE_STATUS           = "SERVICE_STATUS";
    String ACTION_COUNT_FINISHED    = "count_finished";

    //Internet Service
    String MOVIEDB_API_KEY          = "3bc6d940e31571fcefc4319e550468d5";
    String MOVIEID_PATH_PARAM       = "movie_id";
    String BASE_URL                 = "http://api.themoviedb.org/3/";
    String DB_POPULAR_PATH          = "movie/popular";
    String DB_SEARCH_PATH           = "search/movie";
    String DB_TRAILER_PATH          = "movie/{movie_id}/videos";
    String YOUTUBE_PATH             = "https://www.youtube.com/watch?v=";
    String DB_CONFIGURATION_PATH    = "configuration";
    int MAX_DB_PAGE                 = 1000;
    int MIN_DB_PAGE                 = 1;
    int RESULTS_PER_PAGE            = 20;
    String ENCODED_METHOD           = "UTF-8";

    //Download Service
    String DOWNLOAD_URL             = "URL";
    int ONGOING_NOTIFICATION_ID     = 14000605;
    String BROADCAST_ACTION         = "com.academy.fundamentals.DOWNLOAD_COMPLETE";
    String MOVIE_DETAILS_EXTRA      = "MovieDetailsExtra";
    String MOVIE_POSTER_PATH_EXTRA  = "MoviePosterURLExtra";
    String CHANNEL_ID               = "Movie Details Application";

    //internal DB strings
    String DATABASE_NAME                                        = "movies.db";
    String QUERY_SELECT_MOVIES_AND_ORDER_BY_POPULARITY          = "SELECT * FROM MovieDetails ORDER BY popularity DESC";
    String QUERY_DELETE_ALL_MOVIES                              = "DELETE FROM MovieDetails";
    String QUERY_SELECT_TWENTY_MOVIES_AND_ORDER_BY_POPULARITY   = "SELECT * FROM MovieDetails ORDER BY popularity DESC LIMIT :numberOfRows OFFSET :fromRow";
    String QUERY_SELECT_TRAILER_OF_MOVIEID                      = "SELECT * FROM VideoTrailer WHERE movieId = :movieId";
    String QUERY_DELETE_ALL_TRAILERS                            = "DELETE FROM VideoTrailer";

    //swipe configuration
    int SWIPE_THRESHOLD             = 100;
    int SWIPE_VELOCITY_THRESHOLD    = 100;

    //SharedPrefs
    String LAST_PAGE_OBSERVED       = "last page user observed";
    String PREFS_NAME               = "Shared Preferences";
    String WISHLIST                 = "wishlist";
    String WISHLIST_TITLE           = "My Wishlist";

    //Wishlist actions
    String WISHLIST_ADD             = " added to wishlist";
    String WISHLIST_EXIST           = " already in wishlist";
    String WISHLIST_REMOVE          = " removed from wishlist";

    //download permission dialog
    String PERMISSION_DIALOG_BUTTON_TEXT_GOT_IT       = "OK";
    String PERMISSION_DIALOG_BUTTON_TEXT_CANCEL       = "Cancel";
    String PERMISSION_DIALOG_MESSAGE                  = "Saving any movie posters to your phone needs a download agreement";
    String PERMISSION_DIALOG_TITLE                    = "Dear User";
    int PERMISSIONS_REQUEST_CODE                      = 42;

    //Notification
    String NOTIFICATION_CHANNEL_NAME                  = "The Movie DB";
    String NOTIFICATION_CHANNEL_DESCRIPTION           = "Movie DB Application notification options";
    int NOTIFICATION_PROGRESS_MAX                     = 100;

    //Download errors
    String SAVE_FILE_FAILED                           = "Save file failed";
    String DOWNLOAD_FILE_FAILED                       = "Download file failed";
    String CREATE_FILE_FAILED                         = "Create file failed";



}
