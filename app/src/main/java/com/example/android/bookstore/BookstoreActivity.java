package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bookstore.data.BookContract.BookEntry;

import static com.example.android.bookstore.data.BookContract.BookEntry.CONTENT_URI;

public class BookstoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookCursorAdapter mCursorAdapter;
    private static final int BOOK_STORE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookstore);
        Button insertBook = findViewById(R.id.insert_book);
        insertBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookstoreActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
        ListView bookListView = findViewById(R.id.inventory);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookstoreActivity.this, DetailActivity.class);
                Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(bookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_STORE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bookstore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_the_mummy_data:
                insertMummyData();
                return true;
            case R.id.fahreneit451:
                fahrenheit451();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();
    }

    private void insertMummyData() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, "The Mummy");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "120");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, "27081999");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Brendan Fraser");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "68669");
        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
    }

    private void fahrenheit451() {
        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};
        return new CursorLoader(BookstoreActivity.this, BookEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}