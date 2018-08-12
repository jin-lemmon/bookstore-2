package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentBookUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private TextView mQuantityTextView;
    private int mQuantity = 0;
    private static final int EXISTING_BOOK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.Detail_add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.Detail_edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        mNameEditText = findViewById(R.id.product_name);
        mPriceEditText = findViewById(R.id.price);
        mSupplierNameEditText = findViewById(R.id.supplier_name);
        mQuantityTextView = findViewById(R.id.quantity);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);
        Button validate = findViewById(R.id.validate_book);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBook();
            }
        });
        Button minus = findViewById(R.id.buttonMinus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityMinus();
            }
        });
        Button plus = findViewById(R.id.buttonPlus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityPlus();
            }
        });
        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });
        final ImageButton callSupplier = findViewById(R.id.call_supplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupplierPhone();
            }
        });
    }

    private void callSupplierPhone() {
        Uri supp_phone = Uri.parse("tel:" + mSupplierPhoneEditText.getText());
        Intent intent = new Intent(Intent.ACTION_DIAL, supp_phone);
        startActivity(intent);
    }

    private void deleteBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_book);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mCurrentBookUri != null) {
                    int rowsAffected = getContentResolver().delete(mCurrentBookUri, null, null);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void quantityPlus() {
        mQuantity += 1;
        updateQuantity();
    }

    private void quantityMinus() {
        if (mQuantity > 0) {
            mQuantity -= 1;
            updateQuantity();
        }
    }

    private void updateQuantity() {
        mQuantityTextView.setText(mQuantity + "");
    }

    private void eraseText() {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    private void validateBook() {
        String bookName = mNameEditText.getText().toString().trim();
        String bookSupplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        String bookSupplier = mSupplierNameEditText.getText().toString().trim();
        String bookPrice = mPriceEditText.getText().toString().trim();
        long price = 0;
        if (!TextUtils.isEmpty(bookPrice)) {
            price = Long.parseLong(mPriceEditText.getText().toString().trim());
        }
        BookDbHelper mDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, bookSupplier);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, bookSupplierPhone);
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(bookName) || TextUtils.isEmpty(bookPrice)) {
            Toast.makeText(this, getString(R.string.missing_fields),
                    Toast.LENGTH_SHORT).show();
        } else {
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_book_failed),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.insert_book_success,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_book_success),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        return new CursorLoader(DetailActivity.this, mCurrentBookUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            long price = cursor.getLong(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityTextView.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplier);
            mSupplierPhoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        eraseText();
    }
}