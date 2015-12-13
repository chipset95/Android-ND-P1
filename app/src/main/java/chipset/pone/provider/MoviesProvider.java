package chipset.pone.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import chipset.pone.contracts.MoviesContract;
import chipset.pone.helpers.MoviesDBHelper;

/**
 * Developer: chipset
 * Package : chipset.pone.contracts
 * Project : Popular Movies
 * Date : 13/12/15
 */
public class MoviesProvider extends ContentProvider {

    private SQLiteDatabase mWritableDatabase, mReadableDatabase;

    @Override
    public boolean onCreate() {
        MoviesDBHelper mOpenHelper = new MoviesDBHelper(getContext());
        mWritableDatabase = mOpenHelper.getWritableDatabase();
        mReadableDatabase = mOpenHelper.getReadableDatabase();
        Log.d("Create", "Provider");
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mReadableDatabase.query(MoviesContract.MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnUri;
        long _id = mWritableDatabase.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
        if (_id > 0) {
            returnUri = MoviesContract.MoviesEntry.buildUri(_id);
            Log.d("Inserted At", returnUri.toString());
        } else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return mWritableDatabase.delete(MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mWritableDatabase.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                selectionArgs);
    }
}
