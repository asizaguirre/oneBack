package br.com.oneback.commons;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class LibraryClass {
    private static DatabaseReference firebase;

    private LibraryClass() {
    }

    public static DatabaseReference getFirebase() {
        if (firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return (firebase);
    }
}