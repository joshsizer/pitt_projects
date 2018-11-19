package cs445.a4;


/**
 * This abstract data type represents the backend for a streaming radio service.
 * It stores the songs, stations, and users, as well as the
 * ratings that users assign to songs. Songs are stored in the streaming
 * radio service in a 'library', and a song must be added to the library
 * before it can be added to a station. The library is a collection of unique
 * songs stored within this streaming radio service (no duplicate songs
 * allowed). Stations serve a song play-lists, and play-lists can have
 * repeat songs within them.
 */
public interface StreamingRadio {

    /**
     * Adds a new song to the 'library'. If the song is not in the library, it
     * will be added. If the song is already in the library, this method will
     * throw an IllegalArgumentException.
     *
     * @param theSong the song that should be added to the 'library'
     * @throws IllegalArgumentException if the song is already in the 'library'
     * @throws NullPointerException     if the song is null
     */
    void addSong(Song theSong) throws IllegalArgumentException,
            NullPointerException;

    /**
     * Removes an existing song from the 'library'. If the song is in the
     * 'library', then it is removed and returned. If it is not in the
     * 'library', this method will throw a NoSuchSongException.
     *
     * @param theSong the song that should be removed from this system
     * @return The song that is removed
     * @throws NoSuchSongException  if the song is not already in the 'library'
     * @throws NullPointerException if the song is null
     */
    Song removeSong(Song theSong)
            throws NoSuchSongException, NullPointerException;

    /**
     * Adds an existing song in the 'library' to the given playlist for an
     * existing radio station. If the song exists in the 'library' and the
     * station exists in the streaming radio service, then the song will be
     * added to the station. If the song does not exist in the 'library',
     * then a NoSuchSongException is thrown. If the song exists but the
     * station does not, a NoSuchStationException is thrown. If both the song
     * does not exist in the 'library' and the station does not exist, then a
     * NoSuchSongException is thrown.
     *
     * @param theSong    the song in the system's library to add to the provided
     *                   station
     * @param theStation the station to add the song to
     * @throws NoSuchSongException    if the song does not exist within the
     *                                library or both the song and the station do not exist
     * @throws NoSuchStationException if the song exists in the system but the
     *                                station does not
     * @throws NullPointerException   if the song or the station is null
     */
    void addToStation(Song theSong, Station theStation)
            throws NoSuchSongException, NoSuchStationException, NullPointerException;

    /**
     * Removes a song from the playlist for a radio station. If the song
     * exists in the 'library', the station exists in the system, and the
     * station contains the song, then the
     * song is removed from the station and the song is returned. If the song
     * exists in the 'library', the station exists in the system, but the
     * station does not contain the song, an IllegalArgumentException is thrown.
     * If the station does not exist within the system, a NoSuchStationException is
     * thrown. If the station exists but the song does not exist in the
     * station, then a NoSuchSongException is thrown. If both the station and
     * the song do not exist, a NoSuchStationException is thrown.
     *
     * @param theSong    the song to remove from the station
     * @param theStation the station from which the song should be removed
     * @return the song that is removed from the station
     * @throws NoSuchStationException   if the station does not exist within
     *                                  the system or if the song and the
     *                                  station do not exist within the system
     * @throws NoSuchSongException      if the song does not exist within the station
     * @throws IllegalArgumentException if the song exists in the 'library',
     *                                  the station exists within the system, but the station does not contain
     *                                  the song.
     * @throws NullPointerException     if the song or the station is null
     */
    Song removeFromStation(Song theSong, Station theStation)
            throws NoSuchStationException, NoSuchSongException,
            NullPointerException, IllegalArgumentException;

    /**
     * Sets a user's rating for a song, as a number of stars from 1 to 5. If
     * the rating entered is not greater than or equal to 1 and less than or
     * equal to 5, an IllegalArgumentException is thrown. If the song does
     * not exist within the 'library', then a NoSuchSongException is thrown.
     * If the user does not exist within the system, a NoSuchUserException is
     * thrown. If both the song and the user do not exist within the system,
     * then a NoSuchUserException is thrown.
     *
     * @param theUser the user to rate the song
     * @param theSong the song to rate
     * @throws NoSuchUserException  if the user does not exist within the
     *                              system or the user and the song do not exist.
     * @throws NoSuchSongException  if the user exists but the song does not
     * @throws NullPointerException if the user or the song are null
     */
    void rateSong(User theUser, Song theSong, int rating)
            throws IllegalArgumentException, NoSuchUserException,
            NoSuchSongException, NullPointerException;

    /**
     * Clears a user's rating on a song. If this user has rated this song and
     * the rating has not already been cleared, then the rating is cleared and
     * the state will appear as if the rating was never made. If there is no
     * such rating on record (either because this user has not rated this song,
     * or because the rating has already been cleared), then this method will
     * throw an IllegalArgumentException. If the user does not exist within
     * the system, a NoSuchUserException is thrown. If the song does not
     * exist within the 'library', a NoSuchSongException is thrown. If both
     * the user and the song do not exist, then a NoSuchUserException is thrown.
     *
     * @param theUser user whose rating should be cleared
     * @param theSong song from which the user's rating should be cleared
     * @throws IllegalArgumentException if the user does not currently have a
     *                                  rating on record for the song
     * @throws NoSuchUserException      if the user exists but the song does not,
     *                                  or if the user and the song do not exist
     * @throws NoSuchSongException      if the user exists but the song does not
     * @throws NullPointerException     if either the user or the song is null
     */
    void clearRating(User theUser, Song theSong)
            throws IllegalArgumentException, NoSuchUserException,
            NoSuchSongException, NullPointerException;

    /**
     * Predicts the rating a user will assign to a song that they have not yet
     * rated, as a number of stars from 1 to 5. If the song has already been
     * rated, this method will throw an IllegalArgumentException. If the user
     * exists but the song does not, then a NoSuchSongException is thrown. If
     * the user does not exist and the song does, or if both the user and the
     * song do not exist, a NoSuchUserException is thrown.
     *
     * @param theUser the user to associate a songs's predicted rating with.
     * @param theSong the song to predict the rating for
     * @return the predicted rating
     * @throws IllegalArgumentException if the user has already rated this song
     * @throws NoSuchUserException      if the user does not exist and the song
     *                                  does, or if the user and the song do not exist
     * @throws NoSuchSongException      if the user exists but the song does not
     * @throws NullPointerException     if the user or the song is null
     */
    int predictRating(User theUser, Song theSong)
            throws IllegalArgumentException, NoSuchUserException,
            NoSuchSongException, NullPointerException;

    /**
     * Suggests a song for a user that they are predicted to like. If the
     * user does not exist, a NoSuchUserException is thrown. A song will
     * always be reccommended, even if the user has never rated any songs.
     *
     * @param theUser the user to suggest a song for
     * @return the suggested song
     * @throws NoSuchUserException  if the user does not exist within the system
     * @throws NullPointerException if the user is null
     */
    Song suggestSong(User theUser) throws NoSuchUserException, NullPointerException;

}

