package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract;

import static com.example.android.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.example.android.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRODUCT_NAME;
import static com.example.android.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.example.android.bookstore.data.BookContract.BookEntry._ID;


public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int bookQuantity = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOK_QUANTITY));
        final int bookId = cursor.getInt(cursor.getColumnIndex(_ID));
        TextView bookNameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView = view.findViewById(R.id.book_price);
        final TextView quantityTextView = view.findViewById(R.id.book_quantity);
        Button sell = view.findViewById(R.id.sell_button);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);
                ContentValues values = new ContentValues();
                int updatedQuantity;
                if (bookQuantity > 0) {
                    updatedQuantity = bookQuantity - 1;
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, updatedQuantity);
                    String selection = BookContract.BookEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(bookId)};
                    int updatedRows = context.getContentResolver().update(currentBookUri, values, selection, selectionArgs);
                    Toast.makeText(context, context.getString(R.string.quantity_updated) + updatedQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.re_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PRODUCT_NAME));
        String price = String.format(context.getString(R.string.priceTextView), cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PRICE)));
        if (bookQuantity > 0) {
            String quantity = String.format(context.getString(R.string.we_have), cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_QUANTITY)));
            quantityTextView.setText(quantity);
        } else {
            quantityTextView.setText(R.string.don_t_have);
        }
        bookNameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
    }
}