package com.iamdevnitesh.anpr.adapter;


import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static com.squareup.picasso.Picasso.LoadedFrom.NETWORK;

public class FireBaseRequestHandler extends RequestHandler {

    private static final String SCHEME_FIREBASE_STORAGE = "gs";

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_FIREBASE_STORAGE.equals(scheme) );
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {

        StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(request.uri.toString());

        StreamDownloadTask mStreamTask;

        InputStream inputStream ;

        mStreamTask = gsReference.getStream();

        try {
            inputStream = Tasks.await(mStreamTask).getStream();
            Log.i("FireBaseRequestHandler", "Loaded " + gsReference.getPath() );
            return new Result(BitmapFactory.decodeStream(inputStream), NETWORK);
        } catch (ExecutionException | InterruptedException e) {
            throw new IOException();
        }

    }
}
