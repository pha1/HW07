package edu.uncc.hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import edu.uncc.hw07.databinding.FragmentCreateForumBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateForumFragment extends Fragment {

    FragmentCreateForumBinding binding;

    private FirebaseAuth mAuth;
    final String TAG = "test";

    public CreateForumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cancel
        binding.textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancel();
            }
        });

        // Create New Forum
        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String forum_title = binding.editTextForumTitle.getText().toString();
                String forum_desc = binding.editTextForumDesc.getText().toString();

                if (forum_title.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();
                } else if (forum_desc.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    String creator_id = mAuth.getCurrentUser().getUid();
                    String name = mAuth.getCurrentUser().getDisplayName();

                    // Create Forum
                    createNewForum(creator_id, forum_title, forum_desc, name);
                }
            }
        });
    }

    /**
     * This method creates a new Forum using the data provided by the user
     * @param creator_id the current user's id
     * @param forum_title the Forum title provided by the user
     * @param forum_desc the Forum description provided by the user
     * @param name the name of the user
     */
    private void createNewForum(String creator_id, String forum_title, String forum_desc, String name) {
        // Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current date and time
        // Format it to a String
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        // Create the Forum Object
        HashMap<String, Object> forum = new HashMap<>();
        forum.put("creator_id", creator_id);
        forum.put("creator_name", name);
        forum.put("description", forum_desc);
        forum.put("title", forum_title);
        forum.put("createdAt", date);
        forum.put("likes", 0);

        // Collection Reference of "forums" collection
        CollectionReference collRef = db.collection("forums");

        // ID of the newly created document
        String forum_id = collRef.document().getId();

        // Store the id into the document
        forum.put("forum_id", forum_id);

        // Set the data into the document created using its id to locate it
        db.collection("forums").document(forum_id)
                .set(forum)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: ");
                        // Go to Forums once created
                        mListener.goToForums();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateForumFragmentListener) {
            mListener = (CreateForumFragmentListener) context;
        }
    }

    CreateForumFragmentListener mListener;

    public interface CreateForumFragmentListener {
        void cancel();
        void goToForums();
    }
}