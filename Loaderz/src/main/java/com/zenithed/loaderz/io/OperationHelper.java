package com.zenithed.loaderz.io;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;

/**
 * An abstract definition for objects represents responses handlers from various operations.
 */
public abstract class OperationHelper { 
    protected Context mContext;

    protected OperationHelper(Context context) {
        mContext = context;
    }

    public abstract ContentValues [] parse(String reponseString) throws IOException;
}
