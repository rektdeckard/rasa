package com.tobiasfried.brewkeeper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tobiasfried.brewkeeper.Brew;
import com.tobiasfried.brewkeeper.data.BrewContract.BasicEntry;
import com.tobiasfried.brewkeeper.data.BrewContract.BrewEntry;
import com.tobiasfried.brewkeeper.data.BrewContract.IngredientEntry;

import java.util.List;

/**
 * {@link ContentProvider} for BrewKeeper app.
 */
public class BrewProvider extends ContentProvider {

    public static final String LOG_TAG = BrewProvider.class.getSimpleName();

    /**
     * URI matcher codes for the content URIs
     */
    private static final int RECIPES = 100;
    private static final int RECIPE_ID = 101;
    private static final int BREWS = 200;
    private static final int BREW_ID = 201;
    private static final int INGREDIENTS = 300;
    private static final int INGREDIENT_ID = 301;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_RECIPES, RECIPES);
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_RECIPES + "/#", RECIPE_ID);
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_BREWS, BREWS);
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_BREWS + "/#", BREW_ID);
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_INGREDIENTS, INGREDIENTS);
        sUriMatcher.addURI(BrewContract.CONTENT_AUTHORITY, BrewContract.PATH_INGREDIENTS + "/#", INGREDIENT_ID);

    }

    /**
     * DB Helper Object
     **/
    private BrewDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BrewDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database for query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Direct query based on URI code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                cursor = db.query(BasicEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECIPE_ID:
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BasicEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BREWS:
                cursor = db.query(BrewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BREW_ID:
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BrewEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INGREDIENTS:
                cursor = db.query(IngredientEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INGREDIENT_ID:
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(IngredientEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default: throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        // Set notification URI on the Cursor, so we can know when its data has changed
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Check for values
        if (values == null) {
            return null;
        }

        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return insertRecipe(uri, values);
            case BREWS:
                return insertBrew(uri, values);
            case INGREDIENTS:
                return insertIngredient(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for URI: " + uri);
        }
    }

    private Uri insertIngredient(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = -1;

        // Check if ingredient is in table
        String name = values.getAsString(BasicEntry.COLUMN_NAME);
        String[] selectionArgs = {name};
        Cursor cursor = db.query(IngredientEntry.TABLE_NAME, null, BasicEntry.COLUMN_NAME + "=?", selectionArgs, null, null, null);
        if (cursor == null) {
            id = db.insert(IngredientEntry.TABLE_NAME, null, values);
        } else {
            try {
                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndex(IngredientEntry.COLUMN_INGREDIENT_ID));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.i ("insertIngredient", "Nothing in the cursor??");
            }
        }
        cursor.close();
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertBrew(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;

        id = db.insert(BrewEntry.TABLE_NAME, null, values);
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertRecipe(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;

        id = db.insert(BasicEntry.TABLE_NAME, null, values);
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Delete from database
        int deleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                // Delete all rows that match the selection and selection args
                deleted = database.delete(BasicEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_ID:
                // Delete a single row given by the ID in the URI
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleted = database.delete(BasicEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BREWS:
                // Delete all rows that match the selection and selection args
                deleted = database.delete(BrewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BREW_ID:
                // Delete a single row given by the ID in the URI
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleted = database.delete(BrewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENTS:
                // Delete all rows that match the selection and selection args
                deleted = database.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENT_ID:
                // Delete a single row given by the ID in the URI
                selection = BasicEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleted = database.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Notify all listeners that the data at this URI has changed
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
